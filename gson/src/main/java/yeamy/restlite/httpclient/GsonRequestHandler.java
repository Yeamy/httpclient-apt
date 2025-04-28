
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
import java.sql.Time;

/**
 * HttpClientRequestBodyHandler with Google gson.<br>
 * date format: yyyy-MM-dd HH:mm:ss X
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
                    out.value(value.toPlainString());
                }

                @Override
                public BigDecimal read(JsonReader in) throws IOException {
                    return new BigDecimal(in.nextString());
                }
            })
            .registerTypeAdapter(java.util.Date.class, new TypeAdapter<java.util.Date>() {
                @Override
                public void write(JsonWriter out, java.util.Date value) throws IOException {
                    out.value(DateTimeUtil.format(value));
                }

                @Override
                public java.util.Date read(JsonReader in) throws IOException {
                    return DateTimeUtil.parseDateTime(in.nextString());
                }
            })
            .registerTypeAdapter(java.sql.Date.class, new TypeAdapter<java.sql.Date>() {
                @Override
                public void write(JsonWriter out, java.sql.Date value) throws IOException {
                    out.value(DateTimeUtil.format(value));
                }

                @Override
                public java.sql.Date read(JsonReader in) throws IOException {
                    return DateTimeUtil.parseDate(in.nextString());
                }
            })
            .registerTypeAdapter(Time.class, new TypeAdapter<Time>() {
                @Override
                public void write(JsonWriter out, Time value) throws IOException {
                    out.value(DateTimeUtil.format(value));
                }

                @Override
                public Time read(JsonReader in) throws IOException {
                    return DateTimeUtil.parseTime(in.nextString());
                }
            }).create();

    @Override
    public HttpEntity createEntity(Object data, String contentType) {
        return new StringEntity(gson.toJson(data), ContentType.APPLICATION_JSON);
    }
}
