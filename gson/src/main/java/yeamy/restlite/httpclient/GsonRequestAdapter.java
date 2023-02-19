
package yeamy.restlite.httpclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.hc.client5.http.entity.mime.ContentBody;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Time;

/**
 * SerializeAdapter with Google gson.<br>
 * date format: yyyy-MM-dd HH:mm:ss X
 * <pre>
 * &nbsp;@HttpClient(requestAdapter = "yeamy.restlite.httpclient.GsonRequestAdapter")
 *  public interface XXX {
 *  }
 * </pre>
 * @see SerializeAdapter
 */
public class GsonRequestAdapter implements SerializeAdapter {
    protected static volatile Gson gson = new GsonBuilder()
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
                    out.value(DATE_TIME_FORMAT.format(value));
                }

                @Override
                public java.util.Date read(JsonReader in) throws IOException {
                    try {
                        return DATE_TIME_FORMAT.parse(in.nextString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            })
            .registerTypeAdapter(java.sql.Date.class, new TypeAdapter<java.sql.Date>() {
                @Override
                public void write(JsonWriter out, java.sql.Date value) throws IOException {
                    out.value(DATE_FORMAT.format(value));
                }

                @Override
                public java.sql.Date read(JsonReader in) throws IOException {
                    try {
                        return new java.sql.Date(DATE_FORMAT.parse(in.nextString()).getTime());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            })
            .registerTypeAdapter(Time.class, new TypeAdapter<Time>() {
                @Override
                public void write(JsonWriter out, Time value) throws IOException {
                    out.value(TIME_FORMAT.format(value));
                }

                @Override
                public Time read(JsonReader in) throws IOException {
                    try {
                        return new Time(TIME_FORMAT.parse(in.nextString()).getTime());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }).create();

    /**
     * replace the gson
     */
    public static void setGson(Gson gson) {
        GsonRequestAdapter.gson = gson;
    }


    @Override
    public HttpEntity serializeAsBody(Object data, String contentType) {
        return new StringEntity(gson.toJson(data), ContentType.APPLICATION_JSON);
    }

    @Override
    public ContentBody serializeAsPart(Object data) {
        return new StringBody(gson.toJson(data), ContentType.APPLICATION_JSON);
    }
}
