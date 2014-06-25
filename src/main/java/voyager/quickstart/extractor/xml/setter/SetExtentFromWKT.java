package voyager.quickstart.extractor.xml.setter;

import voyager.api.domain.model.entry.Entry;
import voyager.api.domain.model.entry.EntryExtent;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

public class SetExtentFromWKT implements FieldSetter {

  WKTReader reader;
  
  public SetExtentFromWKT() {
    reader = new WKTReader();
  }
  
  public void process(Entry entry, String localName, CharSequence fqn, StringBuilder text) {
    try {
      Geometry geo = reader.read(text.toString());
      entry.setExtent(new EntryExtent(geo));
    }
    catch(Exception ex) {
      entry.addWarningTrace("unable to read extent", ex);
    }
  }
}
