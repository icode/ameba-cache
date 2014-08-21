package ameba.cache;

import javax.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author icode
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@NameBinding
public @interface Cached {
    String value();
    boolean safe() default false;
}