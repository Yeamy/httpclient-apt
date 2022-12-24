package httpclient_apt.demo;

import yeamy.restlite.httpclient.apt.HttpClient;
import yeamy.restlite.httpclient.apt.HttpClientRequest;
import yeamy.restlite.httpclient.apt.PartValues;
import yeamy.restlite.httpclient.apt.Values;

@HttpClient(
        responseHandler = "yeamy.restlite.httpclient.GsonResponseHandler",
        serializeAdapter = "yeamy.restlite.httpclient.GsonRequestAdapter",
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
            header = @Values(name = "h", value = "{h1}"),
            body = {@PartValues(name = "name", value = "{body}")}
    )
    String getPo(String u1, String u2, String c1, String h1, String body);

    //    @HttpClientRequest(uri = "/a", method = "POST")
    String post(String a);
}
