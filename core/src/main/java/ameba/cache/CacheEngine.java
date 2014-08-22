package ameba.cache;

import javax.ws.rs.core.FeatureContext;
import java.util.Map;

/**
 * 缓存引擎接口
 *
 * @author icode
 */
public abstract class CacheEngine<K, V> {

    public static final String DEFAULT_CACHE_NAME = "AMEBA_CACHE";


    public abstract void add(K key, V value, int expiration);

    public abstract boolean syncAdd(K key, V value, int expiration);

    public abstract void set(K key, V value, int expiration);

    public abstract boolean syncSet(K key, V value, int expiration);

    public abstract void replace(K key, V value, int expiration);

    public abstract boolean syncReplace(K key, V value, int expiration);

    public abstract <O> O get(K key);

    public abstract <O> O gat(K key, int expiration);

    public abstract boolean touch(K key, int expiration);

    public abstract Map<K, V> get(K... keys);

    public abstract void incr(K key, int by, final long initial, final int expirationInSecs);

    public abstract void decr(K key, int by, final long initial, final int expirationInSecs);

    public abstract long syncIncr(K key, int by, final long initial, final int expirationInSecs);

    public abstract long syncDecr(K key, int by, final long initial, final int expirationInSecs);

    public abstract void clear();

    public abstract void delete(K key);

    public abstract boolean syncDelete(K key);

    public abstract void stop();

    protected abstract void configure(FeatureContext context);
}
