package voyager.quickstart.location.autonomy;

import java.io.InputStream;
import net.sf.saxon.s9api.*;

import org.junit.Test;
import com.google.common.base.Function;

import voyager.api.domain.model.entry.Entry;
import voyager.quickstart.location.LocationTestHelper;
import voyager.quickstart.location.autonomy.bean.AutnHit;
import voyager.quickstart.location.autonomy.bean.AutnResponseData;

public class AutonomyLocationTest {
  
  @Test
  public void testReadXML() throws Exception {
    
    LocationTestHelper helper = new LocationTestHelper();
    AutonomyLocation loc = new AutonomyLocation();
    final AutonomyRunner runner = new AutonomyRunner(loc, helper.solr, helper.jobs);
    
    final AutonomyXMLReader reader = new AutonomyXMLReader();
    
    InputStream stream = AutonomyLocationTest.class.getResourceAsStream("sample_0.xml");
    XdmNode doc = reader.read(stream);

    final AutnHit hit = new AutnHit();
    AutnResponseData d = reader.readResponse(doc, new Function<XdmNode, Void>() {
      @Override
      public Void apply(XdmNode node) {
        hit.reset();
        XdmNode content = reader.fillHitContent(hit, node);
        Entry entry = runner.toEntry(hit, content);
        System.out.println("HIT: "+entry.toPrettyJSON() );
        return null;
      }
    });
    System.out.println("FOUND: "+d.numhits + " / " + d.totalhits);
  }
}
