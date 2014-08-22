package ameba.cache;

import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author icode
 */
@Retention(RetentionPolicy.RUNTIME)
@NameBinding
public @interface Cached {
    String value() default "";

    int expiration() default 0;

    boolean sync() default false;

    String key() default "";

    boolean touch() default false;

    boolean keyWithQuery() default true;
}