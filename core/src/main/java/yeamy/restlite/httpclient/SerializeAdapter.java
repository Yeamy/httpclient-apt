package yeamy.restlite.httpclient;

import org.apache.hc.core5.http.HttpEntity;

/**
 * Adapter to serialize request data and deserialize data from response
 */
public interface SerializeAdapter<T> {

    /**
     * do serialize request data as body/multipart content
     *
     * @param data data to serialize
     */
    HttpEntity doSerialize(T data, String contentType);

}
