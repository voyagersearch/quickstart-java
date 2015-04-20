package voyager.quickstart.extractor.xml.setter;

import voyager.api.domain.model.entry.Entry;
import voyager.api.domain.model.entry.EntryGeo;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

public class SetExtentFromWKT implements FieldSetter {

  WKTReader reader;
  
  public SetExtentFromWKT() {
    reader = new WKTReader();
  }
  
  @Override
  public void process(Entry entry, String localName, CharSequence fqn, String text) {
    try {
      Geometry geo = reader.read(text);
      entry.setExtent(new EntryGeo(geo));
    }
    catch(Exception ex) {
      entry.addWarningTrace("unable to read extent", ex);
    }
  }
}
