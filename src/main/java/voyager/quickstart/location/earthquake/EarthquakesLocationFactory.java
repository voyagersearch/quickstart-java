package voyager.quickstart.location.earthquake;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServer;

import voyager.api.discovery.DiscoveryRunner;
import voyager.api.discovery.jobs.JobSubmitter;
import voyager.api.discovery.location.LocationFactory;
import voyager.common.util.Registry;
import voyager.extractors.services.wms.DiscoveryRunnerWMS;

public class EarthquakesLocationFactory implements LocationFactory<EarthquakesLocation> {

  @Override
  public String getName() {
    return EarthquakesLocation.TYPE;
  }

  @Override
  public EarthquakesLocation newInstance() throws IllegalStateException {
    return new EarthquakesLocation();
  }

  @Override
  public DiscoveryRunner<?> newRunner(EarthquakesLocation loc, boolean delta) throws IOException {
    SolrServer solr = Registry.get(SolrServer.class);
    JobSubmitter jobs = Registry.get(JobSubmitter.class);
    return new EarthquakesDiscoveryRunner(loc, solr, jobs);
  }

  @Override
  public void validate(EarthquakesLocation loc) throws Exception {
    // TODO: check that the URL is atom?
  }
}
