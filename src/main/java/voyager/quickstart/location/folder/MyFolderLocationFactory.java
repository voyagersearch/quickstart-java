package voyager.quickstart.location.folder;

import java.io.File;
import java.io.IOException;

import org.apache.solr.client.solrj.SolrServer;

import voyager.api.discovery.DiscoveryRunner;
import voyager.api.discovery.jobs.JobSubmitter;
import voyager.api.discovery.location.LocationFactory;
import voyager.api.discovery.location.service.ServiceLocation;
import voyager.common.util.Registry;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;

public class MyFolderLocationFactory implements LocationFactory<MyFolderLocation> {

  @Override
  public String getName() {
    return MyFolderLocation.TYPE;
  }

  @Override
  public MyFolderLocation newInstance() throws IllegalStateException {
    return new MyFolderLocation();
  }

  @Override
  public MyFolderLocation newSampleInstance() {
    MyFolderLocation loc = new MyFolderLocation();
    try {
      // Assume we are running in ${app.dir}
      File docs = new File( "../dev/java/quickstart/docs" );
      loc.setPath(docs.getCanonicalPath());
    }
    catch(Exception ex) {
      Throwables.propagate(ex);
    }
    return loc;
  }

  @Override
  public DiscoveryRunner<?> newRunner(MyFolderLocation loc, boolean delta) throws IOException {
    SolrServer solr = Registry.get(SolrServer.class);
    JobSubmitter jobs = Registry.get(JobSubmitter.class);
    return new MyFolderDiscoveryRunner(loc, solr, jobs);
  }

  @Override
  public void validate(MyFolderLocation loc) throws Exception {
    if(loc.getPath()==null) {
      throw new IllegalArgumentException("Missing Path");
    }
    if(Strings.isNullOrEmpty(loc.getId())) {
      loc.setId(ServiceLocation.getLocationHash(loc.getPath()));
    }
  }
}
