package voyager.quickstart.location.earthquake;

import org.apache.solr.client.solrj.SolrServer;

import voyager.api.discovery.jobs.JobSubmitter;
import voyager.discovery.location.service.ServiceDiscoveryRunner;
import voyager.http.VoyagerHttpClient;
import voyager.http.XmlHttpParser;
import voyager.plugin.csw.extractor.CSWParser;
import voyager.plugin.csw.extractor.SimpleCSWParser;

public class EarthquakesDiscoveryRunner extends ServiceDiscoveryRunner<EarthquakesLocation> {
  
  final VoyagerHttpClient client;
  final XmlHttpParser xclient;
  final CSWParser parser;


  public EarthquakesDiscoveryRunner( EarthquakesLocation loc, SolrServer solr, JobSubmitter jobs) {
    super(loc, solr, jobs);
    xclient = new XmlHttpParser();
    parser = new SimpleCSWParser();
    client = VoyagerHttpClient.getInstance();
  }

  //----------------------------------------------------
  //----------------------------------------------------

  @Override
  protected void doCrawl() throws Exception {
    
  }
}