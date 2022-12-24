package yeamy.restlite.httpclient;

import org.apache.hc.client5.http.entity.mime.ContentBody;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import java.io.IOException;

/**
 * Adapter to serialize request data and deserialize data from response
 */
public interface SerializeAdapter {
    /**
     * do serialize request data
     *
     * @param data data to serialize
     */
    HttpEntity serializeAsBody(Object data, String contentType) throws IOException;

    /**
     * do serialize request data as MultiPart content
     *
     * @param data data to serialize
     */
    ContentBody serializeAsPart(Object data) throws IOException;

}
