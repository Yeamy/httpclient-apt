package yeamy.restlite.httpclient;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import java.net.URLEncoder;
import java.nio.charset.Charset;

public class HttpClientImpl {
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

    public static String encodeUrl(String s) {
        return URLEncoder.encode(s, Charset.defaultCharset());
    }

}
