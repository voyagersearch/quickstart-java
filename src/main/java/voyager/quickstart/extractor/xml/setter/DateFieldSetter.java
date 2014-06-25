package voyager.quickstart.extractor.xml.setter;

import java.text.DateFormat;

import voyager.api.domain.model.entry.Entry;

public class DateFieldSetter implements FieldSetter {
  
  final String fname;
  final DateFormat format;
  
  public DateFieldSetter(String fname, DateFormat format) {
    this.fname = fname;
    this.format = format;
  }
  
  public void process(Entry entry, String localName, CharSequence fqn, StringBuilder text) {
    try {
      entry.getFields().setField(fname, format.parse(text.toString()), 1.0f); 
    }
    catch(Exception ex) {
      entry.addWarningTrace("unable to set date field", ex);
    }
  }
}
