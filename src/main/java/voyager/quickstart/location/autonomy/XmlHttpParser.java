package voyager.quickstart.location.autonomy;

import java.io.InputStream;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.DocumentBuilder;
import voyager.http.BaseHttpParser;
import voyager.http.VoyagerHttpException;
import voyager.http.VoyagerHttpRequest;

public class XmlHttpParser extends BaseHttpParser<VoyagerHttpRequest,XmlHttpResponse>
{
  public final DocumentBuilder builder;
  
  public XmlHttpParser(DocumentBuilder builder) {
    this.builder = builder;
  }
  
  @Override
  public XmlHttpResponse parse(VoyagerHttpRequest req, InputStream input, String contentType, String encoding) throws VoyagerHttpException {
    XmlHttpResponse rsp = new XmlHttpResponse();
    try {
      rsp.doc = builder.build(new StreamSource(input));
    }
    catch (Exception e) {
      throw new VoyagerHttpException( req, "Error reading stram", e );
    }
    return rsp;
  }
}
