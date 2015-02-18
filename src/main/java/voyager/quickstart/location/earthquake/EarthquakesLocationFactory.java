package voyager.quickstart.location.earthquake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.solr.client.solrj.SolrClient;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;

import voyager.api.discovery.DiscoveryRunner;
import voyager.api.discovery.jobs.JobSubmitter;
import voyager.api.discovery.location.LocationFactory;
import voyager.api.discovery.location.service.ServiceLocation;
import voyager.api.infrastructure.util.Registry;

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
  public EarthquakesLocation newSampleInstance() {
    // See others: http://earthquake.usgs.gov/earthquakes/feed/v1.0/atom.php
    try {
      EarthquakesLocation loc = new EarthquakesLocation();
      loc.setURI(new URI("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.atom"));
      return loc;
    }
    catch(URISyntaxException ex) {
      Throwables.propagate(ex);
    }
    return null;
  }

  @Override
  public DiscoveryRunner<?> newRunner(EarthquakesLocation loc, boolean delta) throws IOException {
    SolrClient solr = Registry.get(SolrClient.class);
    JobSubmitter jobs = Registry.get(JobSubmitter.class);
    return new EarthquakesRunner(loc, solr, jobs);
  }

  @Override
  public void validate(EarthquakesLocation loc) throws Exception {
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
