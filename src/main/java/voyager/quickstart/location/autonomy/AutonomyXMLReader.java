package voyager.quickstart.location.autonomy;

import java.io.InputStream;
import java.util.Iterator;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.saxon.Configuration;
import net.sf.saxon.lib.FeatureKeys;
import net.sf.saxon.lib.Validation;
import net.sf.saxon.s9api.Axis;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathExecutable;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmSequenceIterator;
import voyager.quickstart.location.autonomy.bean.AutnHit;
import voyager.quickstart.location.autonomy.bean.AutnResponseData;

import com.google.common.base.Function;

public class AutonomyXMLReader {
  static final Logger log = LoggerFactory.getLogger(AutonomyXMLReader.class);
  
  public final Processor processor;
  public final DocumentBuilder builder;

  public final XPathExecutable xp_response;
  public final XPathExecutable xp_hit_from_response;
  
  public AutonomyXMLReader() throws SaxonApiException {
    processor = new Processor(false);
    processor.setConfigurationProperty(FeatureKeys.DTD_VALIDATION, false);
    processor.setConfigurationProperty(FeatureKeys.SCHEMA_VALIDATION, Validation.SKIP);
    processor.setConfigurationProperty(FeatureKeys.RECOVERY_POLICY, Configuration.RECOVER_WITH_WARNINGS);
    builder = processor.newDocumentBuilder();
    
    XPathCompiler c = processor.newXPathCompiler();
    c.declareNamespace("autn", "http://schemas.autonomy.com/aci/");
    
    xp_response = c.compile("/autnresponse/responsedata");
    xp_hit_from_response = c.compile("autn:hit");
  }
  
  public XdmNode read(InputStream stream) throws SaxonApiException {
    return builder.build(new StreamSource(stream));
  }
  
  public AutnResponseData readResponse(XdmNode doc, Function<XdmNode, Void> hits) throws SaxonApiException {
    XPathSelector sel = xp_response.load();
    sel.setContextItem(doc);
    Iterator<XdmItem> iter = sel.iterator();
    if(!iter.hasNext()) {
      throw new RuntimeException("not found");
    }
    
    XdmNode node = (XdmNode)iter.next();
    AutnResponseData r = new AutnResponseData();
    XdmSequenceIterator s = node.axisIterator(Axis.CHILD);
    while(s.hasNext()) {
      XdmNode item = (XdmNode) s.next();
      QName name = item.getNodeName();
      String local = name.getLocalName();
      if("hit".equals(local)) {
        hits.apply(item);
      }
      else {
        try {
          BeanUtils.setProperty(r, local, item.getStringValue());
        }
        catch(Exception ex) {
          log.warn("error reading response bean: {}={}", local, item, ex);
        }
      }
    }
    return r;
  }
  
  public XdmNode fillHitContent(AutnHit hit, XdmNode node) {
    XdmNode content = null;
    XdmSequenceIterator s = node.axisIterator(Axis.CHILD);
    while(s.hasNext()) {
      XdmNode item = (XdmNode) s.next();
      QName name = item.getNodeName();
      String local = name.getLocalName();
      if("content".equals(local)) {
        content = item;
      }
      else {
        try {
          BeanUtils.setProperty(hit, local, item.getStringValue());
        }
        catch(Exception ex) {
          log.warn("error filling hit bean: {}={} for {}", local, item, hit.id, ex);
        }
      }
    }
    return content;
  }
}
