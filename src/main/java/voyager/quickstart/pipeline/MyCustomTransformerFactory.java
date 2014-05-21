package voyager.quickstart.pipeline;

import voyager.api.pipeline.DocumentTransformer;
import voyager.api.pipeline.DocumentTransformerFactory;

/**
 * This class is only required if you want Voyager to show this transformer
 * as an option in the drop down choice.
 * 
 * Make sure to register this in:
 * 
 */
public class MyCustomTransformerFactory implements DocumentTransformerFactory {

  @Override
  public String getName() {
    return "My Custom Transformer";
  }

  @Override
  public DocumentTransformer newSampleInstance() {
    return new MyCustomTransformer();
  }
}
