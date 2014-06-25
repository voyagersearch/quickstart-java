package voyager.quickstart.location.xml;

import voyager.api.discovery.location.Location;
import voyager.api.infrastructure.json.JSONException;
import voyager.api.infrastructure.json.JSONObject;
import voyager.api.infrastructure.json.JSONWriter;

public class XmlFolderScanLocation extends Location {

  public static final String TYPE = "xml-folder-scan";
  
  private String path;
  
  public XmlFolderScanLocation() {
    
  }

  //--------------------------------------------------------------------
  // Beans
  //--------------------------------------------------------------------
  
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  //--------------------------------------------------------------------
  // JSON
  //--------------------------------------------------------------------
  
  @Override
  protected void fillJSONBody(JSONWriter jj) throws JSONException {
    super.fillJSONBody(jj);
    if(path!=null) {
      jj.key( "path" ).value( path );
    }
  }

  @Override
  public XmlFolderScanLocation readJSON(JSONObject json) throws JSONException {
    super.readJSON(json);
    setPath( json.optString("path", null));
    return this;
  }
  
  //------------------------------------------------------
  // Base Location Functions
  //------------------------------------------------------
  
  @Override
  public String getLocationDisplay() {
    return path;
  }

  @Override
  public String getLocationType() {
    return TYPE;
  }
}
