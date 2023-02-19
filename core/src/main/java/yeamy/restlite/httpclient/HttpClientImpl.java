package yeamy.restlite.httpclient;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.TimeZone;

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
     * @param <T>         any type determined by the response
     * @return response data
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
    private static final int VERSION;
    static {
        boolean b = false;
        try {
            URLEncoder.class.getMethod("encode", String.class, Charset.class);
            b = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        VERSION = b ? 10 : 8;
    }

    /**
     * Translates a string into application/x-www-form-urlencoded format using a specific Charset.
     * This method uses the supplied charset to obtain the bytes for unsafe characters.
     *
     * @param s String to be translated. charset – the given charset
     * @return the translated String.
     */
    public static String encodeUrl(String s) {
        if (VERSION == 10) {
            return URLEncoder.encode(s, Charset.defaultCharset());
        } else {
            return URLEncoder.encode(s);
        }
    }

}
