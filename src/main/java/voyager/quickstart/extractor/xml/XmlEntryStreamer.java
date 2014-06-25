package voyager.quickstart.extractor.xml;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import voyager.api.domain.model.entry.Entry;
import voyager.quickstart.extractor.xml.setter.FieldSetter;

import com.google.common.base.Charsets;

/**
 * 
 */
public abstract class XmlEntryStreamer
{
  static final Logger log = LoggerFactory.getLogger(XmlEntryStreamer.class);

  public static final XMLInputFactory inputFactory;
  static {
    inputFactory = XMLInputFactory.newInstance();
    try {
      inputFactory.setProperty("javax.xml.stream.isNamespaceAware", Boolean.FALSE);

      // The java 1.6 bundled stax parser (sjsxp) does not currently have a thread-safe
      // XMLInputFactory, as that implementation tries to cache and reuse the
      // XMLStreamReader.  Setting the parser-specific "reuse-instance" property to false
      // prevents this.
      // All other known open-source stax parsers (and the bea ref impl)
      // have thread-safe factories.
      inputFactory.setProperty("reuse-instance", Boolean.FALSE);
    }
    catch (IllegalArgumentException ex) {
      // Other implementations will likely throw this exception since "reuse-instance"
      // isimplementation specific.
      log.debug("Unable to set the 'reuse-instance' property for the input chain: " + inputFactory);
    }
  }
  
  protected String boundary;
  protected final Map<String, FieldSetter> map = new HashMap<String, FieldSetter>();
  protected FieldSetter defaultAction = null;
  
  protected XmlEntryStreamer() {
    
  }

  public void readXML(InputStream input) throws XMLStreamException, IOException {
    InputStreamReader r = new InputStreamReader(input,Charsets.UTF_8);
    readXML(r);
  }

  public void readXML(Reader reader) throws XMLStreamException, IOException
  {
    XMLStreamReader xmlstream = inputFactory.createXMLStreamReader(reader);
    readXML( xmlstream );
  }

  public void readXML(XMLStreamReader stream) throws XMLStreamException
  {
    int numResults = -1;
    int count = 0;
    Entry entry = new Entry();
    String localName = null;
    StringBuilder text = new StringBuilder();
    StringBuilder fqn = new StringBuilder();
    while (true) {
      int event = stream.next();
      switch (event) {
      // Add everything to the text
      case XMLStreamConstants.SPACE:
      case XMLStreamConstants.CDATA:
      case XMLStreamConstants.CHARACTERS:
        text.append( stream.getText() );
        break;

      case XMLStreamConstants.END_ELEMENT:
        localName = stream.getLocalName().toLowerCase(Locale.ROOT);
        System.out.println("END: "+fqn + " :: " + text);
        
        if(boundary.equals(localName)) {
          stream(entry,count++);
          entry = new Entry();
        }
        else {
          FieldSetter f = map.get(fqn.toString());
          if(f==null) {
            f = defaultAction;
          }
          if(f!=null) {
            try {
              f.process(entry, localName, fqn, text);
            }
            catch(Exception ex) {
              entry.addWarningTrace("error setting field: "+fqn, ex);
            }
          }
        }
        
        // reset the length
        fqn.setLength(fqn.length()-(localName.length()+1));
        break;

      case XMLStreamConstants.END_DOCUMENT:
        return;

      case XMLStreamConstants.START_ELEMENT:
        text.setLength(0);
        localName = stream.getLocalName().toLowerCase(Locale.ROOT);
        
        if(numResults<0 && "DatasetList".equals(localName)) {
          System.out.println( "TODO, read count");  
          numResults = 0;
        }
        
        fqn.append('/').append(localName);
        break;
      }
    }
  }
  
  public void stream(Entry entry, int index) {
    System.out.println( index + " >> " +entry.toPrettyJSON() );
  }
}


