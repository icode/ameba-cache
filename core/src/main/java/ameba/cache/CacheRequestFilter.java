package ameba.cache;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Priority;
import javax.inject.Singleton;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;

/**
 * @author icode
 */
@Cached
@Singleton
@Priority(Priorities.ENTITY_CODER)
public class CacheRequestFilter extends CacheFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (!HttpMethod.GET.equals(requestContext.getMethod())) return;

        UriInfo uriInfo = requestContext.getUriInfo();

        Cached cached = getCacheAnnotation();


        if (cached != null) {
            String query = uriInfo.getRequestUri().getQuery();
            String cacheKey = DigestUtils.md5Hex(
                    REQ_PROPERTY_KEY
                            + "/"
                            + (StringUtils.isNotBlank(cached.key()) ?
                            cached.key() :
                            uriInfo.getPath())
                            + (cached.keyWithQuery() && query != null ? ("?" + query) : ""));

            Object cache;
            if (cached.touch())
                cache = Cache.gat(cacheKey, parseExpiration(cached));
            else
                cache = Cache.get(cacheKey);

            if (cache != null) {
                requestContext.abortWith(Response.ok(cache).build());
            } else {
                requestContext.setProperty(REQ_PROPERTY_KEY, cacheKey);
                requestContext.setProperty(REQ_PROPERTY_ANNOTATION_KEY, cached);
            }
        }
    }
}
