package voyager.quickstart.location.autonomy;

import java.net.URI;

import org.apache.solr.client.solrj.impl.HttpSolrServer;

import voyager.api.discovery.jobs.JobSubmitter;

public class AutonomyTester 
{
  public static void main(String[] args) throws Exception
  {
    AutonomyLocation loc = new AutonomyLocation();
    loc.setURI(new URI("http://voyagerdemo.com/Autonomy"));
    loc.setPageSize(5);
    
    HttpSolrServer solr = null; // NOTE, not actually used.  new HttpSolrServer("http://localhost:7777/solr/v0");
    JobSubmitter jobs = new DummyJobSubmitter();
    // new ZmqJobSubmitter("tcp://127.0.0.1:7100");
    
    AutonomyRunner runner = new AutonomyRunner(loc, solr, jobs);
    
    runner.run();
    
    System.out.println("done.");
  }
}
