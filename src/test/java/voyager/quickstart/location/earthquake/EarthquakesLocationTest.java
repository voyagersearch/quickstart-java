package voyager.quickstart.location.earthquake;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import voyager.api.domain.model.entry.DexField;
import voyager.jobs.JobSubmitterList;

public class EarthquakesLocationTest {
  
  @Test
  public void testReadIndex() throws Exception {
    JobSubmitterList jobs = new JobSubmitterList();
    SolrServer solr = Mockito.mock(SolrServer.class);
    final List<SolrInputDocument> values = new ArrayList<>();
    Mockito.when(solr.add(Matchers.any(SolrInputDocument.class))).then(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        values.add( (SolrInputDocument)invocation.getArguments()[0] );
        return null;
      }
    });
    
    EarthquakesLocation loc = new EarthquakesLocation();
    loc.setURI(new URI("http://nothing/")); // not used
    EarthquakesDiscoveryRunner runner = new EarthquakesDiscoveryRunner(loc, solr, jobs) {
      @Override
      protected InputStream openStream(URI uri) throws IOException
      {
        return EarthquakesLocationTest.class.getResourceAsStream("all_day.atom");
      }
    };
    
    runner.run();
    Assert.assertEquals(210, values.size());
    
    // make sure everything has a "pointDD" field
    for(SolrInputDocument doc : values) {
      Assert.assertNotNull(doc.getFieldValue(DexField.POINT_DD.name));
    }
  }
}
