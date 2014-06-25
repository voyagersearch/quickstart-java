package voyager.quickstart.extractor.xml.setter;

import voyager.api.domain.model.entry.Entry;

/**
 * Given a string value, set the field
 */
public interface FieldSetter {
  public void process(Entry entry, String localName, CharSequence fqn, String text);
}
