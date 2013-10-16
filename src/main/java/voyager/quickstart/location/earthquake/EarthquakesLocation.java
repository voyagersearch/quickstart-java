package voyager.quickstart.location.earthquake;

import voyager.api.discovery.location.Location;
import voyager.api.discovery.location.LocationType;

public class EarthquakesLocation extends Location {

  public static final String TYPE = "earthquakes";
  
  // See others: http://earthquake.usgs.gov/earthquakes/feed/v1.0/atom.php
  private String url = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.atom";
  
  @Override
  public String getLocationDisplay() {
    return getName();
  }

  @Override
  public String getLocationType() {
    return TYPE;
  }
}
