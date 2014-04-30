package voyager.quickstart.location.autonomy;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServer;

import voyager.api.discovery.DiscoveryRunner;
import voyager.api.discovery.jobs.JobSubmitter;
import voyager.api.discovery.location.LocationFactory;
import voyager.api.discovery.location.service.ServiceLocation;
import voyager.common.util.Registry;

import com.google.common.base.Strings;

public class AutonomyLocationFactory implements LocationFactory<AutonomyLocation> {

  @Override
  public String getName() {
    return AutonomyLocation.TYPE;
  }

  @Override
  public AutonomyLocation newInstance() throws IllegalStateException {
    return new AutonomyLocation();
  }

  @Override
  public AutonomyLocation newSampleInstance() {
    return null;
  }

  @Override
  public DiscoveryRunner<?> newRunner(AutonomyLocation loc, boolean delta) throws IOException {
    SolrServer solr = Registry.get(SolrServer.class);
    JobSubmitter jobs = Registry.get(JobSubmitter.class);
    return new AutonomyRunner(loc, solr, jobs);
  }

  @Override
  public void validate(AutonomyLocation loc) throws Exception {
    if(loc.getURI()==null) {
      throw new IllegalArgumentException("Missing URI");
    }
    if(Strings.isNullOrEmpty(loc.getId())) {
      loc.setId(ServiceLocation.getIdForURI(loc.getURI()));
    }
    // TODO: check that the URL is actually atom...
    // TODO: set the name from the atom feed?
  }
}
