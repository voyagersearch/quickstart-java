package voyager.quickstart.extractor.xml.setter;

import voyager.api.domain.model.entry.Entry;

public class MetaTextField implements FieldSetter {
  
  public MetaTextField() {
    
  }
  
  @Override
  public void process(Entry entry, String localName, CharSequence fqn, String text) {
    entry.getFields().setField("meta_"+
        localName.replace('-', '_').replace('.', '_'), text, 1.0f);
  }
}
