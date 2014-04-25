package voyager.quickstart.location.autonomy;

import voyager.api.discovery.location.service.ServiceLocation;

public class AutonomyLocation extends ServiceLocation {

  public static final String TYPE = "autonomy";
  
  public AutonomyLocation() {
    
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
