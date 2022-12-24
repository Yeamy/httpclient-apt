
package yeamy.restlite.httpclient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import org.apache.hc.client5.http.entity.mime.ContentBody;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;

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
    private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TF = new SimpleDateFormat("HH:mm:ss");
    public static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss X")
            .registerTypeAdapter(BigDecimal.class, (JsonSerializer<BigDecimal>)
                    (src, typeOfSrc, context) -> new JsonPrimitive(src.toPlainString()))
            .registerTypeAdapter(Date.class, (JsonSerializer<Date>)
                    (src, typeOfSrc, context) -> new JsonPrimitive(DF.format(src)))
            .registerTypeAdapter(Time.class, (JsonSerializer<Time>)
                    (src, typeOfSrc, context) -> new JsonPrimitive(TF.format(src)))
            .create();

    @Override
    public HttpEntity serializeAsBody(Object data, String contentType) {
        return new StringEntity(gson.toJson(data), ContentType.APPLICATION_JSON);
    }

    @Override
    public ContentBody serializeAsPart(Object data) {
        return new StringBody(gson.toJson(data), ContentType.APPLICATION_JSON);
    }
}
