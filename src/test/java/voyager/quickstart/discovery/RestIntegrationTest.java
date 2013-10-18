package voyager.quickstart.discovery;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Assert;
import org.junit.Test;

import voyager.api.discovery.jobs.DiscoveryJob;
import voyager.api.domain.model.entry.DexField;
import voyager.api.infrastructure.json.JSONObject;
import voyager.quickstart.IntegrationBase;

/**
 * This test hits a live Voyager HTTP Server
 */
public class RestIntegrationTest extends IntegrationBase {
  
  @Test
  public void testGetDiscoveryStatus() throws Exception 
  {
    String url = baseURL + "api/rest/discovery/status";

    HttpGet httpget = new HttpGet(url);

    System.out.println("[execute] " + httpget.getRequestLine());
    ResponseHandler<String> responseHandler = new BasicResponseHandler();
    String rsp = httpclient.execute(httpget, responseHandler);

    JSONObject json = new JSONObject(rsp);
    //System.out.println("STATUS:"+json.toString(2));
    Assert.assertTrue(json.getBoolean("enabled")); // discovery should be enabled
  }
  
  /**
   * This is a utility class that will take a DiscoveryJob, index it and 
   * verify that it successfully added the document
   */
  private String postJobCommitAndVerifyIndex(DiscoveryJob job) throws Exception
  {
    String postURL = baseURL + "api/rest/discovery/job/index";
    HttpPost httppost = new HttpPost(postURL);
    
    // 1. Send the Job
    StringEntity body = new StringEntity(job.toJSON(), ContentType.APPLICATION_JSON);
    httppost.setEntity(body);
    
    System.out.println("[execute] " + httppost.getRequestLine());
    ResponseHandler<String> responseHandler = new BasicResponseHandler();
    String rsp = httpclient.execute(httppost, responseHandler);

    JSONObject json = new JSONObject(rsp);
    System.out.println("Post Response"+json.toString(2));
    try {
      // Index Jobs will have an entry/fields/id field
      String id = json.getJSONObject("entry").getJSONObject("fields").getString("id");

      // Wait for the results to flush through the system
      solr.commit(true, true);
      
      // Now find the result via REST
      HttpGet httpget = new HttpGet(baseURL+"/api/rest/index/record/"+id);
      rsp = httpclient.execute(httpget, responseHandler);
      json = new JSONObject(rsp);
      System.out.println("Confirm: "+json.toString(2));
      Assert.assertEquals(id, json.getString("id"));
      
      Object val = json.opt(DexField.INDEXING_ERROR.name);
      if(val!=null) {
        Assert.fail("Indexed document has errors: "+val);
      }
      return id;
    }
    catch(Exception ex) {
      // ok
    }
    return null;
  }
  

  @Test
  public void testPostDocumentsViaHTTP() throws Exception 
  {
    // Add a file reference
    postJobCommitAndVerifyIndex(JobSamples.makeAddFile());

    // Add a URL reference
    postJobCommitAndVerifyIndex(JobSamples.makeAddURL());

    // Add a record without file reference
    postJobCommitAndVerifyIndex(JobSamples.makeAddRecordWithoutFile());
   
    // Record with a tree
    postJobCommitAndVerifyIndex(JobSamples.makeAddRecordTree());

    // Record with links to data
    postJobCommitAndVerifyIndex(JobSamples.makeAddRecordWithLinks());
   
    // Add a copy of the tree document so we can remove it
    DiscoveryJob job = JobSamples.makeAddRecordTree();
    job.setId("treecopy");
    postJobCommitAndVerifyIndex(job);
    postJobCommitAndVerifyIndex(JobSamples.makeDelteJob(job.getId()));
    solr.commit(true, true);
    
    // Check that the document (and its children) were removed
    System.out.println( "Check that everythign was removed... "+job.getId() );
    QueryResponse rsp = solr.query(new SolrQuery(DexField.ROOT.name+":"+job.getId()));
    Assert.assertEquals(0, rsp.getResults().getNumFound());
  }
}
