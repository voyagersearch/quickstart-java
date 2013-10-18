package voyager.quickstart.query;

import java.net.URL;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.geo.GeoHelper;
import org.apache.abdera.ext.geo.Position;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.parser.Parser;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;

import voyager.api.domain.model.entry.DexField;
import voyager.quickstart.IntegrationBase;

/**
 * TODO: theses tests need to make sure something is in the index
 * so we can assert that the queries work.  For now they simply 
 * show the plumbing
 */
public class SampleQueryIntegrationTest extends IntegrationBase {

  /**
   * A simple sample using solrj
   */
  @Test
  public void simpleQueryWithSolrj() throws Exception {
    SolrQuery query = new SolrQuery();
    query.setQuery("*:*");
    QueryResponse rsp = solr.query(query);
    SolrDocumentList docs = rsp.getResults();
    System.out.println( "Found: "+docs.getNumFound() );
    for(SolrDocument doc : docs) {
      System.out.println( "Doc: "+doc.get("id") 
          + "\t: " + doc.get(DexField.NAME.name)
          + "\t: " + doc.get(DexField.BBOX.name));
    }
  }
  

  /**
   * A simple example using Abdera
   */
  @Test
  public void simpleOpensearchQuery() throws Exception {
    
    String feedURL = baseURL + "feed/atom.xml";
    
    Abdera abdera = new Abdera();
    Parser parser = abdera.getParser();
    
    URL url = new URL(feedURL);
    Document<Feed> doc = parser.parse(url.openStream(), url.toString());
    Feed feed = doc.getRoot();
    // Get the feed title
    System.out.println("Feed Title: " + feed.getTitle());

    // Get the entry items...
    for (Entry entry : feed.getEntries()) {
      System.out.println("------");
      System.out.println("Title: " + entry.getTitle());
      System.out.println("Unique Identifier: " + entry.getId().toString());
      System.out.println("Updated Date: " + entry.getUpdated().toString());
      System.out.println("Published Date: " + entry.getPublished());

      // Get the links
      for (Link link : entry.getLinks()) {
        System.out.println("Link: " + link.getHref());
      }       

      // Read the position 
      for(Position p : GeoHelper.getPositions(entry)) {
        System.out.println( "Position: "+p);
      }
    }
  }
}
