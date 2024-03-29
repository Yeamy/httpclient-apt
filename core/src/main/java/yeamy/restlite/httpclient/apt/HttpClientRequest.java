package yeamy.restlite.httpclient.apt;

import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import yeamy.restlite.httpclient.NoHttpClientResponseHandler;
import yeamy.restlite.httpclient.NoSerializeAdapter;
import yeamy.restlite.httpclient.SerializeAdapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * declared the method is a http request method.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface HttpClientRequest {

    /**
     * Override the serializeAdapter or keep it with empty string.
     *
     * @return class implement SerializeAdapter serialize request body
     * @see SerializeAdapter
     */
    Class<? extends SerializeAdapter> serializeAdapter() default NoSerializeAdapter.class;

    /**
     * Override the responseHandler or keep it with empty string.
     *
     * @return class implement HttpClientResponseHandler handle response
     * @see HttpClientResponseHandler
     */
    Class<? extends HttpClientResponseHandler> responseHandler() default NoHttpClientResponseHandler.class;

    /**
     * Override the max execute times.
     *
     * @return max execute times
     */
    int maxTryTimes() default 3;

    /**
     * If no defined will use httpclient default protocol.
     *
     * @return http version
     */
    Protocol protocol() default Protocol.NOT_DEFINED;

    /**
     * Override the http method(support custom method), if empty used "GET"
     *
     * @return may be one of the standard uppercase http method
     */
    String method() default "";

    /**
     * Sub uri of current request
     *
     * @return Sub uri of current request
     */
    String uri() default "";

    /**
     * Add header of current request.
     *
     * @return array of headers
     */
    Values[] header() default {};

    /**
     * Add header of current request in a Map&lt;&gt;.
     *
     * @return param name in method with curly bracket
     */
    String headerMap() default "";

    /**
     * Add header of current request.
     *
     * @return array of cookie
     */
    Values[] cookie() default {};

    /**
     * Add cookie of current request in a Map&lt;&gt;.
     *
     * @return param name in method with curly bracket
     */
    String cookieMap() default "";

    /**
     * Add body of current request, if more than one, the request will turn to multipart.
     *
     * @return array of body
     */
    PartValues[] body() default {};
}
