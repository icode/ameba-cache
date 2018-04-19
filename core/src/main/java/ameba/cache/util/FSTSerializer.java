package ameba.cache.util;

import org.apache.commons.lang3.StringUtils;
import org.nustaq.serialization.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author icode
 */
public class FSTSerializer implements Serializer {
    private FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    public FSTSerializer() {
        conf.setForceSerializable(true);
        conf.registerSerializer(Paths.get(".").getClass(), new FSTBasicObjectSerializer() {
            @Override
            public boolean alwaysCopy() {
                return true;
            }

            @Override
            public void writeObject(FSTObjectOutput out, Object toWrite, FSTClazzInfo clzInfo,
                                    FSTClazzInfo.FSTFieldInfo referencedBy, int streamPosition) throws IOException {
                out.writeStringUTF(((Path) toWrite).toUri().toString());
            }

            @Override
            public Object instantiate(Class objectClass, FSTObjectInput fstObjectInput, FSTClazzInfo serializationInfo,
                                      FSTClazzInfo.FSTFieldInfo referencee, int streamPositioin) throws Exception {
                String path = fstObjectInput.readStringUTF();
                if (StringUtils.isBlank(path)) return null;
                return Paths.get(path);
            }
        }, true);
        conf.setClassLoader(Thread.currentThread().getContextClassLoader());
    }

    public void registerClass(Class clazz) {
        conf.registerClass(clazz);
    }

    @Override
    public void destroy() {
        conf.clearCaches();
    }

    public byte[] asBytes(final Object object) {
        return conf.asByteArray(object);
    }

    @SuppressWarnings("unchecked")
    public <O> O asObject(final byte[] bytes) {
        return (O) conf.asObject(bytes);
    }
}
