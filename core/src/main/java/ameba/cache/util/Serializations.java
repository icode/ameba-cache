package ameba.cache.util;

import ameba.exception.AmebaException;
import ameba.util.IOUtils;
import org.nustaq.serialization.FSTConfiguration;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author icode
 */
public class Serializations {

    private static final FSTConfiguration conf = FSTConfiguration.createFastBinaryConfiguration();

    private Serializations() {
    }


    public static byte[] toBytes(final Object object) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream(1024);
        FSTObjectOutput output = conf.getObjectOutput(stream);
        try {
            output.writeObject(object);
            output.flush();
            return stream.toByteArray();
        } catch (IOException e) {
            throw new AmebaException(e);
        } finally {
            IOUtils.closeQuietly(stream);
            output.resetForReUse();
        }
    }

    @SuppressWarnings("unchecked")
    public static <O> O toObject(final byte[] bytes) {
        FSTObjectInput in = conf.getObjectInput(bytes);
        try {
            return (O) in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new AmebaException(e);
        } finally {
            try {
                in.reset();
            } catch (IOException e) {
                // no op
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <O> O toObject(final Object bytes) {
        if (bytes instanceof byte[]) {
            return toObject((byte[]) bytes);
        }
        return (O) bytes;
    }

    public static void registerClass(Class... classes) {
        conf.registerClass(classes);
    }

}
