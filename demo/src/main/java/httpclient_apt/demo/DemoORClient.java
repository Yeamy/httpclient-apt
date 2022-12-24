package httpclient_apt.demo;

import yeamy.restlite.httpclient.apt.HttpClient;
import yeamy.restlite.httpclient.apt.HttpClientRequest;
import yeamy.restlite.httpclient.apt.PartValues;
import yeamy.restlite.httpclient.apt.Values;

@BaseGsonHttpClient
@HttpClient(maxTryTimes = 2)
public interface DemoORClient {

    @HttpClientRequest(
            uri = "/baidu",
            maxTryTimes = 3)
    Object get();

    @HttpClientRequest(
            uri = "/a/{u1}?x={{u2}}",
            cookie = @Values(name = "v", value = "{c1}"),
            header = @Values(name = "h", value = "{h1}"),
            body = {@PartValues(name = "name", value = "{body}")}
    )
    String getPo(String u1, String u2, String c1, String h1, String body);

    //    @HttpClientRequest(uri = "/a", method = "POST")
    String post(String a);
}
