package yeamy.restlite.httpclient;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.hc.client5.http.entity.mime.ContentBody;
import org.apache.hc.core5.http.HttpEntity;

import java.io.IOException;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Adapter to serialize request data and deserialize data from response
 */
public interface SerializeAdapter {
    FastDateFormat TIME_FORMAT = FastDateFormat.getInstance("HH:mm:ss", TimeZone.getDefault(), Locale.getDefault());
    FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd", TimeZone.getDefault(), Locale.getDefault());
    FastDateFormat DATE_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss", TimeZone.getDefault(), Locale.getDefault());

    /**
     * do serialize request data
     *
     * @param data data to serialize
     */
    ContentBody serializeAsBody(Object data) throws IOException;

    /**
     * do serialize request data as MultiPart content
     *
     * @param data data to serialize
     * @param contentType content type of part
     */
    HttpEntity serializeAsPart(Object data, String contentType) throws IOException;

}
