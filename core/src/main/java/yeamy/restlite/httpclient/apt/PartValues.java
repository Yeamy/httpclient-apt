package yeamy.restlite.httpclient.apt;

/**
 * attributes of body
 */
public @interface PartValues {

    /**
     * the value must be string or a variable
     */
    String value();

    /**
     * may not be empty when multipart
     */
    String name() default "";

    /**
     * content type of body.
     */
    String contentType() default "";

    /**
     * Only valid in multipart, may not be empty when contentType is not empty.
     */
    String filename() default "";
}
