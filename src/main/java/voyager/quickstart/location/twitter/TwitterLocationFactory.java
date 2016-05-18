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
    // See others: http://earthquake.usgs.gov/earthquakes/feed/v1.0/atom.php
    try {
      TwitterLocation loc = new TwitterLocation();

      loc.setOAuthConsumerKey("yourOAuthConsumerKey");
      loc.setOAuthConsumerSecret("yourOAuthConsumerSecret");
      loc.setOAuthAccessToken("yourOAuthAccessToken");
      loc.setOAuthAccessTokenSecret("yourOAuthAccessTokenSecret");
      loc.setUser("VoyagerSearch");
      loc.setURI(new URI("https://twitter.com/VoyagerSearch"));
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
    if(loc.getUser()==null) {
      throw new IllegalArgumentException("Missing User");
    }
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

    loc.setURI(new URI("https://twitter.com/" + loc.getUser()));
    if(Strings.isNullOrEmpty(loc.getId())) {
      loc.setId(ServiceLocation.getIdForURI(loc.getURI()));
    }
    // TODO: check that the URL is actually atom...
    // TODO: set the name from the atom feed?
  }
}
