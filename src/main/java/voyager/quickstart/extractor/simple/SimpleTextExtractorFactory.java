package voyager.quickstart.extractor.simple;

import java.util.Locale;

import voyager.api.discovery.extractor.Extractor;
import voyager.api.discovery.info.ExtractorInfoBean;
import voyager.api.discovery.info.FormatInfoBean;
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

  /**
   * Explicitly map this Extractor to a known mime-type
   */
  @Override
  public ExtractorInfoBean getInfoBean() {
    ExtractorInfoBean info = super.getInfoBean();
    FormatInfoBean b = new FormatInfoBean();
    b.setMime("text/plain");
    b.setPriority((byte)10);
    info.add(b);
    return info;
  }
  
  @Override
  public Extractor newExtractor() {
    return new SimpleTextExtractor();
  }
}
