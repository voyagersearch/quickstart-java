package voyager.quickstart.location.autonomy;

import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.Axis;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmSequenceIterator;

import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import voyager.api.discovery.jobs.JobSubmitter;
import voyager.api.domain.model.entry.Entry;
import voyager.api.domain.model.entry.EntryMeta;
import voyager.api.error.DiscoveryError;
import voyager.discovery.location.BaseDiscoveryRunner;
import voyager.http.VoyagerHttpClient;
import voyager.http.VoyagerHttpException;
import voyager.http.VoyagerHttpRequest;
import voyager.quickstart.location.autonomy.bean.AutnHit;
import voyager.quickstart.location.autonomy.bean.AutnResponseData;
import voyager.quickstart.location.autonomy.util.*;

import com.google.common.base.Function;
import com.google.common.base.Throwables;

public class AutonomyRunner extends BaseDiscoveryRunner<AutonomyLocation> {
  
  static final Logger log = LoggerFactory.getLogger(AutonomyRunner.class);

  final AutonomyXMLReader reader;
  final XmlHttpParser parser;
  
  public AutonomyRunner( AutonomyLocation loc, SolrServer solr, JobSubmitter jobs) {
    super(loc, solr, jobs);
  
    AutonomyXMLReader r = null;
    try {
      r = new AutonomyXMLReader();
    }
    catch(SaxonApiException x) {
      Throwables.propagate(x);
    }
    
    reader = r;
    parser = new XmlHttpParser(reader.builder) {
      @Override
      public XmlHttpResponse parse(VoyagerHttpRequest req, InputStream input, String contentType, String encoding) throws VoyagerHttpException {
        XmlHttpResponse rsp = new XmlHttpResponse();
        try {
          // HACK HACK HACK!
          // we are just fixing the XML so it has an autonomy response
          // hopefully a real service advertises real headers
          String xml = IOUtils.toString(input);
          xml = xml.replaceFirst("<autnresponse>", "<autnresponse xmlns:autn=\"http://schemas.autonomy.com/aci/\">");
          rsp.doc = builder.build(new StreamSource(new StringReader(xml)));
        }
        catch (Exception e) {
          throw new VoyagerHttpException( req, "Error reading stram", e );
        }
        return rsp;
      }
    };
  }

  //----------------------------------------------------
  //----------------------------------------------------
  
  @Override
  protected void doCrawl() throws Exception {
    URI uri = location.getURI();
    if(uri==null) {
      throw new IllegalArgumentException("Missing URI");
    }
    
    setProcessedCount(0);
    
    VoyagerHttpRequest req = new VoyagerHttpRequest("https://voyagerdemo.com/Autonomy");
    req.params = new ModifiableSolrParams();
    
    final AtomicInteger count = new AtomicInteger(0);
    int start = 1;
    while(!isStopRequested()) {
      req.params.set("Start", start); 
      req.params.set("MaxResults", start+location.getPageSize());
      req.params.set("predict", Boolean.FALSE); 
      count.set(0);
      
      XdmNode doc = parser.parse(req, VoyagerHttpClient.getInstance()).doc;

      final AutnHit hit = new AutnHit();
      AutnResponseData d = reader.readResponse(doc, new Function<XdmNode, Void>() {
        @Override
        public Void apply(XdmNode node) {
          incrProcessedCount(1);
          
          hit.reset();
          count.incrementAndGet();
          XdmNode content = reader.fillHitContent(hit, node);
          try {
            addDoc( toEntry(hit, content) );
          }
          catch(Exception ex) {
            addError(new DiscoveryError("error adding hit", ex));
          }
          return null;
        }
      });
      setItemsToProcess(d.totalhits);
      
      if(count.get()==0) { // no results returned
        break;
      }
      
      start += location.getPageSize(); // or count.size?
      if(start>=d.totalhits) {
        break;
      }
    } 
  }
  
  public Entry toEntry(AutnHit hit, XdmNode content) {
    if(hit.id==null) {
      throw new IllegalArgumentException("Autonomy result is missing an id");
    }
    Entry entry = new Entry(hit.title);
    entry.setId(location.getIdForPath(hit.id));
    SolrInputDocument doc = entry.getFields();
    doc.setField("id_autn", hit.id);
    doc.setField("meta_autn_database", hit.database);
    doc.setField("meta_autn_reference", hit.reference);
    doc.setField("meta_autn_links", hit.links);
    
    if(content!=null) {
      XdmSequenceIterator iter = content.axisIterator(Axis.CHILD);
      if(iter.hasNext()) {
        XdmNode node = (XdmNode)iter.next();
        // TODO -- run xpath queries here?  It is already parsed!
        EntryMeta meta = new EntryMeta();
        meta.setBody(node.toString()); // the raw XML
        entry.setMeta(meta);
      }
      if(iter.hasNext()) {
        entry.addWarning("multiple content sections", content.toString());
      }
    }
    return entry;
  }
}