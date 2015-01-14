package voyager.quickstart;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.junit.After;
import org.junit.Before;

/**
 * Integration test requires a running Voyager instance
 */
public abstract class IntegrationBase {

  protected String baseURL;
  protected SolrClient solr;
  protected DefaultHttpClient httpclient;
  
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
    solr = new HttpSolrClient(baseURL+"solr/v0");

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
}
