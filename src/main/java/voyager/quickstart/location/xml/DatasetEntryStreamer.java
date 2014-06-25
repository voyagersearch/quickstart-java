package voyager.quickstart.location.xml;

import java.text.SimpleDateFormat;

import voyager.api.domain.model.entry.DexField;
import voyager.quickstart.extractor.xml.XmlEntryStreamer;
import voyager.quickstart.extractor.xml.setter.DateFieldSetter;
import voyager.quickstart.extractor.xml.setter.MetaTextField;
import voyager.quickstart.extractor.xml.setter.SetExtentFromWKT;
import voyager.quickstart.extractor.xml.setter.StringField;

public class DatasetEntryStreamer extends XmlEntryStreamer {

  public DatasetEntryStreamer() {
    boundary = "dataset";

    map.put("/datasetlist/dataset/datasetidentificationtext", 
        new StringField(DexField.NAME.name));

    map.put("/datasetlist/dataset/datasetinternalid", 
        new StringField("id_internal"));
    
    map.put("/datasetlist/dataset/restriction/security-classificationcode", 
        new StringField(DexField.SECURITY_CLASSIFICATION.name));

    map.put("/datasetlist/dataset/ds-country-coverage", 
        new StringField(DexField.GEOGRAPIC_COVERAGE.name));
    
    map.put("/datasetlist/dataset/ds-bounding-polygon/ds-bounding-polygonpolygon", 
        new SetExtentFromWKT()); //
    
    map.put("/datasetlist/dataset/datasetpublicationdate", 
        new DateFieldSetter(DexField.PUBLISHED.name, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"))); 

    map.put("/datasetlist/dataset/image-product/image/sensor-namecode", 
        new StringField(DexField.SENSOR_TYPE.name)); 
    
    // copy everything else to 'meta_xxxx';
    defaultAction = new MetaTextField();
  }
}
