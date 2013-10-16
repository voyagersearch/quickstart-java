package voyager.quickstart.location;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServer;

import com.google.common.base.Throwables;

import voyager.api.discovery.DiscoveryRunner;
import voyager.api.discovery.jobs.JobSubmitter;
import voyager.common.SolrServers;
import voyager.common.util.Registry;
import voyager.discovery.location.path.walker.IndexingFolderVisitor;
import voyager.discovery.location.path.walker.SimpleFileWalker;

public class CustomLocationFactory implements LocationFactory<CustomLocation> {

  @Override
  public String getName() {
    return CustomLocation.TYPE;
  }

  @Override
  public CustomLocation newInstance() throws IllegalStateException {
    return new CustomLocation();
  }

  @Override
  public DiscoveryRunner<?> newRunner(CustomLocation loc, boolean delta) throws IOException {
    throw new RuntimeException("not implemented yet");
  }

  @Override
  public void validate(CustomLocation loc) throws Exception {
    // it is OK
  }
}
