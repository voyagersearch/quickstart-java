package voyager.quickstart.location.twitter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

import org.apache.lucene.document.Document;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;



import voyager.api.discovery.jobs.JobSubmitter;
import voyager.api.domain.model.entry.DexField;
import voyager.api.domain.model.entry.Entry;
import voyager.api.domain.model.entry.EntryFieldType;
import voyager.api.domain.model.entry.EntryGeo;
import voyager.api.infrastructure.json.*;
import voyager.api.infrastructure.util.DateUtil;
import voyager.api.infrastructure.util.Registry;
import voyager.api.process.ProcessState;
import voyager.discovery.ConvertToSearchableDocument;
import voyager.discovery.location.BaseDiscoveryRunner;
import voyager.discovery1x.config.impl.DiscoveryDAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.*;

public class TwitterRunner extends BaseDiscoveryRunner<TwitterLocation> {

  private static final int TWEETS_PER_QUERY	= 100;
  private static final int MAX_QUERIES	= 500;

  static final Logger log = LoggerFactory.getLogger(TwitterRunner.class);
  private String lastID = null;
  private String twitterImageUrl = "http://www.pequea.com/images/stories/home/Twitter_logo_white.png";
  //http://www.silversprite.com/ss/wp-content/uploads/2014/09/url.png
  private Twitter twitter = null;

  private int	totalTweets = 0;
  private long maxID = -1;
  
  public TwitterRunner( TwitterLocation loc, SolrClient solr, JobSubmitter jobs) {
    super(loc, solr, jobs);
  }



  //----------------------------------------------------
  //----------------------------------------------------
  
  protected InputStream openStream(URI uri) throws IOException
  {
    // Can do something fancier if you like
    return uri.toURL().openStream();
  }

  @Override
  protected void doCrawl() throws Exception {
    log.info("Twitter, starting Crawl");
    URI uri = location.getURI();



    if(uri==null) {
      throw new IllegalArgumentException("Missing URI");
    }
    twitter4j.conf.ConfigurationBuilder cb = new twitter4j.conf.ConfigurationBuilder();
    cb.setDebugEnabled(true)
            .setOAuthConsumerKey(location.getOAuthConsumerKey())
            .setOAuthConsumerSecret(location.getOAuthConsumerSecret())
            .setOAuthAccessToken(location.getOAuthAccessToken())
            .setOAuthAccessTokenSecret(location.getOAuthAccessTokenSecret());
    TwitterFactory tf = new TwitterFactory(cb.build());
    twitter = tf.getInstance();

    
    // We could use the last successful info to affect our current query
    String last = location.getProperty("LastSuccess");
    if(last!=null) {
      log.info("FYI, LastSuccess was: {}", last);
    }
    last = location.getProperty("LastID");
    if(last!=null) {
      log.info("FYI, LastID was: {}", last);
    }

    try {

      try{



        List<Status> statuses = new ArrayList<>();
        if(location.getFeeds() != null && location.getFeeds().length > 0){
          for (String feed : location.getFeeds()) {
            log.info("Twitter - Looking for {}", feed);
            int pageNum = 1;
            while(true){
              int size = statuses.size();

              Paging page = new Paging(pageNum++, 100);
              statuses.addAll(twitter.getUserTimeline(feed, page));
              log.info("Twitter - Status Size {} = Statuses Size {}", size,statuses.size());
              if(statuses.size() == size){
                log.info("Twitter - Status Size {} = Statuses Size", size);
                break;
              }

              for (Status status : statuses) {

                createDocFromStatus(status);

              }
            }
          }
        }



      }
      catch (TwitterException te){


        log.info("Twitter - Failed to get timeline: {}", te.getMessage());
      }



      log.info("Twitter -Starting Locations");
      if(location.getLocations() != null && location.getLocations().length > 0){
        for (voyager.api.infrastructure.json.JSONObject twitterLocation : location.getLocations()) {
          //log.info("Twitter -Starting Hashtags: {}", hashTag);
          totalTweets = 0;
          maxID = -1;
          log.info("Twitter -Starting Locations: {}", location);
          double latitude = twitterLocation.optDouble("latitude",0f);
          log.info("Twitter - Latitude Location: {}", latitude);
          double longitude = twitterLocation.optDouble("longitude",0f);
          log.info("Twitter - Longitude Location: {}", longitude);
          double radius = twitterLocation.optDouble("radius",0f);
          log.info("Twitter - Radius Location: {}", radius);
          getQueryResultFromQuery(null,latitude, longitude,radius);
        }
      }


      log.info("Twitter -Starting Hastags");
      if(location.getHashTags() != null && location.getHashTags().length > 0){
        for (String hashTag : location.getHashTags()) {
          //log.info("Twitter -Starting Hashtags: {}", hashTag);
          totalTweets = 0;
          maxID = -1;
          getQueryResultFromQuery(hashTag,location.getHashTagsLatitude(), location.getHashTagsLongitude(),location.getHashTagsRadius());
        }
      }

      if(location.getGetTrendingTopics() == Boolean.TRUE) {
        ResponseList<Location> locations;
        locations = twitter.getAvailableTrends();
        for (Location trendLocation : locations) {
          Trends trends = twitter.getPlaceTrends(trendLocation.getWoeid());

          for (int i = 0; i < trends.getTrends().length; i++) {
            System.out.println(trends.getTrends()[i].getQuery());
            totalTweets = 0;
            maxID = -1;
            getQueryResultFromQuery(trends.getTrends()[i].getQuery(),location.getTrendingTopicsLatitude(), location.getTrendingTopoicsLongitude(),location.getTrendingTopicsRadius());

          }
        }

      }



    } catch (Exception te) {
      te.printStackTrace();
      System.out.println("Failed to get timeline: {}" + te.getMessage());
      log.info("Twitter Failed to get timeline: {}", te.getMessage());
      //System.exit(-1);
    }
    
    

  }


  
  @Override
  public void finish() {
    super.finish();
    
    if(state==ProcessState.SUCCESS) {
      location.setProperty("LastSuccess", DateUtil.FORMAT.SOLR.format(new Date()));
      if(lastID!=null) {
        location.setProperty("LastID", lastID);
      }
      
      try {
        // Note, use of DiscoveryDAO may change in the future!
        Registry.get(DiscoveryDAO.class).save(location);
      }
      catch(Exception ex) {
        log.warn("Error saving location: {}", location, ex);
      }
    }
  }

