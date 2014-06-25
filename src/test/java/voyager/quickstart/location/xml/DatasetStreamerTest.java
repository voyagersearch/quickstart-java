package voyager.quickstart.location.xml;

import java.io.InputStream;
import org.junit.Test;
import voyager.quickstart.extractor.xml.XmlEntryStreamer;

public class DatasetStreamerTest {
  
  @Test
  public void testReadIndex() throws Exception {
    InputStream in = DatasetStreamerTest.class.getResourceAsStream("ssnippet.xml");
    
    XmlEntryStreamer s = new DatasetEntryStreamer();
    s.readXML(in);
   
    System.out.println("done...");
  }
}
