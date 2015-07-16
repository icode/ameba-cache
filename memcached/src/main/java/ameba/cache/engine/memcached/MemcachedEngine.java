package ameba.cache.engine.memcached;

import ameba.cache.CacheEngine;
import ameba.cache.CacheException;
import ameba.core.Application;
import ameba.util.ClassUtils;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.grizzly.memcached.GrizzlyMemcachedCache;
import org.glassfish.grizzly.memcached.GrizzlyMemcachedCacheManager;
import org.glassfish.grizzly.memcached.ValueWithCas;
import org.glassfish.grizzly.memcached.ValueWithKey;
import org.glassfish.grizzly.memcached.zookeeper.ZooKeeperConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.core.FeatureContext;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author icode
 */
public class MemcachedEngine<K, V> extends CacheEngine<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(MemcachedEngine.class);
    private org.glassfish.grizzly.memcached.MemcachedCache<K, V> cache;
    private Map<String, Object> properties;
    @Inject
    private Application app;

    private MemcachedEngine(org.glassfish.grizzly.memcached.MemcachedCache<K, V> cache) {
        this.cache = cache;
    }

    public MemcachedEngine() {
    }

    public static <K, V> MemcachedEngine<K, V> create(Set<SocketAddress> servers) {
        return new MemcachedEngine<>(MemcachedCache.<K, V>create(servers));
    }

    public static <K, V> MemcachedEngine<K, V> create(GrizzlyMemcachedCacheManager manager, Set<SocketAddress> servers) {
        return new MemcachedEngine<>(MemcachedCache.<K, V>create(manager, servers));
    }

    public static <K, V> MemcachedEngine<K, V> create(String cacheName, GrizzlyMemcachedCacheManager manager, Set<SocketAddress> servers) {
        return new MemcachedEngine<>(MemcachedCache.<K, V>create(cacheName, manager, servers));
    }

    public static <K, V> MemcachedEngine<K, V> create(org.glassfish.grizzly.memcached.MemcachedCache<K, V> cache) {
        return new MemcachedEngine<>(cache);
    }

    @Override
    public void add(K key, V value, int expiration) {
        cache.add(key, value, expiration, true);
    }

    @Override
    public boolean syncAdd(K key, V value, int expiration) {
        return cache.add(key, value, expiration, false);
    }

    @Override
    public void set(K key, V value, int expiration) {
        cache.set(key, value, expiration, true);
    }

    @Override
    public boolean syncSet(K key, V value, int expiration) {
        return cache.set(key, value, expiration, false);
    }

    @Override
    public void replace(K key, V value, int expiration) {
        cache.replace(key, value, expiration, true);
    }

    @Override
    public boolean syncReplace(K key, V value, int expiration) {
        return cache.replace(key, value, expiration, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <O> O get(K key) {
        return (O) cache.get(key, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<K, V> get(K... keys) {
        return cache.getMulti(keys == null ? null : Sets.newLinkedHashSet(Arrays.asList(keys)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <O> O gat(K key, int expiration) {
        return (O) cache.gat(key, expiration, false);
    }

    @Override
    public void incr(K key, int by, final long initial, final int expirationInSecs) {
        cache.incr(key, by, initial, expirationInSecs, true);
    }

    @Override
    public void decr(K key, int by, final long initial, final int expirationInSecs) {
        cache.decr(key, by, initial, expirationInSecs, true);
    }

    @Override
    public long syncIncr(K key, int by, long initial, int expirationInSecs) {
        return cache.incr(key, by, initial, expirationInSecs, false);
    }

    @Override
    public long syncDecr(K key, int by, long initial, int expirationInSecs) {
        return cache.decr(key, by, initial, expirationInSecs, false);
    }

    @Override
    public void clear() {
        for (SocketAddress address : cache.getCurrentServerList()) {
            cache.flushAll(address, 0, true);
        }
    }

    @Override
    public void delete(K key) {
        cache.delete(key, true);
    }

    @Override
    public boolean syncDelete(K key) {
        return cache.delete(key, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<K, Boolean> syncDelete(K... keys) {
        return cache.deleteMulti(keys == null ? null : Sets.newLinkedHashSet(Arrays.asList(keys)));
    }

    @Override
    public Map<K, Boolean> syncSet(Map<K, V> map, int expirationInSecs) {
        return cache.setMulti(map, expirationInSecs);
    }

    @Override
    public void shutdown() {
        this.cache.stop();
    }

    @Override
    public <KEY, VALUE> CacheEngine<KEY, VALUE> create(String name) {
        return new MemcachedEngine<>(this.<KEY, VALUE>_create(name));
    }

    @Override
    public boolean touch(K key, int expirationInSecs) {
        return cache.touch(key, expirationInSecs);
    }

    public boolean set(K key, V value, int expirationInSecs, boolean noReply, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.set(key, value, expirationInSecs, noReply, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public long decr(K key, long delta, long initial, int expirationInSecs, boolean noReply, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.decr(key, delta, initial, expirationInSecs, noReply, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public boolean noop(SocketAddress address, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.noop(address, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public String version(SocketAddress address) {
        return cache.version(address);
    }

    public Map<String, String> statsItems(SocketAddress address, String item, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.statsItems(address, item, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public boolean touch(K key, int expirationInSecs, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.touch(key, expirationInSecs, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public boolean quit(SocketAddress address, boolean noReply) {
        return cache.quit(address, noReply);
    }

    public boolean add(K key, V value, int expirationInSecs, boolean noReply, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.add(key, value, expirationInSecs, noReply, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public boolean verbosity(SocketAddress address, int verbosity, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.verbosity(address, verbosity, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public long incr(K key, long delta, long initial, int expirationInSecs, boolean noReply) {
        return cache.incr(key, delta, initial, expirationInSecs, noReply);
    }

    public boolean replace(K key, V value, int expirationInSecs, boolean noReply, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.replace(key, value, expirationInSecs, noReply, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public boolean append(K key, V value, boolean noReply, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.append(key, value, noReply, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public V gat(K key, int expirationInSecs, boolean noReplys) {
        return cache.gat(key, expirationInSecs, noReplys);
    }

    public boolean append(K key, V value, boolean noReply) {
        return cache.append(key, value, noReply);
    }

    public Map<K, Boolean> casMulti(Map<K, ValueWithCas<V>> map, int expirationInSecs) {
        return cache.casMulti(map, expirationInSecs);
    }

    public V get(K key, boolean noReply) {
        return cache.get(key, noReply);
    }

    public boolean delete(K key, boolean noReply, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.delete(key, noReply, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public boolean verbosity(SocketAddress address, int verbosity) {
        return cache.verbosity(address, verbosity);
    }

    public boolean quit(SocketAddress address, boolean noReply, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.quit(address, noReply, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public void removeServer(SocketAddress serverAddress) {
        cache.removeServer(serverAddress);
    }

    public boolean addServer(SocketAddress serverAddress) {
        return cache.addServer(serverAddress);
    }

    public boolean replace(K key, V value, int expirationInSecs, boolean noReply) {
        return cache.replace(key, value, expirationInSecs, noReply);
    }

    public String saslAuth(SocketAddress address, String mechanism, byte[] data) {
        return cache.saslAuth(address, mechanism, data);
    }

    public String version(SocketAddress address, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.version(address, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public boolean isInServerList(SocketAddress serverAddress) {
        return cache.isInServerList(serverAddress);
    }

    public boolean add(K key, V value, int expirationInSecs, boolean noReply) {
        return cache.add(key, value, expirationInSecs, noReply);
    }

    public ValueWithKey<K, V> getKey(K key, boolean noReply) {
        return cache.getKey(key, noReply);
    }

    public Map<K, Boolean> deleteMulti(Set<K> keys, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.deleteMulti(keys, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public boolean prepend(K key, V value, boolean noReply, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.prepend(key, value, noReply, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public Map<String, String> statsItems(SocketAddress address, String item) {
        return cache.statsItems(address, item);
    }

    public ValueWithKey<K, V> getKey(K key, boolean noReply, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.getKey(key, noReply, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public Map<K, V> getMulti(Set<K> keys, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.getMulti(keys, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public boolean cas(K key, V value, int expirationInSecs, long cas, boolean noReply, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.cas(key, value, expirationInSecs, cas, noReply, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public V gat(K key, int expirationInSecs, boolean noReply, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.gat(key, expirationInSecs, noReply, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public Map<String, String> stats(SocketAddress address) {
        return cache.stats(address);
    }

    public Map<K, Boolean> casMulti(Map<K, ValueWithCas<V>> map, int expirationInSecs, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.casMulti(map, expirationInSecs, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public boolean prepend(K key, V value, boolean noReply) {
        return cache.prepend(key, value, noReply);
    }

    public boolean flushAll(SocketAddress address, int expirationInSecs, boolean noReply) {
        return cache.flushAll(address, expirationInSecs, noReply);
    }

    public String saslStep(SocketAddress address, String mechanism, byte[] data) {
        return cache.saslStep(address, mechanism, data);
    }

    public long decr(K key, long delta, long initial, int expirationInSecs, boolean noReply) {
        return cache.decr(key, delta, initial, expirationInSecs, noReply);
    }

    public boolean set(K key, V value, int expirationInSecs, boolean noReply) {
        return cache.set(key, value, expirationInSecs, noReply);
    }

    public ValueWithCas<V> gets(K key, boolean noReply) {
        return cache.gets(key, noReply);
    }

    public List<SocketAddress> getCurrentServerList() {
        return cache.getCurrentServerList();
    }

    public boolean cas(K key, V value, int expirationInSecs, long cas, boolean noReplys) {
        return cache.cas(key, value, expirationInSecs, cas, noReplys);
    }

    public ValueWithCas<V> gets(K key, boolean noReply, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.gets(key, noReply, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public String getName() {
        return cache.getName();
    }

    public String saslAuth(SocketAddress address, String mechanism, byte[] data, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.saslAuth(address, mechanism, data, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public Map<K, ValueWithCas<V>> getsMulti(Set<K> keys) {
        return cache.getsMulti(keys);
    }

    public boolean delete(K key, boolean noReply) {
        return cache.delete(key, noReply);
    }

    public boolean flushAll(SocketAddress address, int expirationInSecs, boolean noReply, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.flushAll(address, expirationInSecs, noReply, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public boolean noop(SocketAddress addresss) {
        return cache.noop(addresss);
    }

    public Map<K, ValueWithCas<V>> getsMulti(Set<K> keys, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.getsMulti(keys, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public Map<String, String> stats(SocketAddress address, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.stats(address, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public String saslStep(SocketAddress address, String mechanism, byte[] data, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.saslStep(address, mechanism, data, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public Map<K, Boolean> setMulti(Map<K, V> map, int expirationInSecs, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.setMulti(map, expirationInSecs, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public String saslList(SocketAddress address) {
        return cache.saslList(address);
    }

    public long incr(K key, long delta, long initial, int expirationInSecs, boolean noReply, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.incr(key, delta, initial, expirationInSecs, noReply, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public V get(K key, boolean noReply, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.get(key, noReply, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    public String saslList(SocketAddress address, long writeTimeoutInMillis, long responseTimeoutInMillis) {
        return cache.saslList(address, writeTimeoutInMillis, responseTimeoutInMillis);
    }

    @Override
    protected void configure(FeatureContext context) {
        properties = context.getConfiguration().getProperties();

        String cacheName = StringUtils.defaultIfBlank((String) properties.get("cache.name"), DEFAULT_CACHE_NAME);

        this.cache = _create(cacheName);
    }

    private void configureZooKeeper(Map<String, Object> properties, GrizzlyMemcachedCacheManager.Builder builder) {
        String zooKeeperName = (String) properties.get("cache.memcached.zk.name");

        if (StringUtils.isBlank(zooKeeperName)) {
            zooKeeperName = app.getApplicationName();
            logger.info("ZooKeeper not set cache.memcached.zk.name config, now use app name: " + zooKeeperName);
        }


        String zooKeeperServers = (String) properties.get("cache.memcached.zk.servers");

        if (StringUtils.isBlank(zooKeeperServers)) {
            throw new CacheException("enable ZooKeeper must be set cache.memcached.zk.servers config!e.g. 127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002");
        }

        ZooKeeperConfig zooKeeperConfig = ZooKeeperConfig.create(zooKeeperName, zooKeeperServers);

        String rootPath = (String) properties.get("cache.memcached.zk.rootPath");
        if (StringUtils.isBlank(rootPath))
            zooKeeperConfig.setRootPath(rootPath);

        String connectTimeoutInMillis = (String) properties.get("cache.memcached.zk.connectTimeoutInMillis");
        if (StringUtils.isBlank(connectTimeoutInMillis))
            zooKeeperConfig.setConnectTimeoutInMillis(Integer.parseInt(connectTimeoutInMillis));

        String sessionTimeoutInMillis = (String) properties.get("cache.memcached.zk.sessionTimeoutInMillis");
        if (StringUtils.isBlank(sessionTimeoutInMillis))
            zooKeeperConfig.setSessionTimeoutInMillis(Integer.parseInt(sessionTimeoutInMillis));

        String commitDelayTimeInSecs = (String) properties.get("cache.memcached.zk.commitDelayTimeInSecs");
        if (StringUtils.isBlank(commitDelayTimeInSecs))
            zooKeeperConfig.setCommitDelayTimeInSecs(Integer.parseInt(commitDelayTimeInSecs));

        builder.zooKeeperConfig(zooKeeperConfig);
    }

    private void configureCacheManager(Map<String, Object> properties, GrizzlyMemcachedCacheManager.Builder builder) {

        String blocking = (String) properties.get("cache.memcached.blocking");
        if (StringUtils.isNotBlank(blocking))
            builder.blocking(Boolean.parseBoolean(blocking));

        String selectorRunnersCount = (String) properties.get("cache.memcached.selectorRunnersCount");
        if (StringUtils.isNotBlank(selectorRunnersCount))
            builder.selectorRunnersCount(Integer.parseInt(selectorRunnersCount));

        String ioStrategy = (String) properties.get("cache.memcached.ioStrategy");
        if (StringUtils.isNotBlank(ioStrategy))
            builder.ioStrategy((org.glassfish.grizzly.IOStrategy) ClassUtils.newInstance(ioStrategy));

        String transport = (String) properties.get("cache.memcached.transport");
        if (StringUtils.isNotBlank(transport))
            builder.transport((org.glassfish.grizzly.nio.transport.TCPNIOTransport) ClassUtils.newInstance(transport));

        String workerThreadPool = (String) properties.get("cache.memcached.workerThreadPool");
        if (StringUtils.isNotBlank(workerThreadPool))
            builder.workerThreadPool((java.util.concurrent.ExecutorService) ClassUtils.newInstance(workerThreadPool));

        String zooKeeperEnabled = (String) properties.get("cache.memcached.zk.enabled");

        if ("true".equals(zooKeeperEnabled)) {
            configureZooKeeper(properties, builder);
        }
    }

    private <KEY, VALUE> void configureCache(Map<String, Object> properties, GrizzlyMemcachedCache.Builder<KEY, VALUE> cacheBuilder) {
        Set<SocketAddress> servers = Sets.newHashSet();

        for (String key : properties.keySet()) {
            if (key.startsWith("cache.memcached.server.")) {
                String s = (String) properties.get(key);
                if (StringUtils.isNotBlank(s)) {
                    String[] se = s.split(":");
                    if (se.length < 2 || StringUtils.isBlank(se[0]) || StringUtils.isBlank(se[1])) {
                        throw new CacheException("config [" + key + "=" + s + "] is not validate");
                    }

                    int port;
                    try {
                        port = Integer.parseInt(se[1]);
                    } catch (Exception e) {
                        throw new CacheException("config [" + key + "=" + s + "] port is not validate");
                    }

                    servers.add(new InetSocketAddress(se[0], port));
                }
            }
        }

        if (servers.size() == 0) {
            throw new CacheException("not found memecached server, please configure memcached servers at /conf/application.conf .\n" +
                    " e.g. cache.memcached.server.server1=127.0.0.1:11211");
        }

        cacheBuilder.servers(servers);

        String allowDisposableConnection = (String) properties.get("cache.memcached.allowDisposableConnection");
        if (StringUtils.isNotBlank(allowDisposableConnection))
            cacheBuilder.allowDisposableConnection(Boolean.parseBoolean(allowDisposableConnection));

        String borrowValidation = (String) properties.get("cache.memcached.borrowValidation");
        if (StringUtils.isNotBlank(borrowValidation))
            cacheBuilder.borrowValidation(Boolean.parseBoolean(borrowValidation));

        String failover = (String) properties.get("cache.memcached.failover");
        if (StringUtils.isNotBlank(failover))
            cacheBuilder.failover(Boolean.parseBoolean(failover));


        String connectTimeoutInMillis = (String) properties.get("cache.memcached.connectTimeoutInMillis");
        if (StringUtils.isNotBlank(connectTimeoutInMillis))
            cacheBuilder.connectTimeoutInMillis(Long.parseLong(connectTimeoutInMillis));


        String healthMonitorIntervalInSecs = (String) properties.get("cache.memcached.healthMonitorIntervalInSecs");
        if (StringUtils.isNotBlank(healthMonitorIntervalInSecs))
            cacheBuilder.healthMonitorIntervalInSecs(Long.parseLong(healthMonitorIntervalInSecs));


        String keepAliveTimeoutInSecs = (String) properties.get("cache.memcached.keepAliveTimeoutInSecs");
        if (StringUtils.isNotBlank(keepAliveTimeoutInSecs))
            cacheBuilder.keepAliveTimeoutInSecs(Long.parseLong(keepAliveTimeoutInSecs));


        String maxConnectionPerServer = (String) properties.get("cache.memcached.maxConnectionPerServer");
        if (StringUtils.isNotBlank(maxConnectionPerServer))
            cacheBuilder.maxConnectionPerServer(Integer.parseInt(maxConnectionPerServer));


        String minConnectionPerServer = (String) properties.get("cache.memcached.minConnectionPerServer");
        if (StringUtils.isNotBlank(minConnectionPerServer))
            cacheBuilder.minConnectionPerServer(Integer.parseInt(minConnectionPerServer));


        String preferRemoteConfig = (String) properties.get("cache.memcached.preferRemoteConfig");
        if (StringUtils.isNotBlank(preferRemoteConfig))
            cacheBuilder.preferRemoteConfig(Boolean.parseBoolean(preferRemoteConfig));


        String responseTimeoutInMillis = (String) properties.get("cache.memcached.responseTimeoutInMillis");
        if (StringUtils.isNotBlank(responseTimeoutInMillis))
            cacheBuilder.responseTimeoutInMillis(Long.parseLong(responseTimeoutInMillis));


        String returnValidation = (String) properties.get("cache.memcached.returnValidation");
        if (StringUtils.isNotBlank(returnValidation))
            cacheBuilder.returnValidation(Boolean.parseBoolean(returnValidation));


        String writeTimeoutInMillis = (String) properties.get("cache.memcached.writeTimeoutInMillis");
        if (StringUtils.isNotBlank(writeTimeoutInMillis))
            cacheBuilder.writeTimeoutInMillis(Long.parseLong(writeTimeoutInMillis));
    }

    public <KEY, VALUE> org.glassfish.grizzly.memcached.MemcachedCache<KEY, VALUE> _create(String name) {
        GrizzlyMemcachedCacheManager.Builder builder = new GrizzlyMemcachedCacheManager.Builder();

        configureCacheManager(properties, builder);

        GrizzlyMemcachedCacheManager manager = builder.build();


        GrizzlyMemcachedCache.Builder<KEY, VALUE> cacheBuilder = manager.createCacheBuilder(name);

        configureCache(properties, cacheBuilder);

        return cacheBuilder.build();
    }

    public static class MemcachedCache {
        public static <K, V> org.glassfish.grizzly.memcached.MemcachedCache<K, V> create(Set<SocketAddress> servers) {
            return create(new GrizzlyMemcachedCacheManager.Builder().build(), servers);
        }

        public static <K, V> org.glassfish.grizzly.memcached.MemcachedCache<K, V> create(GrizzlyMemcachedCacheManager manager, Set<SocketAddress> servers) {
            return create(DEFAULT_CACHE_NAME, manager, servers);
        }

        public static <K, V> org.glassfish.grizzly.memcached.MemcachedCache<K, V> create(String cacheName, GrizzlyMemcachedCacheManager manager, Set<SocketAddress> servers) {
            // gets the cache builder
            final GrizzlyMemcachedCache.Builder<K, V> builder = manager.createCacheBuilder(cacheName);
            // initializes Memcached's list
            builder.servers(servers);
            // creates the cache
            return create(builder);
        }

        public static <K, V> org.glassfish.grizzly.memcached.MemcachedCache<K, V> create(GrizzlyMemcachedCache.Builder<K, V> builder) {
            return builder.build();
        }
    }

}
