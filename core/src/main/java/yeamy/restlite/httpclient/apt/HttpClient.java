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
 * <pre>{@code
 * @HttpClient(uri = "https://example.com")
 *     serializeAdapter = GsonRequestAdapter.class,
 *     responseHandler = GsonResponseHandler.class
 * )
 * public interface ExampleClient {
 *     @HttpClientRequest(uri = "/a/{p1}?x={{ keepWithoutUriEncode }}")
 *     String get(String p1, String keepWithoutUriEncode);
 *
 *     @HttpClientRequest(uri = "/login",
 *             header = @Values(name = "Content-Type", value = "application/x-www-form-urlencoded"),
 *             body = @PartValues(value = "name={p1}&pwd={p2}")
 *     )
 *    String login(String p1, String p2);
 * }
 * }</pre>
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)// For inherit
public @interface HttpClient {

    /**
     * name of class to generate. if empty, class name will be interface's qualified name with suffix "Impl".<br>
     * eg. com.example.Interface -> com.example.InterfaceImpl
     *
     * @return name of class to generate,
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
    Class<? extends SerializeAdapter> serializeAdapter() default NoSerializeAdapter.class;

    /**
     * Deserialize the response data from httpClient.
     * @return Class name of HttpClientResponseHandler.
     * @see HttpClientResponseHandler
     */
    Class<? extends HttpClientResponseHandler> responseHandler() default NoHttpClientResponseHandler.class;

    /**
     * Max execute times if throw Exceptions.
     *
     * @return min set to 1, default is 3
     */
    int maxTryTimes() default 3;

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
     * Base uri of request.<br>
     * The full url = HttpClient.uri() + {@link HttpClientRequest#uri()}
     *
     * @return Base uri of request.
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
