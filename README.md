ameba-cahe
==========

> ameba缓存模块

# 使用

1. 对于基础资源缓存，缓存将因用于所有子资源
2. 子资缓存配置会替换父资源缓存配置

```
@Path("cached")
@Cached
public class CachedResource {
    @GET
    @Path("1")
    public String cache1() {
        return "cache1";
    }
    
    @GET
    @Path("2")
    @Cached("10s")
    public String cache2() {
        return "cache1";
    }
}
```

3. 直接在子资源标记，只应用于该URI上

```
@Path("cache")
public class CacheResource {
    @GET
    @Path("1")
    @Cached("10s")
    public String cache() {
        return "cache";
    }
}
```