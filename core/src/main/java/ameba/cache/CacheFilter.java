package ameba.cache;

import ameba.util.Times;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import java.lang.annotation.Annotation;

/**
 * @author icode
 */
public abstract class CacheFilter {

    public static final String REQ_PROPERTY_KEY = Cache.class.getName() + "." + CacheEngine.DEFAULT_CACHE_NAME;
    public static final String REQ_PROPERTY_ANNOTATION_KEY = Cache.class.getName() + "."
            + CacheEngine.DEFAULT_CACHE_NAME + "CACHE_ANNOTATION";

    @Context
    protected ResourceInfo resourceInfo;

    protected static int parseExpiration(Cached cached) {
        return StringUtils.isNotBlank(cached.value()) ? Times.parseDuration(cached.value()) : cached.expiration();
    }

    protected Cached getCacheAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Cached) {
                return (Cached) annotation;
            }
        }
        return null;
    }

    protected Cached getCacheAnnotation() {
        Annotation[] annotations = resourceInfo.getResourceMethod().getDeclaredAnnotations();

        Cached cached = getCacheAnnotation(annotations);

        if (cached == null) {
            cached = getCacheAnnotation(resourceInfo.getResourceClass().getDeclaredAnnotations());
        }
        return cached;
    }
}
