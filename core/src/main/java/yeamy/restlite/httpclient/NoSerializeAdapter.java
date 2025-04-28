package yeamy.restlite.httpclient;

import org.apache.hc.core5.http.HttpEntity;

/**
 * default SerializeAdapter return null
 */
public final class NoSerializeAdapter implements SerializeAdapter<Object> {

    @Override
    public HttpEntity doSerialize(Object data, String contentType) {
        return null;
    }

}
