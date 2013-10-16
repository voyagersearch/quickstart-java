package voyager.quickstart.location.earthquake;

import java.net.URI;
import java.net.URISyntaxException;

import com.google.common.base.Throwables;

import voyager.api.discovery.location.service.ServiceLocation;

public class EarthquakesLocation extends ServiceLocation {

  public static final String TYPE = "earthquakes";
  
  public EarthquakesLocation() {
    // See others: http://earthquake.usgs.gov/earthquakes/feed/v1.0/atom.php
    try {
      setURI(new URI("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.atom"));
    }
    catch(URISyntaxException ex) {
      Throwables.propagate(ex);
    }
  }
  
  @Override
  public String getLocationDisplay() {
    return getName();
  }

  @Override
  public String getLocationType() {
    return TYPE;
  }
}
