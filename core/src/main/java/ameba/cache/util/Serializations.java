package ameba.cache.util;

/**
 * @author icode
 */
public class Serializations {

    private static Serializer serializer;

    private Serializations() {
    }

    public static void destroy() {
        serializer = null;
    }

    public static Serializer getSerializer() {
        if (serializer == null)
            serializer = new KryoSerializer();
        return serializer;
    }

    public static void setSerializer(Serializer s) {
        serializer = s;
    }

    public static byte[] asBytes(final Object object) {
        return getSerializer().asBytes(object);
    }

    public static <O> O asObject(final byte[] bytes) {
        return getSerializer().asObject(bytes);
    }

    @SuppressWarnings("unchecked")
    public static <O> O asObject(final Object bytes) {
        if (bytes instanceof byte[]) {
            return asObject((byte[]) bytes);
        }
        return (O) bytes;
    }

    public static void registerClass(Class clazz) {
        getSerializer().registerClass(clazz);
    }
}
