package voyager.quickstart.extractor.xml.setter;

import voyager.api.domain.model.entry.Entry;

public class StringField implements FieldSetter {
  
  final String fname;
  
  public StringField(String fname) {
    this.fname = fname;
  }
  
  public void process(Entry entry, String localName, CharSequence fqn, StringBuilder text) {
    entry.getFields().setField(fname, text.toString(), 1.0f); 
  }
}
