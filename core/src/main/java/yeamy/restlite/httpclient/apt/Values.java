package yeamy.restlite.httpclient.apt;

/**
 * header or cookies value bean for HttpClient or HttpClientRequest
 */
public @interface Values {

    /**
     * @return name of the value
     */
    String name();

    /**
     * @return the value data
     */
    String value();
}
