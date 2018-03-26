package ameba.cache;

/**
 * @author icode
 */
public interface CacheValue<V> {
    void add(V value);

    void add(V value, int expiration);

    boolean syncAdd(V value);

    boolean syncAdd(V value, int expiration);

    void set(V value);

    void set(V value, int expiration);

    boolean syncSet(V value);

    boolean syncSet(V value, int expiration);

    void replace(V value);

    void replace(V value, int expiration);

    boolean syncReplace(V value);

    boolean syncReplace(V value, int expiration);

    V get();

    V gat();

    V gat(int expiration);

    boolean touch();

    boolean touch(int expiration);

    long syncIncr();

    long syncIncr(int by, final long initial);

    long syncIncr(int by, final long initial, final int expirationInSecs);

    long syncDecr();

    long syncDecr(int by, final long initial);

    long syncDecr(int by, final long initial, final int expirationInSecs);

    void delete();

    boolean syncDelete();
}
