package ameba.cache;

import ameba.util.ClassUtils;
import ameba.util.Times;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.FeatureContext;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.Map;

/**
 * 缓存
 *
 * @author icode
 */
public class Cache {

    /**
     * 缓存引擎
     */
    static CacheEngine<String, Object> cacheEngine;

    private Cache() {

    }

    /**
     * Add an element only if it doesn't exist.
     *
     * @param key        Element key
     * @param value      Element value
     * @param expiration Ex: 10s, 3mn, 8h
     */
    public static void add(String key, Object value, String expiration) {
        checkSerializable(value);
        cacheEngine.add(key, value, Times.parseDuration(expiration));
    }

    /**
     * Add an element only if it doesn't exist, and return only when
     * the element is effectively cached.
     *
     * @param key        Element key
     * @param value      Element value
     * @param expiration Ex: 10s, 3mn, 8h
     * @return If the element an eventually been cached
     */
    public static boolean syncAdd(String key, Object value, String expiration) {
        checkSerializable(value);
        return cacheEngine.syncAdd(key, value, Times.parseDuration(expiration));
    }

    /**
     * Add an element only if it doesn't exist and store it indefinitely.
     *
     * @param key   Element key
     * @param value Element value
     */
    public static void add(String key, Object value) {
        checkSerializable(value);
        cacheEngine.add(key, value, 0);
    }

    /**
     * Set an element.
     *
     * @param key        Element key
     * @param value      Element value
     * @param expiration Ex: 10s, 3mn, 8h, 2d 5h 30min
     */
    public static void set(String key, Object value, String expiration) {
        checkSerializable(value);
        cacheEngine.set(key, value, Times.parseDuration(expiration));
    }

    /**
     * Set an element and return only when the element is effectively cached.
     *
     * @param key        Element key
     * @param value      Element value
     * @param expiration Ex: 10s, 3mn, 8h, 2d 5h 30min
     * @return If the element an eventually been cached
     */
    public static boolean syncSet(String key, Object value, String expiration) {
        checkSerializable(value);
        return cacheEngine.syncSet(key, value, Times.parseDuration(expiration));
    }

    /**
     * Set an element and store it indefinitely.
     *
     * @param key   Element key
     * @param value Element value
     */
    public static void set(String key, Object value) {
        checkSerializable(value);
        cacheEngine.set(key, value, 0);
    }

    /**
     * Replace an element only if it already exists.
     *
     * @param key        Element key
     * @param value      Element value
     * @param expiration Ex: 10s, 3mn, 8h, 2d 5h 30min
     */
    public static void replace(String key, Object value, String expiration) {
        checkSerializable(value);
        cacheEngine.replace(key, value, Times.parseDuration(expiration));
    }

    /**
     * Replace an element only if it already exists and return only when the
     * element is effectively cached.
     *
     * @param key        Element key
     * @param value      Element value
     * @param expiration Ex: 10s, 3mn, 8h, 2d 5h 30min
     * @return If the element an eventually been cached
     */
    public static boolean syncReplace(String key, Object value, String expiration) {
        checkSerializable(value);
        return cacheEngine.syncReplace(key, value, Times.parseDuration(expiration));
    }

    /**
     * Replace an element only if it already exists and store it indefinitely.
     *
     * @param key   Element key
     * @param value Element value
     */
    public static void replace(String key, Object value) {
        checkSerializable(value);
        cacheEngine.replace(key, value, 0);
    }

    /**
     * Increment the element value (must be a Number) by 1.
     *
     * @param key Element key
     * @return The new value
     */
    public static void incr(String key) {
        incr(key, 0);
    }

    /**
     * Increment the element value (must be a Number).
     *
     * @param key              Element key
     * @param by               The incr value
     * @param initial          The initial value
     * @param expirationInSecs The expiration
     * @return The new value
     */
    public static void incr(String key, int by, final long initial, final int expirationInSecs) {
        cacheEngine.incr(key, by, initial, expirationInSecs);
    }

    /**
     * Increment the element value (must be a Number) by 1.
     *
     * @param key              Element key
     * @param expirationInSecs The expiration
     * @return The new value
     */
    public static void incr(String key, final int expirationInSecs) {
        incr(key, 1, 1, expirationInSecs);
    }

    /**
     * Increment the element value (must be a Number) by 1.
     *
     * @param key Element key
     * @return The new value
     */
    public static void syncIncr(String key) {
        syncIncr(key, 0);
    }

    /**
     * Increment the element value (must be a Number).
     *
     * @param key              Element key
     * @param by               The incr value
     * @param initial          The initial value
     * @param expirationInSecs The expiration
     * @return The new value
     */
    public static void syncIncr(String key, int by, final long initial, final int expirationInSecs) {
        cacheEngine.syncIncr(key, by, initial, expirationInSecs);
    }

