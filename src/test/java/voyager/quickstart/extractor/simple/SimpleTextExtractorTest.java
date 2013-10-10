package voyager.quickstart.extractor.simple;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import voyager.api.discovery.jobs.DiscoveryJob;
import voyager.api.domain.model.entry.DexField;
import voyager.api.domain.model.entry.Entry;

public class SimpleTextExtractorTest {
  @Test
  public void testSimpleExtractor() {
    SimpleTextExtractor extractor = new SimpleTextExtractor();
    
    InputStream input = SimpleTextExtractorTest.class.getResourceAsStream("sample.txt");
    DiscoveryJob job = new DiscoveryJob();
    Entry out = extractor.read(job, input, null, null);
    
    Assert.assertEquals("The body text", out.getFieldValue(DexField.TEXT));  }
}
