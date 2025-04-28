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
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.sql.Time;
import java.util.Date;

/**
 * SerializeAdapter with jackson.<br>
 * date format: yyyy-MM-dd HH:mm:ss X
 * <pre>
 * &nbsp;@HttpClient(requestAdapter = "yeamy.restlite.httpclient.JacksonXmlRequestAdapter")
 *  public interface XXX {
 *  }
 * </pre>
 *
 * @see SerializeAdapter
 */
public class JacksonXmlRequestAdapter implements SerializeAdapter<Object> {
    protected static XmlMapper jackson = (XmlMapper) new XmlMapper().registerModule(new DateFormatModule());

    private static class DateFormatModule extends SimpleModule {
        public DateFormatModule() {
            addSerializer(Time.class, new JsonSerializer<Time>() {

                @Override
                public void serialize(Time value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeString(DateTimeUtil.format(value));
                }
            });
            addDeserializer(Time.class, new JsonDeserializer<Time>() {
                @Override
                public Time deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                    return DateTimeUtil.parseTime(p.getValueAsString());
                }
            });
            addSerializer(java.sql.Date.class, new JsonSerializer<java.sql.Date>() {

                @Override
                public void serialize(java.sql.Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeString(DateTimeUtil.format(value));
                }
            });
            addDeserializer(java.sql.Date.class, new JsonDeserializer<java.sql.Date>() {
                @Override
                public java.sql.Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                    return DateTimeUtil.parseDate(p.getValueAsString());
                }
            });
            addSerializer(Date.class, new JsonSerializer<Date>() {

                @Override
                public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeString(DateTimeUtil.format(value));
                }
            });
            addDeserializer(Date.class, new JsonDeserializer<Date>() {
                @Override
                public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                    return DateTimeUtil.parseDateTime(p.getValueAsString());
                }
            });
        }
    }

    @Override
    public HttpEntity doSerialize(Object data, String contentType) {
        try {
            return new StringEntity(jackson.writeValueAsString(data), ContentType.APPLICATION_JSON);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