    /**
     * Increment the element value (must be a Number) by 1.
     *
     * @param key              Element key
     * @param expirationInSecs The expiration
     * @return The new value
     */
    public static void syncIncr(String key, final int expirationInSecs) {
        syncIncr(key, 1, 1, expirationInSecs);
    }


    public static void add(String key, Object value, int expiration) {
        checkSerializable(value);
        cacheEngine.add(key, value, expiration);
    }

    public static boolean syncAdd(String key, Object value, int expiration) {
        checkSerializable(value);
        return cacheEngine.syncAdd(key, value, expiration);
    }

    public static boolean syncSet(String key, Object value, int expiration) {
        checkSerializable(value);
        return cacheEngine.syncSet(key, value, expiration);
    }

    public static Object gat(String key, int expiration) {
        return cacheEngine.gat(key, expiration);
    }

    public static boolean syncReplace(String key, Object value, int expiration) {
        checkSerializable(value);
        return cacheEngine.syncReplace(key, value, expiration);
    }

    public static boolean touch(String key, int expiration) {
        return cacheEngine.touch(key, expiration);
    }

    public static void replace(String key, Object value, int expiration) {
        checkSerializable(value);
        cacheEngine.replace(key, value, expiration);
    }

    public static void set(String key, Object value, int expiration) {
        checkSerializable(value);
        cacheEngine.set(key, value, expiration);
    }

    /**
     * Decrement the element value (must be a Number).
     *
     * @param key              Element key
     * @param by               The decr value
     * @param initial          The initial value
     * @param expirationInSecs The expiration
     * @return The new value
     */
    public static void decr(String key, int by, final long initial, final int expirationInSecs) {
        cacheEngine.decr(key, by, initial, expirationInSecs);
    }

    /**
     * Decrement the element value (must be a Number) by 1.
     *
     * @param key              Element key
     * @param expirationInSecs The expiration
     * @return The new value
     */
    public static void decr(String key, final int expirationInSecs) {
        decr(key, 1, 0, expirationInSecs);
    }

    /**
     * Decrement the element value (must be a Number) by 1.
     *
     * @param key Element key
     * @return The new value
     */
    public static void decr(String key) {
        decr(key, 0);
    }

    /**
     * Decrement the element value (must be a Number).
     *
     * @param key              Element key
     * @param by               The decr value
     * @param initial          The initial value
     * @param expirationInSecs The expiration
     * @return The new value
     */
    public static void syncDecr(String key, int by, final long initial, final int expirationInSecs) {
        cacheEngine.syncDecr(key, by, initial, expirationInSecs);
    }

    /**
     * Decrement the element value (must be a Number) by 1.
     *
     * @param key              Element key
     * @param expirationInSecs The expiration
     * @return The new value
     */
    public static void syncDecr(String key, final int expirationInSecs) {
        syncDecr(key, 1, 0, expirationInSecs);
    }

    /**
     * Decrement the element value (must be a Number) by 1.
     *
     * @param key Element key
     * @return The new value
     */
    public static void syncDecr(String key) {
        syncDecr(key, 0);
    }

    /**
     * Bulk retrieve.
     *
     * @param key List of keys
     * @return Map of keys & values
     */
    public static Map<String, Object> get(String... key) {
        return cacheEngine.get(key);
    }

    /**
     * Delete an element from the cache.
     *
     * @param key The element key
     */
    public static void delete(String key) {
        cacheEngine.delete(key);
    }

    /**
     * Delete an element from the cache and return only when the
     * element is effectively removed.
     *
     * @param key The element key
     * @return If the element an eventually been deleted
     */
    public static boolean syncDelete(String key) {
        return cacheEngine.syncDelete(key);
    }

    /**
     * Clear all data from cache.
     */
    public static void clear() {
        cacheEngine.clear();
    }

    /**
     * Convenient clazz to get a value a class type;
     *
     * @param <T> The needed type
     * @param key The element key
     * @return The element value or null
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        return (T) cacheEngine.get(key);
    }

    /**
     * Stop the cache system.
     */
    public static void stop() {
        cacheEngine.stop();
    }

    /**
     * Utility that check that an object is serializable.
     */
    static void checkSerializable(Object value) {
        if (value != null && !(value instanceof Serializable)) {
            throw new CacheException("Cannot cache a non-serializable value of type " + value.getClass().getName(), new NotSerializableException(value.getClass().getName()));
        }
    }

    public static class Feature implements javax.ws.rs.core.Feature {

        @Override
        @SuppressWarnings("unchecked")
        public boolean configure(FeatureContext context) {
            String engine = (String) context.getConfiguration().getProperty("cache.engine");
            if (StringUtils.isNoneBlank(engine)) {
                cacheEngine = (CacheEngine<String, Object>) ClassUtils.newInstance(engine);
                cacheEngine.configure(context);
                context.register(CacheRequestFilter.class)
                        .register(CacheResponseFilter.class);
                return true;
            } else
                return false;
        }
    }
}

