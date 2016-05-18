package voyager.quickstart.location.twitter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;

import voyager.api.discovery.jobs.JobSubmitter;
import voyager.api.domain.model.entry.DexField;
import voyager.api.domain.model.entry.EntryGeo;
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

  static final Logger log = LoggerFactory.getLogger(TwitterRunner.class);
  private String lastID = null;
  
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
    Twitter twitter = tf.getInstance();

    
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
      List<Status> statuses;
      String user = location.getUser();
      if (user != null && user.length() > 0) {
        //user = args[0];
        statuses = twitter.getUserTimeline(user);
      } else {
        user = twitter.verifyCredentials().getScreenName();
        statuses = twitter.getUserTimeline();
      }
      System.out.println("Showing @" + user + "'s user timeline.");
      for (Status status : statuses) {
        SolrInputDocument doc = new SolrInputDocument();
        System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
        log.info("Twitter @ {}", status.getUser().getScreenName() + " - " + status.getText());

        doc.setField(DexField.ID.name, status.getId()); // assuming this is globally unique
        doc.setField(DexField.NAME.name, status.getUser().getScreenName());
        doc.setField(DexField.TITLE.name, status.getUser().getScreenName());
        doc.setField(DexField.CREATED.name, status.getCreatedAt());
        doc.setField(DexField.TYPE.name, location.getLocationType());
        doc.setField(DexField.DESCRIPTION.name, status.getText());
        doc.setField(DexField.URI.name, location.getURI());


        // Read the geometry information
        EntryGeo ext = null;
        GeoLocation geo = status.getGeoLocation();
        if(geo != null){
          log.info("Twitter Has Geo {}",geo.getLatitude());
          ext = new EntryGeo(geo.getLatitude(), geo.getLongitude());
        }

        // Add the extent fields to the SolrInputDocument
        if(ext!=null && ext.isValid()) {
          ConvertToSearchableDocument.setExtent(doc, ext);
        }
        addDoc(doc);


      }
    } catch (TwitterException te) {
      te.printStackTrace();
      System.out.println("Failed to get timeline: " + te.getMessage());
      log.info("Twitter Failed to get timeline:", te.getMessage());
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
}