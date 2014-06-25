package voyager.quickstart.extractor.xml.setter;

import voyager.api.domain.model.entry.Entry;

public class MetaTextField implements FieldSetter {
  
  public MetaTextField() {
    
  }
  
  public void process(Entry entry, String localName, CharSequence fqn, StringBuilder text) {
    entry.getFields().setField("meta_"+localName, text, 1.0f);
  }
}
