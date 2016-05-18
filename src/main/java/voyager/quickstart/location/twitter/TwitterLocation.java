package voyager.quickstart.location.twitter;

import voyager.api.discovery.location.service.ServiceLocation;
import voyager.api.infrastructure.json.JSONException;
import voyager.api.infrastructure.json.JSONObject;
import voyager.api.infrastructure.json.JSONWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TwitterLocation extends ServiceLocation {

  static final Logger log = LoggerFactory.getLogger(TwitterLocation.class);

  public static final String TYPE = "tweets";
  private String oAuthAccessToken;
  private String oAuthAccessTokenSecret;
  private String oAuthConsumerKey;
  private String oAuthConsumerSecret;
  private String user;



  
  public TwitterLocation() {

  }

  @Override
  protected void fillJSONBody(JSONWriter jj) throws JSONException {
    super.fillJSONBody(jj);
    jj.key("oAuthConsumerKey").value(oAuthConsumerKey);
    jj.key("oAuthConsumerSecret").value(oAuthConsumerSecret);
    jj.key("oAuthAccessToken").value(oAuthAccessToken);
    jj.key("oAuthAccessTokenSecret").value(oAuthAccessTokenSecret);
    jj.key("user").value(user);
  }

  @Override
  public TwitterLocation readJSON(JSONObject json) throws JSONException {
    super.readJSON(json);
    oAuthConsumerKey = json.optString("oAuthConsumerKey", "");
    oAuthConsumerSecret = json.optString("oAuthConsumerSecret", "");
    oAuthAccessToken = json.optString("oAuthAccessToken", "");
    oAuthAccessTokenSecret = json.optString("oAuthAccessTokenSecret", "");
    user = json.optString("user", "");


    //log.info("FYI, twitterApiKey was: {}", twitterApiKey);
    return this;
  }


  public String getOAuthConsumerKey() {
    return oAuthConsumerKey;
  }

  public void setOAuthConsumerKey(String oAuthConsumerKey) {
    this.oAuthConsumerKey = oAuthConsumerKey;
  }

  public String getOAuthConsumerSecret() {
    return oAuthConsumerSecret;
  }

  public void setOAuthConsumerSecret(String oAuthConsumerSecret) {
    this.oAuthConsumerSecret = oAuthConsumerSecret;
  }

  public String getOAuthAccessToken() {
    return oAuthAccessToken;
  }

  public void setOAuthAccessToken(String oAuthAccessToken) {
    this.oAuthAccessToken = oAuthAccessToken;
  }

  public String getOAuthAccessTokenSecret() {
    return oAuthAccessTokenSecret;
  }

  public void setOAuthAccessTokenSecret(String oAuthAccessTokenSecret) {
    this.oAuthAccessTokenSecret = oAuthAccessTokenSecret;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
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
