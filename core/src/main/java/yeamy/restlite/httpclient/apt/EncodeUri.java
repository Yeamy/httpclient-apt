package yeamy.restlite.httpclient.apt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declare if to encode the uri parameter.
 * Default is true even if no annotation
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
public @interface EncodeUri {
    boolean value() default true;
}
