package ameba.cache.util;

import ameba.exception.AmebaException;
import ameba.util.IOUtils;
import org.nustaq.serialization.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author icode
 */
public class Serializations {

    private static FSTConfiguration conf = null;

    private Serializations() {
    }

    public static void init() {
        if (conf == null)
            conf = FSTConf.createFastBinaryConfiguration();
    }

    public static void reinit() {
        conf = FSTConf.createFastBinaryConfiguration();
    }

    public static byte[] asBytes(final Object object) {
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
    public static <O> O asObject(final byte[] bytes) {
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
    public static <O> O asObject(final Object bytes) {
        if (bytes instanceof byte[]) {
            return asObject((byte[]) bytes);
        }
        return (O) bytes;
    }

    public static void registerClass(Class... classes) {
        conf.registerClass(classes);
    }

    public static class FSTConf extends FSTConfiguration {

        private FSTConfiguration configuration;

        public FSTConf(FSTConfiguration configuration) {
            this.configuration = configuration;
        }

        public static FSTConfiguration getDefaultConfiguration() {
            return new FSTConf(FSTConfiguration.getDefaultConfiguration());
        }

        public static FSTConfiguration createMinBinConfiguration() {
            return new FSTConf(FSTConfiguration.createMinBinConfiguration());
        }

        public static FSTConfiguration createJsonConfiguration() {
            return new FSTConf(FSTConfiguration.createJsonConfiguration());
        }

        public static FSTConfiguration createJsonConfiguration(boolean prettyPrint, boolean shareReferences) {
            return new FSTConf(FSTConfiguration.createJsonConfiguration(prettyPrint, shareReferences));
        }

        public static FSTConfiguration createAndroidDefaultConfiguration() {
            return new FSTConf(FSTConfiguration.createAndroidDefaultConfiguration());
        }

        public static FSTConfiguration createDefaultConfiguration() {
            return new FSTConf(FSTConfiguration.createDefaultConfiguration());
        }

        public static FSTConfiguration initDefaultFstConfigurationInternal(FSTConfiguration conf) {
            return new FSTConf(FSTConfiguration.initDefaultFstConfigurationInternal(conf));
        }

        public static FSTConfiguration createFastBinaryConfiguration() {
            return new FSTConf(FSTConfiguration.createFastBinaryConfiguration());
        }

        public static FSTConfiguration createStructConfiguration() {
            return FSTConfiguration.createStructConfiguration();
        }

        @Override
        public ClassLoader getClassLoader() {
            return Thread.currentThread().getContextClassLoader();
        }

        @Override
        public void setClassLoader(ClassLoader classLoader) {
            configuration.setClassLoader(classLoader);
        }

        @Override
        public FSTClazzInfo getClassInfo(Class type) {
            return configuration.getClassInfo(type);
        }

        @Override
        public FSTObjectInput getObjectInput(InputStream in) {
            return configuration.getObjectInput(in);
        }

        @Override
        public FSTObjectInput getObjectInput() {
            return configuration.getObjectInput();
        }

        @Override
        public FSTObjectInput getObjectInput(byte[] arr) {
            return configuration.getObjectInput(arr);
        }

        @Override
        public FSTObjectInput getObjectInput(byte[] arr, int len) {
            return configuration.getObjectInput(arr, len);
        }

        @Override
        public FSTObjectOutput getObjectOutput(OutputStream out) {
            return configuration.getObjectOutput(out);
        }

        @Override
        public FSTObjectOutput getObjectOutput() {
            return configuration.getObjectOutput();
        }

        @Override
        public FSTObjectOutput getObjectOutput(byte[] outByte) {
            return configuration.getObjectOutput(outByte);
        }

        @Override
        public boolean isStructMode() {
            return configuration.isStructMode();
        }

        @Override
        public void setStructMode(boolean ignoreSerialInterfaces) {
            configuration.setStructMode(ignoreSerialInterfaces);
        }

        @Override
        public FSTClazzInfo getClazzInfo(Class rowClass) {
            return configuration.getClazzInfo(rowClass);
        }

        @Override
        public boolean isCrossPlatform() {
            return configuration.isCrossPlatform();
        }

        @Override
        public void setCrossPlatform(boolean crossPlatform) {
            configuration.setCrossPlatform(crossPlatform);
        }

        @Override
        public <T> T deepCopy(T metadata) {
            return configuration.deepCopy(metadata);
        }

        @Override
        public FSTEncoder createStreamEncoder() {
            return configuration.createStreamEncoder();
        }

        @Override
        public FSTDecoder createStreamDecoder() {
            return configuration.createStreamDecoder();
        }

        @Override
        public void registerCrossPlatformClassBinaryCache(String fulLQName, byte[] binary) {
            configuration.registerCrossPlatformClassBinaryCache(fulLQName, binary);
        }

        @Override
        public byte[] getCrossPlatformBinaryCache(String symbolicName) {
            return configuration.getCrossPlatformBinaryCache(symbolicName);
        }

        @Override
        public FSTConfiguration registerCrossPlatformClassMapping(String[][] keysAndVals) {
            return configuration.registerCrossPlatformClassMapping(keysAndVals);
        }

        @Override
        public FSTConfiguration registerCrossPlatformClassMapping(String shortName, String fqName) {
            return configuration.registerCrossPlatformClassMapping(shortName, fqName);
        }

        @Override
        public FSTConfiguration cpMap(String shortName, Class clz) {
            return configuration.cpMap(shortName, clz);
        }

        @Override
        public FSTConfiguration registerCrossPlatformClassMappingUseSimpleName(Class... classes) {
            return configuration.registerCrossPlatformClassMappingUseSimpleName(classes);
        }

        @Override
        public FSTConfiguration registerCrossPlatformClassMappingUseSimpleName(List<Class> classes) {
            return configuration.registerCrossPlatformClassMappingUseSimpleName(classes);
        }

        @Override
        public String getCPNameForClass(Class cl) {
            return configuration.getCPNameForClass(cl);
        }

        @Override
        public String getClassForCPName(String name) {
            return configuration.getClassForCPName(name);
        }

        @Override
        public Object asObject(byte[] b) {
            return configuration.asObject(b);
        }

        @Override
        public byte[] asByteArray(Object object) {
            return configuration.asByteArray(object);
        }

        @Override
        public byte[] asSharedByteArray(Object object, int[] length) {
            return configuration.asSharedByteArray(object, length);
        }

        @Override
        public String asJsonString(Object o) {
            return configuration.asJsonString(o);
        }

        @Override
        public void encodeToStream(OutputStream out, Object toSerialize) throws IOException {
            configuration.encodeToStream(out, toSerialize);
        }

        @Override
        public Object decodeFromStream(InputStream in) throws Exception {
            return configuration.decodeFromStream(in);
        }

        @Override
        public boolean isForceClzInit() {
            return configuration.isForceClzInit();
        }

        @Override
        public FSTConfiguration setForceClzInit(boolean forceClzInit) {
            return configuration.setForceClzInit(forceClzInit);
        }

        @Override
        public FSTClassInstantiator getInstantiator(Class clazz) {
            return configuration.getInstantiator(clazz);
        }

        @Override
        public void setInstantiator(FSTClassInstantiator instantiator) {
            configuration.setInstantiator(instantiator);
        }

        @Override
        public void registerSerializer(Class clazz, FSTObjectSerializer ser, boolean alsoForAllSubclasses) {
            configuration.registerSerializer(clazz, ser, alsoForAllSubclasses);
        }

        @Override
        public <T> T getCoderSpecific() {
            return configuration.getCoderSpecific();
        }

        @Override
        public void setCoderSpecific(Object coderSpecific) {
            configuration.setCoderSpecific(coderSpecific);
        }

        @Override
        public StreamCoderFactory getStreamCoderFactory() {
            return configuration.getStreamCoderFactory();
        }

        @Override
        public void setStreamCoderFactory(StreamCoderFactory streamCoderFactory) {
            configuration.setStreamCoderFactory(streamCoderFactory);
        }

        @Override
        public void returnObject(Object cached) {
            configuration.returnObject(cached);
        }

        @Override
        public boolean isPreferSpeed() {
            return configuration.isPreferSpeed();
        }

        @Override
        public void setPreferSpeed(boolean preferSpeed) {
            configuration.setPreferSpeed(preferSpeed);
        }

        @Override
        public int calcObjectSizeBytesNotAUtility(Object obj) throws IOException {
            return configuration.calcObjectSizeBytesNotAUtility(obj);
        }

        @Override
        public void setSerializerRegistryDelegate(FSTSerializerRegistryDelegate del) {
            configuration.setSerializerRegistryDelegate(del);
        }

        @Override
        public Object getCachedObject(Class cl) {
            return configuration.getCachedObject(cl);
        }

        @Override
        public boolean isForceSerializable() {
            return configuration.isForceSerializable();
        }

        @Override
        public FSTConfiguration setForceSerializable(boolean forceSerializable) {
            return configuration.setForceSerializable(forceSerializable);
        }

        @Override
        public void clearCaches() {
            configuration.clearCaches();
        }

        @Override
        public boolean isShareReferences() {
            return configuration.isShareReferences();
        }

        @Override
        public void setShareReferences(boolean shareReferences) {
            configuration.setShareReferences(shareReferences);
        }

        @Override
        public void registerClass(Class... c) {
            configuration.registerClass(c);
        }

        @Override
        public FSTClazzNameRegistry getClassRegistry() {
            return configuration.getClassRegistry();
        }

        @Override
        public FSTClazzInfoRegistry getCLInfoRegistry() {
            return configuration.getCLInfoRegistry();
        }
    }

}
