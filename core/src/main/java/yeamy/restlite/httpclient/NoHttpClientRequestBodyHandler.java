package yeamy.restlite.httpclient;

import org.apache.hc.core5.http.HttpEntity;

/**
 * default HttpClientRequestBodyHandler return null
 */
public final class NoHttpClientRequestBodyHandler implements HttpClientRequestBodyHandler<Object> {

    @Override
    public HttpEntity createEntity(Object data, String contentType) {
        return null;
    }

}
