package voyager.quickstart.location.earthquake;

import voyager.api.discovery.location.service.ServiceLocation;

public class EarthquakesLocation extends ServiceLocation {

  public static final String TYPE = "earthquakes";
  
  public EarthquakesLocation() {
    
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
