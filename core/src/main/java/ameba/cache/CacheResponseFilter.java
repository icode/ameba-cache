package ameba.cache;

import javax.annotation.Priority;
import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;

/**
 * @author icode
 */
@Cached
@Singleton
@Priority(Integer.MAX_VALUE)
public class CacheResponseFilter extends CacheFilter implements WriterInterceptor {
    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        String cacheKey = (String) context.getProperty(REQ_PROPERTY_KEY);

        Cached cached = (Cached) context.getProperty(REQ_PROPERTY_ANNOTATION_KEY);

        if (cacheKey != null && cached != null) {
            int exp = parseExpiration(cached);

            if (cached.sync()) {
                Cache.syncSet(cacheKey, context.getEntity(), exp);
            } else {
                Cache.set(cacheKey, context.getEntity(), exp);
            }
        }

        context.proceed();
    }
}
