package yeamy.restlite.httpclient.apt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface HttpClientRequest {

    /**
     * override the serializeAdapter.
     */
    String serializeAdapter() default "";

    /**
     * override the responseHandler.
     */
    String responseHandler() default "";

    int maxTryTimes() default 0;

    /**
     * if no defined will use httpclient default protocol.
     */
    Protocol protocol() default Protocol.NOT_DEFINED;

    /**
     * Override the http method(support custom method), if empty used "GET"
     * @return may be one of the standard uppercase http method
     */
    String method() default "";

    /**
     * sub uri of current request
     */
    String uri() default "";

    /**
     * Add header of current request.
     */
    Values[] header() default {};

    /**
     * Add header of current request.
     */
    Values[] cookie() default {};

    PartValues[] body() default {};
}
