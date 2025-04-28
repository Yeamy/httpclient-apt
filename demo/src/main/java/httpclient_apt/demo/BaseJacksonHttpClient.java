package httpclient_apt.demo;

import yeamy.restlite.httpclient.JacksonRequestHandler;
import yeamy.restlite.httpclient.JacksonResponseHandler;
import yeamy.restlite.httpclient.apt.HttpClient;
import yeamy.restlite.httpclient.apt.Values;

@HttpClient(
        responseHandler = JacksonResponseHandler.class,
        requestBodyHandler = JacksonRequestHandler.class,
        maxTryTimes = 2,
        uri = "http://localhost:8080",
        header = @Values(name = "user-agent", value = "custom-app/1.0"),
        cookie = {@Values(name = "1", value = "2"),
                @Values(name = "3", value = "4")})
public @interface BaseJacksonHttpClient {
}
