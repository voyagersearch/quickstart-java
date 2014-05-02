package voyager.quickstart.location.autonomy;

import voyager.api.discovery.location.service.ServiceLocation;
import voyager.api.infrastructure.json.JSONException;
import voyager.api.infrastructure.json.JSONObject;
import voyager.api.infrastructure.json.JSONWriter;

public class AutonomyLocation extends ServiceLocation {

  public static final String TYPE = "autonomy";
  
  private int pageSize = 50;
  
  public AutonomyLocation() {
    
  }
  
  @Override
  protected void fillJSONBody(JSONWriter jj) throws JSONException {
    super.fillJSONBody(jj);
    jj.key("pageSize").value(pageSize);
  }

  @Override
  public AutonomyLocation readJSON(JSONObject json) throws JSONException {
    super.readJSON(json);
    pageSize = json.optInt("pageSize", 50);
    return this;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
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
