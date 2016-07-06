package voyager.quickstart.placefinder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.InputStream;
import java.util.List;

import org.apache.solr.common.params.SolrParams;
import org.junit.Test;

import org.locationtech.spatial4j.shape.Shape;
import voyager.api.infrastructure.json.JSONStringer;
import voyager.api.placefinder.PlaceResult;
import voyager.api.placefinder.PlaceSearch;
import voyager.api.placefinder.PlacefinderSettings;

public class XmlStreamPlacefinderTest {

  XmlStreamPlacefinder finder = new XmlStreamPlacefinder(new PlacefinderSettings());

  @Test
  public void testStream() throws Exception {
    InputStream input = this.getClass().getResourceAsStream("XmlStreamPlacefinder_test0.xml");
    
    List<PlaceResult> results = finder.find(input, null);
    
    for(PlaceResult r : results) {
      System.out.println(r.writeJSON(new JSONStringer()));
    }
  }

  Shape matchShape(String text) throws Exception {
    return match(text).shape();
  }

  PlaceResult match(String text) throws Exception {
    List<PlaceResult> results = finder.find(new PlaceSearch(text,null, mock(SolrParams.class)));
    assertThat("something is matched", !results.isEmpty());
    return results.iterator().next();
  }
}
