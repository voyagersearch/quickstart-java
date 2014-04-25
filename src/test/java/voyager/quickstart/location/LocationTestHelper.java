package voyager.quickstart.location;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import voyager.jobs.JobSubmitterList;

public class LocationTestHelper {
  public JobSubmitterList jobs = new JobSubmitterList();
  public SolrServer solr = Mockito.mock(SolrServer.class);
  final List<SolrInputDocument> values = new ArrayList<>();
  
  public LocationTestHelper() throws Exception {
    Mockito.when(solr.add(Matchers.any(SolrInputDocument.class))).then(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        values.add( (SolrInputDocument)invocation.getArguments()[0] );
        return null;
      }
    });
  }
}
