package voyager.quickstart.placefinder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.solr.common.EmptyEntityResolver;
import org.apache.solr.common.util.XMLErrorLogger;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.context.jts.JtsSpatialContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import voyager.api.placefinder.PlaceResult;
import voyager.api.placefinder.PlaceSearch;
import voyager.api.placefinder.Placefinder;
import voyager.api.placefinder.PlacefinderFactory;
import voyager.api.placefinder.PlacefinderSettings;
import voyager.common.http.VoyagerHttpClient;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;


public class XmlStreamPlacefinder extends Placefinder implements ResponseHandler<List<PlaceResult>> {

  static final Logger log = LoggerFactory.getLogger(XmlStreamPlacefinder.class);
  static final XMLErrorLogger xmllog = new XMLErrorLogger(log);
  
  @Component
  public static class Factory implements PlacefinderFactory {

    public static final String URL = "url";
    public static final String NODE = "node";
    public static final String LAT = "lat";
    public static final String LON = "lon";
    public static final String PLACE_NAME = "place_name";
    public static final String PLACE_NAME_EXT = "place_name_ext";
    
    @Override
    public String getName() {
      return "XmlStream";
    }

    @Override
    public String getTitle() {
      return "Get place from XML Request";
    }

    @Override
    public String getDescription() {
      return "This placefinder will parse XML results from the configured URL.  Note the {query} in the URL template setting";
    }

    @Override
    public PlacefinderSettings createInitialSettings() {
      PlacefinderSettings settings = new PlacefinderSettings().type(getName()).threshold(0.4f).enabled(false);
      settings.put(URL, "http://geonames.nga.mil/nameswfs/service.svc/get?SERVICE=WFS&VERSION=1.1.0&REQUEST=GetFeature&TYPENAME=GEONAMES&FILTER=%3CFilter%20xmlns%3D%22http%3A%2F%2Fwww.opengis.net%2Fogc%22%3E%3CAnd%3E%3CPropertyIsLike%20wildCard%3D%22*%22%20singleChar%3D%22_%22%20escapeChar%3D%22%5C%22%3E%3CPropertyName%3ENAME%3C%2FPropertyName%3E%3CLiteral%3E{query}*%3C%2FLiteral%3E%3C%2FPropertyIsLike%3E%3CPropertyIsLike%20wildCard%3D%22*%22%20singleChar%3D%22_%22%20escapeChar%3D%22%5C%22%3E%3CPropertyName%3EFEATURE_DESIGNATION_CODE%3C%2FPropertyName%3E%3CLiteral%3EPPL*%3C%2FLiteral%3E%3C%2FPropertyIsLike%3E%3C%2FAnd%3E%3C%2FFilter%3E");
      settings.put(NODE, "GEONAMES");
      settings.put(LAT, "LATITUDE_DD");
      settings.put(LON, "LONGITUDE_DD");
      settings.put(PLACE_NAME, "NAME");
      settings.put(PLACE_NAME_EXT, "PRIMARY_ADMIN_DIVISION");
      return settings;
    }

    @Override
    public XmlStreamPlacefinder create(PlacefinderSettings settings) {
      return new XmlStreamPlacefinder(settings);
    }
  }

  protected static final SpatialContext CTX = JtsSpatialContext.GEO;
  
  final XMLInputFactory inputFactory;
  final SAXParserFactory saxFactory;

  protected final String param_url;
  protected final String param_node;
  protected final String param_lat;
  protected final String param_lon;
  protected final String param_place_name;
  protected final String param_place_name_ext;

  public XmlStreamPlacefinder(PlacefinderSettings settings) {
    super(settings);
    
    param_url = (String)settings.get(Factory.URL); //, "http://geonames.nga.mil/nameswfs/service.svc/get?SERVICE=WFS&VERSION=1.1.0&REQUEST=GetFeature&TYPENAME=GEONAMES&FILTER=%3CFilter%20xmlns%3D%22http%3A%2F%2Fwww.opengis.net%2Fogc%22%3E%3CAnd%3E%3CPropertyIsLike%20wildCard%3D%22*%22%20singleChar%3D%22_%22%20escapeChar%3D%22%5C%22%3E%3CPropertyName%3ENAME%3C%2FPropertyName%3E%3CLiteral%3E{query}*%3C%2FLiteral%3E%3C%2FPropertyIsLike%3E%3CPropertyIsLike%20wildCard%3D%22*%22%20singleChar%3D%22_%22%20escapeChar%3D%22%5C%22%3E%3CPropertyName%3EFEATURE_DESIGNATION_CODE%3C%2FPropertyName%3E%3CLiteral%3EPPL*%3C%2FLiteral%3E%3C%2FPropertyIsLike%3E%3C%2FAnd%3E%3C%2FFilter%3E");
    param_node = (String)settings.get(Factory.NODE); //, "GEONAMES");
    param_lat = (String)settings.get(Factory.LAT); //, "LATITUDE_DD");
    param_lon = (String)settings.get(Factory.LON); //, "LONGITUDE_DD");
    param_place_name = (String)settings.get(Factory.PLACE_NAME); //, "NAME");
    param_place_name_ext = (String)settings.get(Factory.PLACE_NAME_EXT); //, "PRIMARY_ADMIN_DIVISION");
    
    
    // Init StAX parser:
    inputFactory = XMLInputFactory.newInstance();
    EmptyEntityResolver.configureXMLInputFactory(inputFactory);
    inputFactory.setXMLReporter(xmllog);
    try {
      // The java 1.6 bundled stax parser (sjsxp) does not currently have a thread-safe
      // XMLInputFactory, as that implementation tries to cache and reuse the
      // XMLStreamReader.  Setting the parser-specific "reuse-instance" property to false
      // prevents this.
      // All other known open-source stax parsers (and the bea ref impl)
      // have thread-safe factories.
      inputFactory.setProperty("reuse-instance", Boolean.FALSE);
    } catch (IllegalArgumentException ex) {
      // Other implementations will likely throw this exception since "reuse-instance"
      // isimplementation specific.
      log.debug("Unable to set the 'reuse-instance' property for the input chain: " + inputFactory);
    }
    
    // Init SAX parser (for XSL):
    saxFactory = SAXParserFactory.newInstance();
    saxFactory.setNamespaceAware(false); 
    EmptyEntityResolver.configureSAXParserFactory(saxFactory);    
  }

