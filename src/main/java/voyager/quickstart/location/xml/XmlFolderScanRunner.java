package voyager.quickstart.location.xml;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.wicket.util.file.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;

import voyager.api.discovery.jobs.DiscoveryAction;
import voyager.api.discovery.jobs.DiscoveryJob;
import voyager.api.discovery.jobs.JobSubmitter;
import voyager.api.domain.model.entry.DexField;
import voyager.api.domain.model.entry.Entry;
import voyager.api.error.DiscoveryError;
import voyager.discovery.location.BaseDiscoveryRunner;
import voyager.quickstart.extractor.xml.XmlEntryStreamer;

public class XmlFolderScanRunner extends BaseDiscoveryRunner<XmlFolderScanLocation> {
  static final Logger log = LoggerFactory.getLogger(XmlFolderScanRunner.class);
  
  public XmlFolderScanRunner( XmlFolderScanLocation loc, SolrClient solr, JobSubmitter jobs) {
    super(loc, solr, jobs);
  }

  //----------------------------------------------------
  //----------------------------------------------------
  
  @Override
  protected void doCrawl() throws Exception {

    final AtomicInteger counter = new AtomicInteger();
    final XmlEntryStreamer streamer = new DatasetEntryStreamer() {
      @Override
      public void stream(Entry entry, int index) {
        entry.setField(DexField.LOCATION, location.getId());
        entry.setField(DexField.DISCOVERY_ID, getStatus().getId());
        
        // Make sure there exists a valid ID
        if(entry.getId()==null) {
          String v = (String) entry.getFieldValue("id_internal");
          if(v==null) {
            v = counter.get()+"_" + index;
          }
          entry.setId(location.getId()+"_"+v);
        }
        DiscoveryJob job = new DiscoveryJob();
        job.setAction(DiscoveryAction.ADD);
        job.setEntry(entry);
        jobs.submit(job);
        incrProcessedCount(1);
      }
    };
    
    Path path = new File(location.getPath()).toPath();
    lastItem = path.toAbsolutePath().toString();
    
    java.nio.file.Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
      @Override 
      public FileVisitResult visitFile(Path path, BasicFileAttributes attr) throws IOException 
      {
        if(attr.size() > 0 && attr.isRegularFile() && 
            path.getFileName().toString().endsWith(".xml")) {
          counter.incrementAndGet();
          try {
            streamer.readXML(Files.newBufferedReader(path, Charsets.UTF_8));
          }
          catch(Exception ex) {
            log.warn("unable to read: {}", path, ex);
            addWarning(ex);
          }
        }
        else {
          log.info("skip: {}", path);
        }
        return FileVisitResult.CONTINUE;
      }
      
      @Override
      public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
      {
        return FileVisitResult.CONTINUE;
      }
    });
    
    if(counter.get()<1) {
      addError(new DiscoveryError("No XML Documents found under: "+location.getPath()));
    }
    if(getProcessedCount()<1) {
      addError(new DiscoveryError("No Entries found in "+counter+" Documents"));
    }
  }
}