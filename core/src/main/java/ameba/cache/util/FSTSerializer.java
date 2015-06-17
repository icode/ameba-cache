package ameba.cache.util;

import org.nustaq.serialization.FSTConfiguration;

/**
 * @author icode
 */
public class FSTSerializer implements Serializer {
    private FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    {
        conf.setClassLoader(Thread.currentThread().getContextClassLoader());
    }

    public void registerClass(Class clazz) {
    }

    public byte[] asBytes(final Object object) {
        return conf.asByteArray(object);
    }

    @SuppressWarnings("unchecked")
    public <O> O asObject(final byte[] bytes) {
        return (O) conf.asObject(bytes);
    }
}
