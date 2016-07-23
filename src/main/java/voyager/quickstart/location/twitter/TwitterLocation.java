package voyager.quickstart.location.twitter;


import voyager.api.discovery.location.service.ServiceLocation;
import voyager.api.infrastructure.json.JSONArray;
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


  private String[] feeds;
  private Integer feedsNumberOfTweetsToGet;

  private JSONObject[] locations;
  private Integer locationsNumberOfTweetsToGet;

  private String[] hashTags;
  private Integer hashTagsNumberOfTweetsToGet;
  private Double hashTagsLatitude;
  private Double hashTagsLongitude;
  private Double hashTagsRadius;
  private String hashTagsUnit;

  private Boolean getTrendingTopics = false;
  private Integer trendingTopicsNumberOfTweetsToGet;
  private Double trendingTopicsLatitude;
  private Double trendingTopoicsLongitude;
  private Double trendingTopicsRadius;
  private String trendingTopicsUnit;





  
  public TwitterLocation() {

  }

  @Override
  protected void fillJSONBody(JSONWriter jj) throws JSONException {
    super.fillJSONBody(jj);
    jj.key("oAuthConsumerKey").value(oAuthConsumerKey);
    jj.key("oAuthConsumerSecret").value(oAuthConsumerSecret);
    jj.key("oAuthAccessToken").value(oAuthAccessToken);
    jj.key("oAuthAccessTokenSecret").value(oAuthAccessTokenSecret);


    jj.key("locations").object();

    jj.key("locations").array();

    if(locations !=null && locations.length>0) {

      for (JSONObject k : locations) {
        jj.value(k);
      }





    }
    jj.endArray();
    jj.key("numberOfTweetsToGet").value(locationsNumberOfTweetsToGet);
    jj.endObject();


    if(feeds !=null && feeds.length>0) {
      jj.key("feeds").object();

      jj.key("feeds").array();
      for (String k : feeds) {
        jj.value(k);
      }
      jj.endArray();
      jj.key("numberOfTweetsToGet").value(feedsNumberOfTweetsToGet);

      jj.endObject();

    }

    if(hashTags!=null && hashTags.length>0) {
      jj.key("hashTags").object();
      jj.key("hashTags").array();
      for(String k : hashTags) {
        jj.value(k);
      }
      jj.endArray();

      jj.key("numberOfTweetsToGet").value(hashTagsNumberOfTweetsToGet);
      jj.key("hashTagsLocation").object();
      jj.key("latitude").value(hashTagsLatitude);
      jj.key("longitude").value(hashTagsLongitude);
      jj.key("radius").value(hashTagsRadius);
      jj.key("unit").value(hashTagsUnit);
      jj.endObject();
      jj.endObject();
    }


    jj.key("trendingTopics").object();
    jj.key("getTrendingTopics").value(getTrendingTopics);
    jj.key("numberOfTweetsToGet").value(trendingTopicsNumberOfTweetsToGet);
    jj.key("trendingTopicsLocation").object();
    jj.key("latitude").value(trendingTopicsLatitude);
    jj.key("longitude").value(trendingTopoicsLongitude);
    jj.key("radius").value(trendingTopicsRadius);
    jj.key("unit").value(trendingTopicsUnit);
    jj.endObject();
    jj.endObject();
  }

  @Override
  public TwitterLocation readJSON(JSONObject json) throws JSONException {
    super.readJSON(json);
    oAuthConsumerKey = json.optString("oAuthConsumerKey", "");
    oAuthConsumerSecret = json.optString("oAuthConsumerSecret", "");
    oAuthAccessToken = json.optString("oAuthAccessToken", "");
    oAuthAccessTokenSecret = json.optString("oAuthAccessTokenSecret", "");



    try{

      JSONObject feedsTopObject = json.getJSONObject("feeds");
      JSONArray arr = feedsTopObject.optJSONArray("feeds");
      if(arr==null || arr.length() == 0) {
        feeds = null;
      }
      else {
        feeds = new String[arr.length()];
        for(int i=0; i<arr.length(); i++) {
          feeds[i] = arr.getString(i);
        }
      }
      feedsNumberOfTweetsToGet = feedsTopObject.optInt("numberOfTweetsToGet");


    }
    catch(JSONException je)
    {
      log.info("JSON Error, Error parsing Feeds JSON: {}", je);
    }

    try{

      JSONObject locationsTopObject = json.getJSONObject("locations");
      JSONArray arr = locationsTopObject.optJSONArray("locations");
      if(arr==null || arr.length() ==0) {
        locations = null;
      }
      else {
        locations = new JSONObject[arr.length()];
        for(int i=0; i<arr.length(); i++) {
          locations[i] = arr.getJSONObject(i);
        }
      }
      locationsNumberOfTweetsToGet = locationsTopObject.optInt("numberOfTweetsToGet");

    }
    catch(JSONException je)
    {
      log.info("JSON Error, Error parsing Locations JSON: {}", je);
    }

    try{

      JSONObject hashTagsTopObject = json.getJSONObject("hashTags");
      JSONArray arr = hashTagsTopObject.optJSONArray("hashTags");
      if(arr==null || arr.length() ==0) {
        hashTags = null;
      }
      else {
        hashTags = new String[arr.length()];
        for(int i=0; i<arr.length(); i++) {
          hashTags[i] = arr.getString(i);
        }
      }
      hashTagsNumberOfTweetsToGet = hashTagsTopObject.optInt("numberOfTweetsToGet");
      JSONObject hashTagsLocationObject = hashTagsTopObject.getJSONObject("hashTagsLocation");
      hashTagsLatitude = hashTagsLocationObject.optDouble("latitude",0f);
      hashTagsLongitude = hashTagsLocationObject.optDouble("longitude",0f);
      hashTagsRadius = hashTagsLocationObject.optDouble("radius",0f);
      hashTagsUnit = "miles";

    }
    catch(JSONException je)
    {
      log.info("JSON Error, Error parsing HashTags JSON: {}", je);
    }

    try{

      JSONObject trendingTopicsTopObject = json.getJSONObject("trendingTopics");
      getTrendingTopics = trendingTopicsTopObject.optBoolean("getTrendingTopics");
      trendingTopicsNumberOfTweetsToGet = trendingTopicsTopObject.optInt("numberOfTweetsToGet");
      JSONObject getTrendingTopicsLocationObject = trendingTopicsTopObject.getJSONObject("trendingTopicsLocation");
      trendingTopicsLatitude = getTrendingTopicsLocationObject.optDouble("latitude",0f);
      trendingTopoicsLongitude = getTrendingTopicsLocationObject.optDouble("longitude",0f);
      trendingTopicsRadius = getTrendingTopicsLocationObject.optDouble("radius",0f);
      trendingTopicsUnit = "miles";

    }
    catch(JSONException je)
    {
      log.info("JSON Error, Error parsing Trending Topics JSON: {}", je);
    }


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

  public String[] getFeeds() {
    return feeds;
  }

  public void setFeeds(String[] feeds) {
    this.feeds = feeds;
  }

  public Integer getFeedsNumberOfTweetsToGet(){
    return feedsNumberOfTweetsToGet;
  }

  public void setFeedsNumberOfTweetsToGet(Integer numberOfTweetsToGet){
    this.feedsNumberOfTweetsToGet = numberOfTweetsToGet;
  }

  public JSONObject[] getLocations() {
    return locations;
  }
  public void setLocations(JSONObject[] locations) {
    this.locations = locations;
  }

  public Integer getLocationsNumberOfTweetsToGet(){
    return locationsNumberOfTweetsToGet;
  }
  public void setLocationsNumberOfTweetsToGet(Integer numberOfTweetsToGet){
    this.locationsNumberOfTweetsToGet = numberOfTweetsToGet;
  }

  public String[] getHashTags() {
    return hashTags;
  }

  public Integer getHashTagsNumberOfTweetsToGet(){
    return hashTagsNumberOfTweetsToGet;
  }
  public void setHashTagsNumberOfTweetsToGet(Integer numberOfTweetsToGet){
    this.hashTagsNumberOfTweetsToGet = numberOfTweetsToGet;
  }

  public void setHashTags(String[] hashTags) {
    this.hashTags = hashTags;
  }

  public Boolean getGetTrendingTopics() {
    return getTrendingTopics;
  }

  public void setGetTrendingTopics(Boolean getTrendingTopics) {
    this.getTrendingTopics = getTrendingTopics;
  }

  public Integer getTrendingTopicsNumberOfTweetsToGet(){
    return trendingTopicsNumberOfTweetsToGet;
  }
  public void setTrendingTopicsNumberOfTweetsToGet(Integer numberOfTweetsToGet){
    this.trendingTopicsNumberOfTweetsToGet = numberOfTweetsToGet;
  }



  public Double getHashTagsLatitude() {
    return hashTagsLatitude;
  }

  public void setHashTagsLatitude(Double hashTagsLatitude) {
    this.hashTagsLatitude = hashTagsLatitude;
  }
  public Double getHashTagsLongitude() {
    return hashTagsLongitude;
  }

  public void setHashTagsLongitude(Double hashTagsLongitude) {
    this.hashTagsLongitude = hashTagsLongitude;
  }

  public Double getHashTagsRadius() {
    return hashTagsRadius;
  }

  public void setHashTagsRadius(Double hashTagsRadius) {
    this.hashTagsRadius = hashTagsRadius;
  }

  public String getHashTagsUnit() {
    return hashTagsUnit;
  }

  public void setHashTagsUnit(String hashTagsUnit) {
    this.hashTagsUnit = hashTagsUnit;
  }

  public Double getTrendingTopicsLatitude() {
    return trendingTopicsLatitude;
  }

  public void setTrendingTopicsLatitude(Double trendingTopicsLatitude) {
    this.trendingTopicsLatitude = trendingTopicsLatitude;
  }
  public Double getTrendingTopoicsLongitude() {
    return trendingTopoicsLongitude;
  }

  public void setTrendingTopoicsLongitude(Double trendingTopoicsLongitude) {
    this.trendingTopoicsLongitude = trendingTopoicsLongitude;
  }

  public Double getTrendingTopicsRadius() {
    return trendingTopicsRadius;
  }

  public void setTrendingTopicsRadius(Double trendingTopicsRadius) {
    this.trendingTopicsRadius = trendingTopicsRadius;
  }

  public String getTrendingTopicsUnit() {
    return trendingTopicsUnit;
  }

  public void setTrendingTopicsUnit(String trendingTopicsUnit) {
    this.trendingTopicsUnit = trendingTopicsUnit;
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
