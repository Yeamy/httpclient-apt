package yeamy.restlite.httpclient;

import com.google.gson.reflect.TypeToken;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * HttpClientResponseHandler with Google gson.
 * <pre>{@code
 * @HttpClient(responseHandler = "yeamy.restlite.httpclient.GsonResponseHandler")
 * public interface XXX {
 * }
 * }</pre>
 * @see HttpClientResponseHandler
 */
public class GsonResponseHandler<T> implements HttpClientResponseHandler<T> {

    @Override
    public T handleResponse(ClassicHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            return null;
        }
        String charset = "UTF-8";
        String header = entity.getContentType();
        if (header != null) {
            String[] parts = header.split(";");
            if (parts.length > 1) {
                String[] charsetPart = parts[1].split("=");
                if (charsetPart.length == 2 && "charset".equalsIgnoreCase(charsetPart[0].trim())) {
                    charset = charsetPart[1];
                }
            }
        }
        // ignore contentType
        try (InputStream is = entity.getContent()) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(is.available());
            byte[] buf = new byte[512];
            while (true) {
                int l = is.read(buf);
                if (l == -1) {
                    break;
                }
                bos.write(buf, 0, l);
            }
            String json = bos.toString(charset);
            return GsonRequestAdapter.gson.fromJson(json, new TypeToken<T>() {
            }.getType());
        }

    }
}
