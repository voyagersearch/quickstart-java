package voyager.quickstart.location.folder;

import org.apache.solr.client.solrj.SolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import voyager.api.discovery.jobs.JobSubmitter;
import voyager.discovery.location.BaseDiscoveryRunner;

public class MyFolderRunner extends BaseDiscoveryRunner<MyFolderLocation> {
  static final Logger log = LoggerFactory.getLogger(MyFolderRunner.class);
  
  public MyFolderRunner( MyFolderLocation loc, SolrClient solr, JobSubmitter jobs) {
    super(loc, solr, jobs);
  }

  //----------------------------------------------------
  //----------------------------------------------------
  
  @Override
  protected void doCrawl() throws Exception {
    throw new RuntimeException("not implemented yet");
  }
}