  private void createDocFromStatus(Status status){
    //SolrInputDocument doc = new SolrInputDocument();
    Entry doc = new Entry();
    try{

      //log.info("Twitter @ {}", status.getUser().getScreenName() + " - " + status.getText());

      doc.addField(DexField.ID.name, EntryFieldType.STRING,String.valueOf(status.getId())); // assuming this is globally unique
      doc.addField(DexField.NAME.name,EntryFieldType.STRING, status.getUser().getScreenName());
      doc.addField(DexField.TITLE.name, EntryFieldType.STRING,status.getUser().getName());
      doc.addField(DexField.CREATED.name, EntryFieldType.DATE,status.getCreatedAt());
      doc.addField(DexField.TYPE.name,EntryFieldType.STRING, location.getLocationType());
      doc.addField(DexField.DESCRIPTION.name,EntryFieldType.STRING, status.getText());
      doc.addField(DexField.URI.name, EntryFieldType.STRING,"https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId());
      doc.addField(DexField.PATH.name,EntryFieldType.STRING, "https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId());

      MediaEntity[] media = status.getMediaEntities(); //get the media entities from the status
      if(media != null && media.length > 0){
        for(MediaEntity m : media){ //search trough your entities
          //log.info("Twitter - has media {}", m.getType());
          if(m.getType().equals("photo")){
            //log.info("Twitter @ {}",  "Image Url - " + m.getMediaURL());
            doc.addField(DexField.IMAGE_URL.name,EntryFieldType.STRING, m.getMediaURL());

          }
          else{
           // log.info("Twitter @Default Image Url - ");
            doc.addField(DexField.IMAGE_URL.name,EntryFieldType.STRING,status.getUser().getOriginalProfileImageURL());
          }


        }
      }
      else{
        //log.info("Twitter @Default Image Url - ");
        doc.addField(DexField.IMAGE_URL.name,EntryFieldType.STRING,status.getUser().getOriginalProfileImageURL());
      }


      HashtagEntity[] hashTags = status.getHashtagEntities(); //get the hash tag entities from the status

      if(hashTags != null && hashTags.length > 0){
        List<String> hts = new ArrayList<>(hashTags.length);
        for(HashtagEntity ht : hashTags) { //search trough your entities
          //log.info("Twitter - has hashTag {}", ht.getText());
            hts.add(ht.getText());
        }
        doc.addField("hashtags", EntryFieldType.STRING, hts );
      }

      UserMentionEntity[] mentions = status.getUserMentionEntities(); //get the hash tag entities from the status
      if(mentions != null && mentions.length > 0){
        List<String> m = new ArrayList<>(mentions.length);
        for(UserMentionEntity mention : mentions){ //search trough your entities
          //log.info("Twitter - has mention {}", mention.getText());

          m.add(mention.getScreenName());
        }
        doc.addField("mentions",EntryFieldType.STRING, m);
      }


      // Read the geometry information
      EntryGeo ext = null;
      GeoLocation geo = status.getGeoLocation();
      if(geo != null){

        ext = new EntryGeo(geo.getLatitude(), geo.getLongitude());
      }
      else if(status.getPlace() != null) {
        GeoLocation[][] placeGeo = status.getPlace().getBoundingBoxCoordinates();
        if(placeGeo != null){


          Double centerLatitude = ( placeGeo[0][0].getLatitude() + placeGeo[0][3].getLatitude() ) / 2;
          Double centerLongitude = ( placeGeo[0][0].getLongitude() + placeGeo[0][3].getLongitude())  / 2;
          ext = new EntryGeo(centerLatitude, centerLongitude);


        }
      }

      // Add the extent fields to the SolrInputDocument
      if(ext!=null && ext.isValid()) {

        ConvertToSearchableDocument.setExtent(ConvertToSearchableDocument.toSolrInputDocument(doc,null,null), ext);
      }
      addDoc(doc);
    }
    catch(IOException ex){
      log.warn("IO Error adding document: {}", doc, ex);
    }
    catch (SolrServerException ssex){
      log.warn("Error adding document: {}", doc, ssex);
    }

  }

  private void getQueryResultFromQuery(String queryRequest,Double latitude,Double longitude, Double radius){
    QueryResult result;
    try{
      //	This returns all the various rate limits in effect for us with the Twitter API
      log.info("Twitter -Starting QueryResult before Map:");
      Map<String ,RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus("search");



      for (String endpoint : rateLimitStatus.keySet()) {
        RateLimitStatus status = rateLimitStatus.get(endpoint);
        log.info("Endpoint: {}" , endpoint);
        log.info(" Limit: {}" , status.getLimit());
        log.info(" Remaining: {}" , status.getRemaining());
        log.info(" ResetTimeInSeconds: {}" , status.getResetTimeInSeconds());
        log.info(" SecondsUntilReset: {}" , status.getSecondsUntilReset());
      }

      log.info("Twitter -Starting QueryResult after Map:");
      //	This finds the rate limit specifically for doing the search API call we use in this program
      RateLimitStatus searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");

      log.info("Twitter -Starting QueryResult after RateLimitStatus:");
      //	Always nice to see these things when debugging code...
      log.info("Twitter - You have {} calls remaining out of {}, Limit resets in {} seconds", searchTweetsRateLimit.getRemaining(), searchTweetsRateLimit.getLimit(), searchTweetsRateLimit.getSecondsUntilReset());

      for (int queryNumber=0;queryNumber < MAX_QUERIES; queryNumber++) {
        //	Do we need to delay because we've already hit our rate limits?
        log.info("Twitter - You have {} calls remaining out of {}, Limit resets in {} seconds", searchTweetsRateLimit.getRemaining(), searchTweetsRateLimit.getLimit(), searchTweetsRateLimit.getSecondsUntilReset());
        if (searchTweetsRateLimit.getRemaining() == 0) {
          //	Yes we do, unfortunately ...
          log.info("Twitter - Sleeping for {} seconds due to rate limits", searchTweetsRateLimit.getSecondsUntilReset());
          //	If you sleep exactly the number of seconds, you can make your query a bit too early
          // 	and still get an error for exceeding rate limitations
          // // Adding two seconds seems to do the trick. Sadly, even just adding one second still triggers a
          // 	rate limit exception more often than not. I have no idea why, and I know from a Comp Sci
          // 	standpoint this is really bad, but just add in 2 seconds and go about your business. Or else.
          Thread.sleep((searchTweetsRateLimit.getSecondsUntilReset()+2) * 1000l);
        }

        Query query = new Query(queryRequest);
        query.count(TWEETS_PER_QUERY );
        query.resultType(Query.ResultType.recent);

        if(latitude != 0f && longitude != 0f || radius != 0f )
        {
          GeoLocation geoLoc = new GeoLocation(latitude, longitude);
          query.geoCode(geoLoc,radius,Query.MILES.toString());
        }

        if (maxID != -1) {
          query.setMaxId(maxID - 1);
        }
        result = twitter.search(query);

        if(result.getTweets().size() == 0){

          break;
        }

        for(Status status: result.getTweets()){
          if (maxID == -1 || status.getId() < maxID)
          {
            maxID = status.getId();
          }
          createDocFromStatus(status);
        }

        searchTweetsRateLimit = result.getRateLimitStatus();
      }


    }
    catch(InterruptedException ie){
      log.info("Twitter Wait Failed: ()", ie.getMessage());
    }
    catch(TwitterException te){
      log.info("Twitter Failed to get timeline:", te.getMessage());
    }

  }

}