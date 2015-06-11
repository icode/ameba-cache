package ameba.cache.engine.ehcache;

import ameba.cache.CacheEngine;
import ameba.util.IOUtils;
import com.google.common.collect.Maps;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.FeatureContext;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

/**
 * @author icode
 */
public class EhcacheEngine<K, V> extends CacheEngine<K, V> {

    private CacheManager cacheManager;

    private net.sf.ehcache.Cache cache;

    public EhcacheEngine() {
        URL cfgUrl = IOUtils.getResource("conf/ehcache.xml");
        if (cfgUrl != null)
            this.cacheManager = CacheManager.create(cfgUrl);
        else
            this.cacheManager = CacheManager.create();
    }

    public EhcacheEngine(String cacheName) {
        this();
        cacheManager.addCache(cacheName);
        cache = cacheManager.getCache(cacheName);
    }

    public static <K, V> EhcacheEngine<K, V> createEngine(String cacheName) {
        return new EhcacheEngine<>(cacheName);
    }

    public static <K, V> EhcacheEngine<K, V> createEngine() {
        return createEngine(DEFAULT_CACHE_NAME);
    }

    @Override
    public void add(K key, V value, int expiration) {
        syncAdd(key, value, expiration);
    }

    @Override
    public boolean syncAdd(K key, V value, int expiration) {
        return cache.putIfAbsent(createElement(key, value, expiration)) == null;
    }

    @Override
    public void set(K key, V value, int expiration) {
        cache.put(createElement(key, value, expiration));
    }

    private Element createElement(K key, V value, int expiration) {
        Element element = new Element(key, value);
        configureElement(element, expiration);
        return element;
    }

    private void configureElement(Element element, int expiration) {
        if (expiration > 0) {
            element.setTimeToLive(0);
            element.setTimeToIdle(expiration);
        }
    }

    @Override
    public boolean syncSet(K key, V value, int expiration) {
        try {
            set(key, value, expiration);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void replace(K key, V value, int expiration) {
        set(key, value, expiration);
    }

    @Override
    public boolean syncReplace(K key, V value, int expiration) {
        // 不存在返回false，并执行设置
        return get(key) != null & syncSet(key, value, expiration);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <O> O get(K key) {
        Element e = cache.getQuiet(key);
        if (e != null) {
            return (O) e.getObjectValue();
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <O> O gat(K key, int expiration) {
        // get method auto update AccessStatistics
        Element e = cache.get(key);
        if (e == null) return null;
        if (expiration > 0) {
            e.setTimeToIdle(expiration);
            cache.flush();
        }
        return (O) e.getObjectValue();
    }

    @Override
    public boolean touch(K key, int expiration) {
        // get method auto update AccessStatistics
        return cache.get(key) != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<K, V> get(K... keys) {
        Map<Object, Element> map = cache.getAll(Arrays.asList(keys));
        Map<K, V> result = Maps.newLinkedHashMap();
        for (Map.Entry entry : map.entrySet()) {
            result.put((K) entry.getKey(), (V) entry.getValue());
        }
        return result;
    }

    @Override
    public void incr(K key, int by, long initial, int expirationInSecs) {
        syncIncr(key, by, initial, expirationInSecs);
    }

    @Override
    public void decr(K key, int by, long initial, int expirationInSecs) {
        syncDecr(key, by, initial, expirationInSecs);
    }

    @Override
    public long syncIncr(K key, int by, long initial, int expirationInSecs) {
        Element e = cache.getQuiet(key);
        long newValue = initial;
        if (e != null) {
            newValue = ((Number) e.getObjectValue()).longValue() + by;
        }
        return _syncSet(key, newValue, expirationInSecs) ? newValue : -1;
    }

    private boolean _syncSet(K key, long value, int expiration) {
        try {
            Element element = new Element(key, value);
            configureElement(element, expiration);
            cache.put(element);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public long syncDecr(K key, int by, long initial, int expirationInSecs) {
        Element e = cache.getQuiet(key);
        long newValue = initial;
        if (e != null) {
            newValue = ((Number) e.getObjectValue()).longValue() - by;
        }
        return _syncSet(key, newValue, expirationInSecs) ? newValue : -1;
    }

    @Override
    public void clear() {
        cache.removeAll();
    }

    @Override
    public void delete(K key) {
        cache.remove(key);
    }

    @Override
    public boolean syncDelete(K key) {
        return cache.remove(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<K, Boolean> syncDelete(K... keys) {
        Map<K, Boolean> result = Maps.newLinkedHashMap();
        cache.removeAll(Arrays.asList(keys));
        for (K key : keys) {
            result.put(key, true);
        }

        return result;
    }

    @Override
    public Map<K, Boolean> syncSet(Map<K, V> map, int expirationInSecs) {
        Map<K, Boolean> result = Maps.newLinkedHashMap();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            result.put(entry.getKey(), syncSet(entry.getKey(), entry.getValue(), expirationInSecs));
        }

        return result;
    }

    @Override
    public void shutdown() {
        cacheManager.shutdown();
    }

    @Override
    protected void configure(FeatureContext context) {
        Map<String, Object> properties = context.getConfiguration().getProperties();
        String cacheName = StringUtils.defaultIfBlank((String) properties.get("cache.name"), DEFAULT_CACHE_NAME);
        cacheManager.addCache(cacheName);
        cache = cacheManager.getCache(cacheName);
    }

    @Override
    public <KEY, VALUE> CacheEngine<KEY, VALUE> create(String name) {
        return createEngine(name);
    }
}
