package voyager.quickstart.location.earthquake;

import java.io.IOException;

import voyager.api.discovery.DiscoveryRunner;
import voyager.api.discovery.location.LocationFactory;

public class EarthquakesLocationFactory implements LocationFactory<EarthquakesLocation> {

  @Override
  public String getName() {
    return EarthquakesLocation.TYPE;
  }

  @Override
  public EarthquakesLocation newInstance() throws IllegalStateException {
    return new EarthquakesLocation();
  }

  @Override
  public DiscoveryRunner<?> newRunner(EarthquakesLocation loc, boolean delta) throws IOException {
    throw new RuntimeException("not implemented yet");
  }

  @Override
  public void validate(EarthquakesLocation loc) throws Exception {
    // it is OK
  }
}
