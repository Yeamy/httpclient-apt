package yeamy.restlite.httpclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.hc.client5.http.entity.mime.ContentBody;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * SerializeAdapter with jackson.<br>
 * date format: yyyy-MM-dd HH:mm:ss X
 * <pre>
 * &nbsp;@HttpClient(requestAdapter = "yeamy.restlite.httpclient.JacksonXmlRequestAdapter")
 *  public interface XXX {
 *  }
 * </pre>
 * @see SerializeAdapter
 */
public class JacksonXmlRequestAdapter implements SerializeAdapter {
    static final ObjectMapper jackson = new XmlMapper()
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss X"));

    @Override
    public HttpEntity serializeAsBody(Object data, String contentType) throws IOException {
        return new StringEntity(jackson.writeValueAsString(data), ContentType.APPLICATION_JSON);
    }

    @Override
    public ContentBody serializeAsPart(Object data) throws IOException {
        return new StringBody(jackson.writeValueAsString(data), ContentType.APPLICATION_JSON);
    }
}
