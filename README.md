ameba-cahe
==========

> ameba缓存模块

## 使用

### 添加引用

#### ehcache 缓存引擎

```
<dependency>
    <groupId>org.amebastack.module</groupId>
    <artifactId>ameba-cache-ehcache</artifactId>
    <version>0.1.0</version>
</dependency>
```

#### memchached 缓存引擎

```
<dependency>
    <groupId>org.amebastack.module</groupId>
    <artifactId>ameba-cache-memcached</artifactId>
    <version>0.1.0</version>
</dependency>
```

### 配置

#### memcached 配置

##### 必须配置项

```
cache.memcached.server.{serverName}=host:port
```

e.g.

```
cache.memcached.server.localhost=127.0.0.1:11211
```

##### 可选配置项

```
# 默认为AMEBA_CACHE
cache.name=String
cache.memcached.blocking=Boolean
cache.memcached.selectorRunnersCount=Integer
cache.memcached.ioStrategy=ClassString
cache.memcached.transport=ClassString
cache.memcached.workerThreadPool=ClassString
cache.memcached.allowDisposableConnection=Boolean
cache.memcached.borrowValidation=Boolean
cache.memcached.failover=Boolean
cache.memcached.connectTimeoutInMillis=Long
cache.memcached.healthMonitorIntervalInSecs=Long
cache.memcached.keepAliveTimeoutInSecs=Long
cache.memcached.maxConnectionPerServer=Integer
cache.memcached.minConnectionPerServer=Integer
cache.memcached.preferRemoteConfig=Boolean
cache.memcached.responseTimeoutInMillis=Long
cache.memcached.returnValidation=Boolean
cache.memcached.writeTimeoutInMillis=Long

#true 才会开启ZooKeeper支持，需要添加zookeeper引用
cache.memcached.zk.enabled=Boolean
```

##### ZooKeeper 必选配置

```
# 在ZooKeeper注册的名称
cache.memcached.zk.name=String
# e.g. 127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002
cache.memcached.zk.servers=String
```

##### ZooKeeper 可选配置

```
cache.memcached.zk.rootPath=String
cache.memcached.zk.connectTimeoutInMillis=Integer
cache.memcached.zk.sessionTimeoutInMillis=Integer
cache.memcached.zk.commitDelayTimeInSecs=Integer
```

### 对父资源进行标记
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
### 对子源进行标记
1. 直接在子资源标记，只应用于该URI上

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