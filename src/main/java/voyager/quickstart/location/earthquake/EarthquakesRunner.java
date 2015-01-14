package voyager.quickstart.location.earthquake;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;

import voyager.api.discovery.jobs.JobSubmitter;
import voyager.api.domain.model.entry.DexField;
import voyager.api.domain.model.entry.EntryExtent;
import voyager.discovery.ConvertToSearchableDocument;
import voyager.discovery.location.BaseDiscoveryRunner;

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
    
    Parser parser = getAbdera().getParser();
    try {
      Document<Feed> xdoc = parser.parse(openStream(uri), uri.toString());
      Feed feed = xdoc.getRoot();
      // Get the feed title
      System.out.println("Feed Title: " + feed.getTitle());

      // Get the entry items...
      for (Entry item : feed.getEntries()) {
        SolrInputDocument doc = new SolrInputDocument();
        doc.setField(DexField.ID.name, item.getId().toString().replace(':', '_')); // assuming this is globally unique
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
        EntryExtent ext = null;
        for(Position p : GeoHelper.getPositions(item)) {
          if(p instanceof Point) {
            Coordinate point = ((Point)p).getCoordinate();
            EntryExtent tmp = new EntryExtent(point.getLatitude(), point.getLongitude()); 
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
          ConvertToSearchableDocument.setExtent(doc, ext);
        }
        addDoc(doc);
      }
    } 
    catch (Exception ex) {
      log.error("Error reading feed", ex);
    }
  }
}