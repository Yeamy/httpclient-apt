package yeamy.restlite.httpclient.apt;

import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import yeamy.restlite.httpclient.HttpClientRequestBodyHandler;
import yeamy.restlite.httpclient.NoHttpClientRequestBodyHandler;
import yeamy.restlite.httpclient.NoHttpClientResponseHandler;

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
     * Override the requestBodyHandler to serialize request body.
     *
     * @return class implement HttpClientRequestBodyHandler
     * @see HttpClientRequestBodyHandler
     */
    Class<? extends HttpClientRequestBodyHandler> requestBodyHandler() default NoHttpClientRequestBodyHandler.class;

    /**
     * Override the responseHandler to deserialize response body.
     *
     * @return class implement HttpClientResponseHandler
     * @see HttpClientResponseHandler
     */
    Class<? extends HttpClientResponseHandler> responseHandler() default NoHttpClientResponseHandler.class;

    /**
     * Override the max execute times.
     *
     * @return max execute times
     * @see HttpClient#maxTryTimes()
     */
    int maxTryTimes() default 0;

    /**
     * If no defined will use httpclient default protocol.
     *
     * @return http version
     */
    Protocol protocol() default Protocol.NOT_DEFINED;

    /**
     * Override the http method(support custom method), if empty used "GET" or "POST"(if {@link #body()} not empty)
     *
     * @return may be one of the standard uppercase http method
     */
    String method() default "";

    /**
     * Sub uri of request.
     * The full url = {@link HttpClient#uri()} + HttpClientRequest.uri()<br>
     * Allow parameters in {}, or {{}}
     *
     * @return Sub uri of request.
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
