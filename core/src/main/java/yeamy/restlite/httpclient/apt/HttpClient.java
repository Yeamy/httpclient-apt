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
 * Declare an interface to generate an implements class
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)// For inherit
public @interface HttpClient {

    /**
     * @return name of class to generate,
     * if empty, class name will be like interface name with suffix "Impl"
     */
    String className() default "";

    /**
     * @return Whether create a single instance constant field: INSTANCE;
     */
    boolean createConstant() default true;

    /**
     * @return Class name of the SerializeAdapter.
     * @see SerializeAdapter
     */
    Class<?> serializeAdapter() default NoSerializeAdapter.class;

    /**
     * @return Class name of HttpClientResponseHandler.
     * @see HttpClientResponseHandler
     */
    Class<?> responseHandler() default NoHttpClientResponseHandler.class;

    /**
     * Max execute times if throw Exceptions.
     *
     * @return min set to 1, if set to 0(default) using 3
     */
    int maxTryTimes() default 0;

    /**
     * @return Http version
     */
    Protocol protocol() default Protocol.NOT_DEFINED;

    /**
     * Support custom method, suggest to return one of the standard uppercase http method
     *
     * @return The http method, if empty used "GET" or "POST"(if contains body)
     */
    String method() default "";

    /**
     * @return Base uri of request.<br>
     * The full uri is HttpClient.uri() + HttpClientRequest.uri()
     */
    String uri() default "";

    /**
     * Add common header of request.
     *
     * @return array of headers
     */
    Values[] header() default {};

    /**
     * Add common cookie of request.
     *
     * @return array of cookie
     */
    Values[] cookie() default {};

}
