
package yeamy.restlite.httpclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.time.OffsetDateTime;

/**
 * HttpClientRequestBodyHandler with Google gson.<br>
 * <pre>{@code
 * HttpClient(requestBodyHandler = GsonRequestHandler.class,
 *            responseHandler = GsonResponseHandler.class)
 * public interface XXX {
 * }
 * }</pre>
 */
public class GsonRequestHandler implements HttpClientRequestBodyHandler<Object> {
    protected static Gson gson = new GsonBuilder()
            .registerTypeAdapter(BigDecimal.class, new TypeAdapter<BigDecimal>() {
                @Override
                public void write(JsonWriter out, BigDecimal value) throws IOException {
                    out.value(value == null ? null : value.toPlainString());
                }

                @Override
                public BigDecimal read(JsonReader in) throws IOException {
                    String num = in.nextString();
                    return num == null ? null : new BigDecimal(num);
                }
            })
            .registerTypeAdapter(OffsetDateTime.class, new TypeAdapter<OffsetDateTime>() {
                @Override
                public void write(JsonWriter out, OffsetDateTime value) throws IOException {
                    out.value(value == null ? null : value.toString());
                }

                @Override
                public OffsetDateTime read(JsonReader in) throws IOException {
                    String value = in.nextString();
                    return value == null ? null : OffsetDateTime.parse(value);
                }
            })
            .registerTypeAdapter(DateTime.class, new TypeAdapter<DateTime>() {
                @Override
                public void write(JsonWriter out, DateTime value) throws IOException {
                    out.value(value == null ? null : value.toString());
                }

                @Override
                public DateTime read(JsonReader in) throws IOException {
                    String value = in.nextString();
                    return value == null ? null : DateParser.parseDateTime(value);
                }
            })
            .registerTypeAdapter(Date.class, new TypeAdapter<Date>() {
                @Override
                public void write(JsonWriter out, Date value) throws IOException {
                    out.value(value == null ? null : value.toString());
                }

                @Override
                public Date read(JsonReader in) throws IOException {
                    String value = in.nextString();
                    return value == null ? null : DateParser.parseDate(value);
                }
            })
            .registerTypeAdapter(Time.class, new TypeAdapter<Time>() {
                @Override
                public void write(JsonWriter out, Time value) throws IOException {
                    out.value(value == null ? null : value.toString());
                }

                @Override
                public Time read(JsonReader in) throws IOException {
                    String value = in.nextString();
                    return value == null ? null : DateParser.parseTime(value);
                }
            }).create();

    @Override
    public HttpEntity createEntity(Object data, String contentType) {
        return new StringEntity(gson.toJson(data), ContentType.APPLICATION_JSON);
    }
}
