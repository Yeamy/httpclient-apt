package httpclient_apt.demo;

import yeamy.restlite.httpclient.GsonRequestAdapter;
import yeamy.restlite.httpclient.GsonResponseHandler;
import yeamy.restlite.httpclient.apt.HttpClient;
import yeamy.restlite.httpclient.apt.HttpClientRequest;
import yeamy.restlite.httpclient.apt.PartValues;
import yeamy.restlite.httpclient.apt.Values;

import java.util.List;
import java.util.Map;

@HttpClient(
        responseHandler = GsonResponseHandler.class,
        serializeAdapter = GsonRequestAdapter.class,
        uri = "http://localhost:8080",
        maxTryTimes = 1,
        header = @Values(name = "user-agent", value = "custom-app/1.0"),
        cookie = {@Values(name = "1", value = "2"),
                @Values(name = "3", value = "4")})
public interface DemoClient {

    @HttpClientRequest(uri = "/baidu")
    Object get();

    @HttpClientRequest(uri = "/a/{u1}?x={{u2}}",
            cookie = @Values(name = "v", value = "{c1}"),
            headerMap = "{m1}",
            cookieMap = "{m2}",
            header = @Values(name = "h", value = "{h1}"),
            body = {@PartValues(name = "name", value = "body", contentType = "text/plain", filename = "f1"),
                    @PartValues(name = "bd1", value = "{body}", contentType = "text/plain")}
    )
    String getPo(String u1, String u2, String c1, String h1, String body,
                 Map<?, String> m1, Map<String, List<String>> m2);

    //    @HttpClientRequest(uri = "/a", method = "POST")
    String post(String a);
}
