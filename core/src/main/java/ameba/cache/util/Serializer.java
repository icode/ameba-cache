package ameba.cache.util;

/**
 * @author icode
 */
public interface Serializer {
    byte[] asBytes(final Object object);

    <O> O asObject(final byte[] bytes);

    void registerClass(Class clazz);

    void destroy();
}
