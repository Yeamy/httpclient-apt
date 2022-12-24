package yeamy.restlite.httpclient.apt;

public @interface PartValues {

    String value();

    /**
     * may not be empty when multipart
     */
    String name() default "";

    String contentType() default "";

    String filename() default "";
}
