package ameba.cache.engine.guava;

import ameba.cache.CacheEngine;

import java.util.Map;

/**
 * @author icode
 */
public class GuavaCacheEngine<K,V> implements CacheEngine<K,V> {
    @Override
    public void add(K key, V value, int expiration) {

    }

    @Override
    public boolean safeAdd(K key, V value, int expiration) {
        return false;
    }

    @Override
    public void set(K key, V value, int expiration) {

    }

    @Override
    public boolean safeSet(K key, V value, int expiration) {
        return false;
    }

    @Override
    public void replace(K key, V value, int expiration) {

    }

    @Override
    public boolean safeReplace(K key, V value, int expiration) {
        return false;
    }

    @Override
    public V get(K key) {
        return null;
    }

    @Override
    public V gat(K key, int expiration) {
        return null;
    }

    @Override
    public boolean touch(K key, int expiration) {
        return false;
    }

    @Override
    public Map<K, V> get(K[] keys) {
        return null;
    }

    @Override
    public long incr(K key, int by, long initial, int expirationInSecs) {
        return 0;
    }

    @Override
    public long decr(K key, int by, long initial, int expirationInSecs) {
        return 0;
    }

    @Override
    public void clear() {

    }

    @Override
    public void delete(K key) {

    }

    @Override
    public boolean safeDelete(K key) {
        return false;
    }

    @Override
    public void stop() {

    }
}
