package voyager.quickstart.extractor.simple;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Date;

import org.apache.commons.io.IOUtils;

import voyager.api.discovery.jobs.DiscoveryJob;
import voyager.api.domain.model.entry.DexField;
import voyager.api.domain.model.entry.Entry;
import voyager.api.error.DiscoveryError;
import voyager.discovery.extractor.base.HttpEnabledExtractor;

import com.google.common.base.Strings;

public class SimpleTextExtractor extends HttpEnabledExtractor<Entry>
{
  public SimpleTextExtractor()
  {

  }

  @Override
  public Entry read(DiscoveryJob job, InputStream input, String contentType, String encoding) {
    Entry entry = job.getValidEntry();
    try {
      Charset cs = getCharsetForEncoding(encoding);
      
      // auto-close the resource
      try(InputStreamReader r = new InputStreamReader(input,cs)) {
        String body = IOUtils.toString(r);
        entry.setField(DexField.TEXT, body);
        entry.setField(DexField.CHARACTER_COUNT, body.length());
      }

      // Fill in some value
      entry.setField(DexField.AUTHOR, "author");

      // Set a name field
      String name = (String)entry.getFieldValue(DexField.NAME);
      if(Strings.isNullOrEmpty(name)) {
        entry.setName("example "+new Date());
      }
      
      // Set a date field
      entry.setField(DexField.META_MODIFIED_DATE, new Date());
    }
    catch(Exception ex) {
      entry.addErrorMessage(new DiscoveryError("Error reading file", ex));
    }
    return entry;
  }

}
