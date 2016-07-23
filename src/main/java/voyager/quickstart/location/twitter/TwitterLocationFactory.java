package voyager.quickstart.location.twitter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.solr.client.solrj.SolrClient;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import voyager.api.discovery.DiscoveryRunner;
import voyager.api.discovery.jobs.JobSubmitter;
import voyager.api.discovery.location.LocationFactory;
import voyager.api.discovery.location.service.ServiceLocation;
import voyager.api.infrastructure.json.JSONObject;
import voyager.api.infrastructure.json.JSONWriter;
import voyager.api.infrastructure.util.Registry;

public class TwitterLocationFactory implements LocationFactory<TwitterLocation> {

  static final Logger log = LoggerFactory.getLogger(TwitterLocationFactory.class);
  @Override
  public String getName() {
    return TwitterLocation.TYPE;
  }

  @Override
  public TwitterLocation newInstance() throws IllegalStateException {
    try {
      TwitterLocation loc = new TwitterLocation();
      loc.setURI(new URI("https://twitter.com/"));
      return loc;
    }
    catch(URISyntaxException ex) {
      Throwables.propagate(ex);
    }
    return null;
  }

  @Override
  public TwitterLocation newSampleInstance() {

    try {
      TwitterLocation loc = new TwitterLocation();

      loc.setOAuthConsumerKey("yourOAuthConsumerKey");
      loc.setOAuthConsumerSecret("yourOAuthConsumerSecret");
      loc.setOAuthAccessToken("yourOAuthAccessToken");
      loc.setOAuthAccessTokenSecret("yourOAuthAccessTokenSecret");

      loc.setURI(new URI("https://twitter.com/VoyagerSearch"));

      String[] hashTags = {"#usgs","#usda","#blm"};
      loc.setHashTags(hashTags);
      loc.setHashTagsNumberOfTweetsToGet(100);

      String[] feeds = {"Voyager","usda","usgs"};
      loc.setFeeds(feeds);
      loc.setFeedsNumberOfTweetsToGet(100);

     /* JSONObject jj = new JSONObject();
      jj.append("latitude",40.712784);
      jj.append("longitude",-74.00594);
      jj.append("radius",20);
      JSONObject[] jja = {jj};
      loc.setLocations(jja);*/




      loc.setHashTagsLatitude(40.712784);
      loc.setHashTagsLongitude(-74.00594);
      loc.setHashTagsRadius(100.0);
      loc.setHashTagsUnit("Miles is only supported at this time");

      loc.setTrendingTopicsNumberOfTweetsToGet(100);
      loc.setTrendingTopicsLatitude(40.712784);
      loc.setTrendingTopoicsLongitude(-74.00594);
      loc.setTrendingTopicsRadius(100.0);
      loc.setTrendingTopicsUnit("Miles is only supported at this time");

      return loc;
    }
    catch(Exception ex) {
      Throwables.propagate(ex);
    }
    return null;
  }

  @Override
  public DiscoveryRunner<?> newRunner(TwitterLocation loc, boolean delta) throws IOException {
    SolrClient solr = Registry.get(SolrClient.class);
    JobSubmitter jobs = Registry.get(JobSubmitter.class);
    return new TwitterRunner(loc, solr, jobs);
  }

  @Override
  public void validate(TwitterLocation loc) throws Exception {

    if(loc.getOAuthConsumerKey()==null || loc.getOAuthConsumerKey().length() == 0) {
      throw new IllegalArgumentException("Missing Consumer Key");
    }
    if(loc.getOAuthConsumerSecret()==null || loc.getOAuthConsumerSecret().length() == 0) {
      throw new IllegalArgumentException("Missing Consumer Secret");
    }
    if(loc.getOAuthAccessToken()==null || loc.getOAuthAccessToken().length() == 0) {
      throw new IllegalArgumentException("Missing Access Token");
    }
    if(loc.getOAuthAccessTokenSecret()==null || loc.getOAuthAccessTokenSecret().length() == 0) {
      throw new IllegalArgumentException("Missing Access Token Secret");
    }

    loc.setURI(new URI("https://twitter.com/" + Math.random()));
    if(Strings.isNullOrEmpty(loc.getId())) {
      loc.setId(ServiceLocation.getIdForURI(loc.getURI()));
    }

  }
}
