package yeamy.restlite.httpclient;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import java.io.IOException;

public final class NoHttpClientResponseHandler implements HttpClientResponseHandler<Object> {
    @Override
    public Object handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
        return null;
    }
}
