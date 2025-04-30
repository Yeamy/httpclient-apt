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
import java.sql.Date;
import java.sql.Time;
import java.time.OffsetDateTime;

/**
 * HttpClientRequestBodyHandler with jackson.<br>
 * <pre>{@code
 * @HttpClient(requestAdapter = "yeamy.restlite.httpclient.JacksonXmlRequestHandler")
 * public interface XXX {
 * }
 * }</pre>
 *
 * @see HttpClientRequestBodyHandler
 */
public class JacksonXmlRequestHandler implements HttpClientRequestBodyHandler<Object> {
    protected static XmlMapper jackson = (XmlMapper) new XmlMapper().registerModule(new DateFormatModule());

    private static class DateFormatModule extends SimpleModule {
        public DateFormatModule() {
            addSerializer(Time.class, new JsonSerializer<>() {

                @Override
                public void serialize(Time value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeString(value.toString());
                }
            });
            addDeserializer(Time.class, new JsonDeserializer<>() {
                @Override
                public Time deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                    return DateParser.parseTime(p.getValueAsString());
                }
            });
            addSerializer(Date.class, new JsonSerializer<>() {

                @Override
                public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeString(value.toString());
                }
            });
            addDeserializer(Date.class, new JsonDeserializer<>() {
                @Override
                public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                    return DateParser.parseDate(p.getValueAsString());
                }
            });
            addSerializer(DateTime.class, new JsonSerializer<>() {

                @Override
                public void serialize(DateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeString(value.toString());
                }
            });
            addDeserializer(DateTime.class, new JsonDeserializer<>() {
                @Override
                public DateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                    return DateParser.parseDateTime(p.getValueAsString());
                }
            });
            addSerializer(OffsetDateTime.class, new JsonSerializer<>() {

                @Override
                public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeString(value.toString());
                }
            });
            addDeserializer(OffsetDateTime.class, new JsonDeserializer<>() {
                @Override
                public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                    return OffsetDateTime.parse(p.getValueAsString());
                }
            });
        }
    }

    @Override
    public HttpEntity createEntity(Object data, String contentType) {
        try {
            return new StringEntity(jackson.writeValueAsString(data), ContentType.APPLICATION_JSON);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