  @Override
  public boolean matches(CharSequence text) {
    return true; // do this for everything
  }

  @Override
  public List<PlaceResult> find(PlaceSearch search) throws Exception {
    HttpClient httpclient = VoyagerHttpClient.getInstance().getHttpClient();
    HttpGet httpget = getMethod(search);
    return httpclient.execute(httpget, this);
  }
  
  protected HttpGet getMethod(PlaceSearch search) {
    String url = param_url.replace("{query}", 
        StringEscapeUtils.escapeJavaScript(search.text.toString()));

    System.out.println("SEND:"+url);
    return new HttpGet(url);
  }

  @Override
  public List<PlaceResult> handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
    int status = response.getStatusLine().getStatusCode();
    if (status >= 200 && status < 300) {
      HttpEntity entity = response.getEntity();
      String enc = null;
      try {
        enc = entity.getContentEncoding().getValue();
      }
      catch(Exception ex) {}
      return find(entity.getContent(), enc); 
    } else {
      throw new ClientProtocolException("Unexpected response status: " + status);
    }
  }
  
  public final class NodeInfo {
    boolean inside = false;
    public Map<String, String> attrs = new HashMap<String, String>();
    public Map<String, String> children = new HashMap<String, String>();
    
    public void clear() {
      attrs.clear();
      children.clear();
    }
    
    @Override
    public String toString() {
      return "NodeInfo["+attrs+":"+children+"]";
    }
  }
  
  public List<PlaceResult> find(InputStream is, String charset) throws IOException {
    List<PlaceResult> results = new ArrayList<PlaceResult>(10);
    XMLStreamReader parser = null;
    final StringBuilder text = new StringBuilder();
    final NodeInfo element = new NodeInfo();
    element.inside = false;
    try {
      parser = (charset == null) ?
        inputFactory.createXMLStreamReader(is) : inputFactory.createXMLStreamReader(is, charset);
        
      while (true) {
        int event = parser.next();
        switch (event) {
          case XMLStreamConstants.END_DOCUMENT:
            parser.close();
            return results;

          case XMLStreamConstants.START_ELEMENT: {
            String currTag = parser.getLocalName();
            text.setLength(0);
            if (currTag.equals(param_node)) {
              if(element.inside) {
                throw new IllegalStateException("can not have nested elements");
              }
              element.inside = true;
              element.clear();
              
              for (int i = 0; i < parser.getAttributeCount(); i++) {
                String k = parser.getAttributeLocalName(i);
                String v = parser.getAttributeValue(i);
                element.attrs.put(k, v);
              }
            } 
            else {
              if(!element.inside) {
                System.out.println("START:"+currTag + " :: " + element);
              }
            }
            break;}

            // Add everything to the text
          case XMLStreamConstants.SPACE:
          case XMLStreamConstants.CDATA:
          case XMLStreamConstants.CHARACTERS:
            text.append(parser.getText());
            break;

          case XMLStreamConstants.END_ELEMENT: {
            String currTag = parser.getLocalName();
            if (currTag.equals(param_node)) {
              if(!element.inside) {
                throw new IllegalStateException("can not have nested elements");
              }
              element.inside = false;
              PlaceResult r = toPlaceResult(element);
              if(r!=null) {
                System.out.println( "ADD: "+r.toString() );
                results.add(r);
              }
            }
            else if(text.length()>0) {
              String val = text.toString().trim();
              if(val.length()>0) {
                element.children.put(currTag, val);
              }
            }
            break;
          }
        }
      }
    } 
    catch(XMLStreamException ex) {
      throw new IOException(ex);
    }
    finally {
      if (parser != null) {
        try {
          parser.close();
        } catch (XMLStreamException e) {
          throw new IOException(e);
        }
      }
    }
  }
  
  public PlaceResult toPlaceResult(NodeInfo info) {
    String lat = info.children.remove(param_lat);
    String lon = info.children.remove(param_lon);
    String name = info.children.remove(param_place_name);
    String name_ext = info.children.remove(param_place_name_ext);
    
    if(lat!=null&&lon!=null&&name!=null) {
      try {
        String id = info.attrs.get("id");
        if(Strings.isNullOrEmpty(id)) {
          HashFunction hf = Hashing.sha1();
          HashCode hc = hf.newHasher()
              .putString(info.toString(), Charsets.UTF_8)
              .hash();
          id = hc.toString();
        }
        PlaceResult p = new PlaceResult(id, 1.0f, getName());
        p.shape(CTX.makePoint(Double.parseDouble(lon), Double.parseDouble(lat)));
        
        if(name_ext!=null) {
          name += " ("+name_ext+")";
        }
        p.name(name);
        for(Map.Entry<String, String> entry : info.children.entrySet()) {
          p.attr(entry.getKey(), entry.getValue());
        }
        return p;
      }
      catch(Exception ex) {
        log.warn("Error reading place: {}", info, ex );
      }
    }
    return null;
  }
}
