package yeamy.restlite.httpclient;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.hc.client5.http.entity.mime.ContentBody;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.sql.Time;
import java.text.ParseException;
import java.util.Date;

/**
 * SerializeAdapter with jackson.<br>
 * date format: yyyy-MM-dd HH:mm:ss X
 * <pre>
 * &nbsp;@HttpClient(requestAdapter = "yeamy.restlite.httpclient.JacksonRequestAdapter")
 *  public interface XXX {
 *  }
 * </pre>
 * @see SerializeAdapter
 */
public class JacksonRequestAdapter implements SerializeAdapter {
    protected static volatile ObjectMapper jackson = new ObjectMapper().registerModule(new DateFormatModule());

    private static class DateFormatModule extends SimpleModule {
        public DateFormatModule() {
            addSerializer(Time.class, new JsonSerializer<Time>() {

                @Override
                public void serialize(Time value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeString(TIME_FORMAT.format(value));
                }
            });
            addDeserializer(Time.class, new JsonDeserializer<Time>() {
                @Override
                public Time deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                    try {
                        return new Time(TIME_FORMAT.parse(p.getValueAsString()).getTime());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            addSerializer(java.sql.Date.class, new JsonSerializer<java.sql.Date>() {

                @Override
                public void serialize(java.sql.Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeString(DATE_FORMAT.format(value));
                }
            });
            addDeserializer(java.sql.Date.class, new JsonDeserializer<java.sql.Date>() {
                @Override
                public java.sql.Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                    try {
                        return new java.sql.Date(DATE_FORMAT.parse(p.getValueAsString()).getTime());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            addSerializer(Date.class, new JsonSerializer<Date>() {

                @Override
                public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeString(DATE_TIME_FORMAT.format(value));
                }
            });
            addDeserializer(Date.class, new JsonDeserializer<Date>() {
                @Override
                public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                    try {
                        return DATE_TIME_FORMAT.parse(p.getValueAsString());
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }

    /**
     * replace the jackson
     */
    public static void setJackson(ObjectMapper mapper) {
        jackson = mapper;
    }

    @Override
    public HttpEntity serializeAsBody(Object data, String contentType) throws IOException {
        return new StringEntity(jackson.writeValueAsString(data), ContentType.APPLICATION_JSON);
    }

    @Override
    public ContentBody serializeAsPart(Object data) throws IOException {
        return new StringBody(jackson.writeValueAsString(data), ContentType.APPLICATION_JSON);
    }
}
