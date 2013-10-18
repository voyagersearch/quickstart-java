package voyager.quickstart.location.folder;

import org.apache.solr.client.solrj.SolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import voyager.api.discovery.jobs.JobSubmitter;
import voyager.discovery.location.BaseDiscoveryRunner;

public class MyFolderDiscoveryRunner extends BaseDiscoveryRunner<MyFolderLocation> {
  static final Logger log = LoggerFactory.getLogger(MyFolderDiscoveryRunner.class);
  
  public MyFolderDiscoveryRunner( MyFolderLocation loc, SolrServer solr, JobSubmitter jobs) {
    super(loc, solr, jobs);
  }

  //----------------------------------------------------
  //----------------------------------------------------
  
  @Override
  protected void doCrawl() throws Exception {
    throw new RuntimeException("not implemented yet");
  }
}