package voyager.quickstart.location.earthquake;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;

import voyager.api.discovery.jobs.JobSubmitter;
import voyager.api.domain.model.entry.DexField;
import voyager.api.domain.model.entry.EntryGeo;
import voyager.api.infrastructure.util.DateUtil;
import voyager.api.infrastructure.util.Registry;
import voyager.api.process.ProcessState;
import voyager.discovery.SearchableDocumentConverter;
import voyager.discovery.location.BaseDiscoveryRunner;
import voyager.discovery1x.config.impl.DiscoveryDAO;

import org.apache.abdera.ext.geo.Coordinate;
import org.apache.abdera.ext.geo.GeoHelper;
import org.apache.abdera.ext.geo.Point;
import org.apache.abdera.ext.geo.Position;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Link;
import org.apache.abdera.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EarthquakesRunner extends BaseDiscoveryRunner<EarthquakesLocation> {
  private static Abdera abdera = null;
  static final Logger log = LoggerFactory.getLogger(EarthquakesRunner.class);
  private String lastID = null;
  
  public EarthquakesRunner( EarthquakesLocation loc, SolrClient solr, JobSubmitter jobs) {
    super(loc, solr, jobs);
  }

  public static synchronized Abdera getAbdera() {
    if(abdera==null) {
      abdera = new Abdera();
    }
    return abdera;
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
    URI uri = location.getURI();
    if(uri==null) {
      throw new IllegalArgumentException("Missing URI");
    }
    
    // We could use the last successful info to affect our current query
    String last = location.getProperty("LastSuccess");
    if(last!=null) {
      log.info("FYI, LastSuccess was: {}", last);
    }
    last = location.getProperty("LastID");
    if(last!=null) {
      log.info("FYI, LastID was: {}", last);
    }
    
    
    Parser parser = getAbdera().getParser();
    try {
      Document<Feed> xdoc = parser.parse(openStream(uri), uri.toString());
      Feed feed = xdoc.getRoot();
      // Get the feed title
      System.out.println("Feed Title: " + feed.getTitle());

      // Get the entry items...
      for (Entry item : feed.getEntries()) {
        SolrInputDocument doc = new SolrInputDocument();
        lastID = item.getId().toString();
        doc.setField(DexField.ID.name, lastID.replace(':', '_')); // assuming this is globally unique
        doc.setField(DexField.NAME.name, item.getTitle());
        doc.setField(DexField.CREATED.name, item.getPublished());

        List<Link> links = item.getLinks();
        if(links!=null && !links.isEmpty()) {
          doc.setField(DexField.URI.name, links.get(0).getHref().toString());
          if(links.size()>1) {
            doc.addField(DexField.INDEXING_WARNING.name, "Multiple Link Fields found");
          }
        }
        for (Category category : item.getCategories()) {
          doc.addField(DexField.CATEGORY.name, category.getTerm());
        }
        
        // Read the geometry information
        EntryGeo ext = null;
        for(Position p : GeoHelper.getPositions(item)) {
          if(p instanceof Point) {
            Coordinate point = ((Point)p).getCoordinate();
            EntryGeo tmp = new EntryGeo(point.getLatitude(), point.getLongitude()); 
            if(ext==null) {
              ext = tmp;
            }
            else {
              ext.expand(tmp);
            }
          }
          else {
            log.warn("Unused location type... {}", p);
          }
        }
        
        // Add the extent fields to the SolrInputDocument
        if(ext!=null && ext.isValid()) {
          SearchableDocumentConverter.setExtent(doc, ext);
        }
        addDoc(doc);
      }
    } 
    catch (Exception ex) {
      log.error("Error reading feed", ex);
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