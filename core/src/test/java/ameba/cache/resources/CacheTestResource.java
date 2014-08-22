package ameba.cache.resources;

import ameba.cache.Cached;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * @author icode
 */
@Path("sub")
public class CacheTestResource {
    private static final Logger logger = LoggerFactory.getLogger(CacheTestResource.class);

    @GET
    @Path("1")
    @Cached("10s")
    public String sub1() {
        logger.debug("GET sub/1");
        return "11111111111111111111";
    }

    @GET
    @Path("2")
    @Cached
    public String sub2() {
        logger.debug("GET sub/2");
        return "2222222222222222222";
    }

    @GET
    @Path("3")
    @Cached("10s")
    public String sub3() {
        logger.debug("GET sub/3");
        return "3333333333333333333333";
    }

    @GET
    @Path("4")
    @Cached(value = "5s", touch = true)
    public String sub4() {
        logger.debug("GET sub/4");
        return "44444444444444";
    }

    @GET
    @Path("5")
    @Cached(expiration = 5, touch = true)
    public String sub5() {
        logger.debug("GET sub/5");
        return "555555555555555555";
    }

    @GET
    @Path("6")
    @Cached(key = "sub6")
    public String sub6() {
        logger.debug("GET sub/6");
        return "666666666666666666666666";
    }

    @GET
    @Path("7")
    @Cached(sync = true)
    public String sub7() {
        logger.debug("GET sub/7");
        return "777777777";
    }
}
