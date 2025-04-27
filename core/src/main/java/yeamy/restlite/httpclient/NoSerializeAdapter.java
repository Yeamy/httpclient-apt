package yeamy.restlite.httpclient;

import org.apache.hc.client5.http.entity.mime.ContentBody;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;

public final class NoSerializeAdapter implements SerializeAdapter {
    @Override
    public ContentBody serializeAsBody(Object data) throws IOException {
        return new StringBody("", ContentType.TEXT_PLAIN);
    }

    @Override
    public HttpEntity serializeAsPart(Object data, String contentType) throws IOException {
        return new StringEntity("");
    }
}
