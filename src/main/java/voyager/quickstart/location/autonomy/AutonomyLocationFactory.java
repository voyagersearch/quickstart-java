package voyager.quickstart.location.autonomy;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.solr.client.solrj.SolrServer;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;

import voyager.api.discovery.DiscoveryRunner;
import voyager.api.discovery.jobs.JobSubmitter;
import voyager.api.discovery.location.LocationFactory;
import voyager.api.discovery.location.service.ServiceLocation;
import voyager.common.util.Registry;

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
    // See others: http://earthquake.usgs.gov/earthquakes/feed/v1.0/atom.php
    try {
      AutonomyLocation loc = new AutonomyLocation();
      loc.setURI(new URI("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.atom"));
      return loc;
    }
    catch(URISyntaxException ex) {
      Throwables.propagate(ex);
    }
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
