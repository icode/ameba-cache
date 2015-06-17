package ameba.cache.util;

import ameba.util.IOUtils;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.bean.BeanCollection;
import com.esotericsoftware.kryo.ClassResolver;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.ReferenceResolver;
import com.esotericsoftware.kryo.StreamFactory;
import com.esotericsoftware.kryo.factories.SerializerFactory;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoCallback;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.esotericsoftware.kryo.util.DefaultClassResolver;
import com.esotericsoftware.kryo.util.FastestStreamFactory;
import com.esotericsoftware.kryo.util.MapReferenceResolver;
import com.esotericsoftware.minlog.Log;
import de.javakaffee.kryoserializers.*;
import de.javakaffee.kryoserializers.guava.ImmutableListSerializer;
import de.javakaffee.kryoserializers.jodatime.JodaDateTimeSerializer;
import de.javakaffee.kryoserializers.jodatime.JodaLocalDateSerializer;
import de.javakaffee.kryoserializers.jodatime.JodaLocalDateTimeSerializer;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationHandler;
import java.util.Arrays;
import java.util.GregorianCalendar;

/**
 * @author icode
 */
public class KryoSerializer implements Serializer {
    private static final Logger logger = LoggerFactory.getLogger(Serializations.class);

    static {
        Log.setLogger(new Slf4jLogger());
    }

    private final KryoFactory factory = new KryoFactory() {
        public Kryo create() {
            return new KryoExtends();
        }
    };
    private final KryoPool pool = new KryoPool.Builder(factory).softReferences().build();

    public void registerClass(Class clazz) {
    }

    public byte[] asBytes(final Object object) {
        return pool.run(new KryoCallback<byte[]>() {
            @Override
            public byte[] execute(Kryo kryo) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Output output = new Output(out, 1024);
                kryo.writeClassAndObject(output, object);
                output.flush();
                try {
                    return out.toByteArray();
                } finally {
                    IOUtils.closeQuietly(output);
                    IOUtils.closeQuietly(out);
                    kryo.reset();
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    public <O> O asObject(final byte[] bytes) {
        return pool.run(new KryoCallback<O>() {
            @Override
            public O execute(Kryo kryo) {
                Input input = new Input(bytes, 0, 1024);
                try {
                    return (O) kryo.readClassAndObject(input);
                } finally {
                    IOUtils.closeQuietly(input);
                    kryo.reset();
                }
            }
        });
    }

    private static class Slf4jLogger extends Log.Logger {
        @Override
        public void log(int level, String category, String message, Throwable ex) {
            switch (level) {
                case Log.LEVEL_ERROR:
                    logger.error(message, ex);
                    break;
                case Log.LEVEL_WARN:
                    logger.warn(message, ex);
                    break;
                case Log.LEVEL_INFO:
                    logger.info(message, ex);
                    break;
                case Log.LEVEL_DEBUG:
                    logger.debug(message, ex);
                    break;
                case Log.LEVEL_TRACE:
                    logger.trace(message, ex);
                    break;
            }
        }
    }

    public static class KryoExtends extends Kryo {
        {
            setAsmEnabled(true);
            setAutoReset(false);

            setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
            register(Arrays.asList("").getClass(), new ArraysAsListSerializer());
            register(GregorianCalendar.class, new GregorianCalendarSerializer());
            register(InvocationHandler.class, new JdkProxySerializer());
            UnmodifiableCollectionsSerializer.registerSerializers(this);
            SynchronizedCollectionsSerializer.registerSerializers(this);

            // custom serializers for non-jdk libs

            // register CGLibProxySerializer, works in combination with the appropriate action in handleUnregisteredClass (see below)
//            register(CGLibProxySerializer.CGLibProxyMarker.class, new CGLibProxySerializer());
            // joda DateTime, LocalDate and LocalDateTime
            register(DateTime.class, new JodaDateTimeSerializer());
            register(LocalDate.class, new JodaLocalDateSerializer());
            register(LocalDateTime.class, new JodaLocalDateTimeSerializer());
            // guava ImmutableList
            ImmutableListSerializer.registerSerializers(this);
            SerializerFactory fieldSerializerFactory = new SerializerFactory() {
                @Override
                public com.esotericsoftware.kryo.Serializer makeSerializer(Kryo kryo, Class<?> type) {
                    return new FieldSerializer<>(kryo, BeanCollection.class.isAssignableFrom(type)
                            ? BeanCollection.class : SqlRow.class);
                }
            };

            addDefaultSerializer(BeanCollection.class, fieldSerializerFactory);
            addDefaultSerializer(SqlRow.class, fieldSerializerFactory);
            setClassLoader(Thread.currentThread().getContextClassLoader());
        }

        public KryoExtends() {
            super(new DefaultClassResolver(), new MapReferenceResolver(), new FastestStreamFactory());
        }

        public KryoExtends(ReferenceResolver referenceResolver) {
            super(referenceResolver);
        }

        public KryoExtends(ClassResolver classResolver, ReferenceResolver referenceResolver) {
            super(classResolver, referenceResolver);
        }

        public KryoExtends(ClassResolver classResolver, ReferenceResolver referenceResolver, StreamFactory streamFactory) {
            super(classResolver, referenceResolver, streamFactory);
        }
    }
}
