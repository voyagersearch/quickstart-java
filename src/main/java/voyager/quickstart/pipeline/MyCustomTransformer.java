package voyager.quickstart.pipeline;

import java.io.Serializable;
import java.util.Date;

import org.apache.solr.common.SolrInputDocument;

import voyager.api.pipeline.DocumentTransformer;

/**
 * This is a custom location where you can modify the document
 *  before it gets indexed by voyager
 */
public class MyCustomTransformer implements DocumentTransformer, Serializable {

  @Override
  public boolean transform(SolrInputDocument doc) throws Exception {
    StringBuilder str = new StringBuilder();
    str.append("Hello! id=")
       .append(doc.getFieldValue("id"))
       .append(" at ").append(new Date());
    doc.setField("meta_my_custom_field", str.toString());
    return true; // or false if nothing changed
  }
}
