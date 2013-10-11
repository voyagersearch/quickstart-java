package voyager.quickstart.discovery;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import voyager.api.discovery.jobs.DiscoveryJob;
import voyager.api.domain.model.entry.DexField;
import voyager.api.infrastructure.json.JSONObject;

/**
 * This test hits a live Voyager HTTP Server
 */
public class RestIntegrationTest {
  
  String baseURL;
  DefaultHttpClient httpclient;
  
  @Before
  public void initClient()
  {
    baseURL = System.getProperty("voyager.url");
    if(baseURL==null) {
      baseURL = "http://localhost:7777/";
    }
    if(!baseURL.endsWith("/")) {
      baseURL += "/";
    }

    httpclient = new DefaultHttpClient();
    httpclient.getCredentialsProvider().setCredentials(
        AuthScope.ANY,
        // Add the default credentials
        new UsernamePasswordCredentials("admin", "admin"));
  }
  
  @After
  public void shutdownClient() {
    httpclient.getConnectionManager().shutdown();
    httpclient = null;
  }
  
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
  private void postJobCommitAndVerifyIndex(DiscoveryJob job) throws Exception
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
    
    // 2. send a 'commit' command so that the item is visible with a search
    body = new StringEntity(JobSamples.makeCommitJob().toJSON(), ContentType.APPLICATION_JSON);
    httppost.setEntity(body);
    httpclient.execute(httppost, responseHandler);
    
    // 3. Now make sure we get it from the index
    HttpGet httpget = new HttpGet(baseURL+"/api/rest/index/record/"+job.getId());
    rsp = httpclient.execute(httpget, responseHandler);
    json = new JSONObject(rsp);
    System.out.println("Confirm: "+json.toString(2));
    Assert.assertEquals(job.getId(), json.getString("id"));
    
    Object val = json.opt(DexField.INDEXING_ERROR.name);
    if(val!=null) {
      Assert.fail("Indexed document has errors: "+val);
    }
  }
  

  @Test
  public void testPostDocumentsViaHTTP() throws Exception 
  {
    DiscoveryJob job = JobSamples.makeAddSimpleRectord();
    postJobCommitAndVerifyIndex(job);
   
    // Add a 
    job = JobSamples.makeAddRecordTree();
    postJobCommitAndVerifyIndex(job);
  }
}
