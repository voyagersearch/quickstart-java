package voyager.quickstart.location;

import voyager.api.discovery.location.Location;
import voyager.api.discovery.location.LocationType;

public class CustomLocation extends Location {

  public static final String TYPE = "custom";
  
  private String connection;
  
  @Override
  public String getLocationDisplay() {
    return getName();
  }

  @Override
  public String getLocationType() {
    return TYPE;
  }
}
