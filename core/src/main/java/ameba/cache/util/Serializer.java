package ameba.cache.util;

/**
 * @author icode
 */
public interface Serializer {
    public byte[] asBytes(final Object object);

    public <O> O asObject(final byte[] bytes);

    public void registerClass(Class clazz);
}
