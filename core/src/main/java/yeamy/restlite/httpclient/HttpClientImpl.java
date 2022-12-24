package yeamy.restlite.httpclient;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * http client executor
 */
public class HttpClientImpl {

    /**
     * execute the http request
     *
     * @param request     http request to execute
     * @param handler     http response deserializer
     * @param maxTryTimes max run times
     * @return response data
     * @param <T> any type determined by the response
     */
    public static <T> T execute(ClassicHttpRequest request, HttpClientResponseHandler<T> handler, int maxTryTimes) {
        int tryTimes = 1;
        while (true) {
            try {
                return client.execute(request, handler);
            } catch (Exception e) {
                e.printStackTrace();
                if (tryTimes >= maxTryTimes) break;
                try {
                    Thread.sleep(tryTimes >= 3 ? 300L : 100L * tryTimes);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            tryTimes++;
        }
        return null;
    }

    private static final CloseableHttpClient client = HttpClients.createDefault();
    private static volatile short VERSION = 0;

    /**
     * Translates a string into application/x-www-form-urlencoded format using a specific Charset.
     * This method uses the supplied charset to obtain the bytes for unsafe characters.
     * @param s String to be translated. charset – the given charset
     * @return the translated String.
     */
    public static String encodeUrl(String s) {
        if (VERSION == 0) {
            try {
                URLEncoder.class.getMethod("encode", String.class, Charset.class);
                VERSION = 10;
            } catch (Exception e) {
                VERSION = 8;
                e.printStackTrace();
            }
        }
        if (VERSION == 10 ) {
            return URLEncoder.encode(s, Charset.defaultCharset());
        } else {
            return URLEncoder.encode(s);
        }
    }

}
