package yeamy.restlite.httpclient;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.hc.client5.http.entity.mime.ContentBody;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
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
    private static final ThreadLocal<XmlMapper> local = new ThreadLocal<>();

    private static class DateFormatModule extends SimpleModule {
        public DateFormatModule() {
            final SimpleDateFormat TF = new SimpleDateFormat("HH:mm:ss");
            addSerializer(Time.class, new JsonSerializer<Time>() {
                final SimpleDateFormat TF = new SimpleDateFormat("HH:mm:ss");

                @Override
                public void serialize(Time value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeString(TF.format(value));
                }
            });
            addDeserializer(Time.class, new JsonDeserializer<Time>() {
                @Override
                public Time deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                    try {
                        return new Time(TF.parse(p.getValueAsString()).getTime());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    private static volatile JacksonXmlBuilder builder = () -> (XmlMapper) new XmlMapper()
            .registerModule(new DateFormatModule())
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss X"));

    private static XmlMapper getJacksonXml() {
        XmlMapper jackson = local.get();
        if (jackson == null) {
            local.set(jackson = builder.build());
        }
        return jackson;
    }

    /**
     * replace the jackson
     */
    public static void setJacksonBuilder(JacksonXmlBuilder b) {
        builder = b;
    }


    @Override
    public HttpEntity serializeAsBody(Object data, String contentType) throws IOException {
        return new StringEntity(getJacksonXml().writeValueAsString(data), ContentType.APPLICATION_JSON);
    }

    @Override
    public ContentBody serializeAsPart(Object data) throws IOException {
        return new StringBody(getJacksonXml().writeValueAsString(data), ContentType.APPLICATION_JSON);
    }
}
