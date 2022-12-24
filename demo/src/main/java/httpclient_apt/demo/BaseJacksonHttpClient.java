package httpclient_apt.demo;

import yeamy.restlite.httpclient.apt.HttpClient;
import yeamy.restlite.httpclient.apt.Values;

@HttpClient(
        responseHandler = "yeamy.restlite.httpclient.JacksonResponseHandler",
        serializeAdapter = "yeamy.restlite.httpclient.JacksonRequestAdapter",
        maxTryTimes = 2,
        uri = "http://localhost:8080",
        header = @Values(name = "user-agent", value = "custom-app/1.0"),
        cookie = {@Values(name = "1", value = "2"),
                @Values(name = "3", value = "4")})
public @interface BaseJacksonHttpClient {
}
