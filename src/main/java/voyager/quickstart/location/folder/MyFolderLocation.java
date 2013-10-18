package voyager.quickstart.location.folder;

import voyager.api.discovery.location.Location;
import voyager.api.infrastructure.json.JSONArray;
import voyager.api.infrastructure.json.JSONException;
import voyager.api.infrastructure.json.JSONObject;
import voyager.api.infrastructure.json.JSONWriter;

public class MyFolderLocation extends Location {

  public static final String TYPE = "myfolder";
  
  private String path;
  private String[] keywords = null;
  
  public MyFolderLocation() {
    
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

  public String[] getKeywords() {
    return keywords;
  }

  public void setKeywords(String[] keywords) {
    this.keywords = keywords;
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
    if(keywords!=null && keywords.length>0) {
      jj.key("keywords").array();
      for(String k : keywords) {
        jj.value(k);
      }
      jj.endArray();
    }
  }

  @Override
  public MyFolderLocation readJSON(JSONObject json) throws JSONException {
    super.readJSON(json);
    setPath( json.optString("path", null));
    
    JSONArray arr = json.optJSONArray("keywords");
    if(arr==null) {
      keywords = null;
    }
    else {
      keywords = new String[arr.length()];
      for(int i=0; i<arr.length(); i++) {
        keywords[i] = arr.getString(i);
      }
    }
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
