package voyager.quickstart.location.xml;

import java.io.File;
import java.io.IOException;

import org.apache.solr.client.solrj.SolrClient;

import voyager.api.discovery.DiscoveryRunner;
import voyager.api.discovery.jobs.JobSubmitter;
import voyager.api.discovery.location.Location;
import voyager.api.discovery.location.LocationFactory;
import voyager.api.infrastructure.util.Registry;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;

public class XmlFolderScanLocationFactory implements LocationFactory<XmlFolderScanLocation> {

  @Override
  public String getName() {
    return XmlFolderScanLocation.TYPE;
  }

  @Override
  public XmlFolderScanLocation newInstance() throws IllegalStateException {
    return new XmlFolderScanLocation();
  }

  @Override
  public XmlFolderScanLocation newSampleInstance() {
    XmlFolderScanLocation loc = new XmlFolderScanLocation();
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
  public DiscoveryRunner<?> newRunner(XmlFolderScanLocation loc, boolean delta) throws IOException {
    SolrClient solr = Registry.get(SolrClient.class);
    JobSubmitter jobs = Registry.get(JobSubmitter.class);
    return new XmlFolderScanRunner(loc, solr, jobs);
  }

  @Override
  public void validate(XmlFolderScanLocation loc) throws Exception {
    if(loc.getPath()==null) {
      throw new IllegalArgumentException("Missing Path");
    }
    if(Strings.isNullOrEmpty(loc.getName())) {
      loc.setName( new File(loc.getPath()).getName() );
    }
    if(Strings.isNullOrEmpty(loc.getId())) {
      loc.setId(Location.getLocationHash(loc.getClass()+loc.getPath()));
    }
  }
}
