package voyager.quickstart.placefinder;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.spatial4j.core.context.SpatialContext;
import com.spatial4j.core.context.jts.JtsSpatialContext;
import com.spatial4j.core.shape.Shape;

import voyager.api.placefinder.PlaceResult;
import voyager.api.placefinder.PlaceSearch;
import voyager.api.placefinder.Placefinder;
import voyager.api.placefinder.PlacefinderFactory;
import voyager.api.placefinder.PlacefinderSettings;

public class SamplePlacefinder extends Placefinder {

  @Component
  public static class Factory implements PlacefinderFactory {
    @Override
    public String getName() {
      return "SAMPLE";
    }

    @Override
    public String getTitle() {
      return "Sample Placefinder";
    }

    @Override
    public String getDescription() {
      return "A description for your custom placefinder.";
    }

    @Override
    public PlacefinderSettings createInitialSettings() {
      return new PlacefinderSettings().type(getName());
    }

    @Override
    public Placefinder create(PlacefinderSettings settings) {
      return new SamplePlacefinder(settings);
    }
  }
  
  
  
  final Map<String, Shape> places;
  
  public SamplePlacefinder(PlacefinderSettings settings) {
    super(settings);
    places = new ConcurrentHashMap<String, Shape>();
    
    // Hardcode locations
    SpatialContext ctx = JtsSpatialContext.GEO;
    register(ctx.makePoint(37.783333, -122.416667), "San Francisco");
    register(ctx.makePoint(39.76185, -104.881105), "Denver");
    register(ctx.makePoint(38.904722, -77.016389), "Washington DC", "DC");
  }
  
  public String normalize(String query) {
    return query.toLowerCase(Locale.ROOT);
  }
  
  public void register(Shape shape, String ... names) {
    for(String name : names) {
      places.put(normalize(name), shape);
    }
  }
 
  @Override
  public boolean matches(CharSequence input) {
    return true; // this will accept any input
  }

  
  @Override
  public List<PlaceResult> find(PlaceSearch query) throws Exception {
    String norm = normalize(query.text.toString());
    Shape match = places.get(query.text);
    if(match!=null) {
      PlaceResult r = new PlaceResult(norm, 1.0f, getName()).shape(match);
      return Collections.singletonList(r);
    }
    return Collections.emptyList();
  }


  @Override
  public List<PlaceResult> suggest(PlaceSearch query) throws Exception {
    // Could do a prefix query
    return find(query);
  }
}
