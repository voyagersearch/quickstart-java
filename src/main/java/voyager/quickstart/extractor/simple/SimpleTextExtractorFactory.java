package voyager.quickstart.extractor.simple;

import java.util.Locale;

import voyager.api.discovery.extractor.Extractor;
import voyager.discovery.extractor.info.AbstractJavaExtractorFactory;

public class SimpleTextExtractorFactory extends AbstractJavaExtractorFactory {

  @Override
  public String getDescription(Locale locale) {
    return "Quickstart Sample Extractor";
  }
  
  @Override
  public String getExtractorName() {
    return "quickstart";
  }
  
  @Override
  public Extractor newExtractor() {
    return new SimpleTextExtractor();
  }
}
