package yeamy.restlite.httpclient;

import org.apache.hc.core5.http.HttpEntity;

/**
 * Adapter to serialize request data and deserialize data from response
 */
public interface HttpClientRequestBodyHandler<T> {

    /**
     * do serialize request data as body/multipart content
     *
     * @param data data to serialize
     */
    HttpEntity createEntity(T data, String contentType);

}
