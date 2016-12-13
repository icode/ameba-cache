package ameba.cache.util;

import ameba.util.IOUtils;
import com.esotericsoftware.kryo.ClassResolver;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.ReferenceResolver;
import com.esotericsoftware.kryo.StreamFactory;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.esotericsoftware.kryo.util.DefaultClassResolver;
import com.esotericsoftware.kryo.util.FastestStreamFactory;
import com.esotericsoftware.kryo.util.MapReferenceResolver;
import com.esotericsoftware.minlog.Log;
import de.javakaffee.kryoserializers.*;
import de.javakaffee.kryoserializers.guava.ImmutableListSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableMapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableMultimapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableSetSerializer;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationHandler;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author icode
 */
public class KryoSerializer implements Serializer {
    private static final Logger logger = LoggerFactory.getLogger(Serializations.class);

    static {
        Log.setLogger(new Slf4jLogger());
    }

    private final KryoFactory factory = KryoExtends::new;
    private final Queue<Kryo> queue = new ConcurrentLinkedQueue<>();
    private final KryoPool pool = new KryoPool.Builder(factory).queue(queue).softReferences().build();

    public void registerClass(Class clazz) {
    }

    @Override
    public void destroy() {
        queue.clear();
    }

    public byte[] asBytes(final Object object) {
        return pool.run(kryo -> {
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
        });
    }

    @SuppressWarnings("unchecked")
    public <O> O asObject(final byte[] bytes) {
        return pool.run(kryo -> {
            Input input = new Input(bytes, 0, 1024);
            try {
                return (O) kryo.readClassAndObject(input);
            } finally {
                IOUtils.closeQuietly(input);
                kryo.reset();
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
//            register(DateTime.class, new JodaDateTimeSerializer());
//            register(LocalDate.class, new JodaLocalDateSerializer());
//            register(LocalDateTime.class, new JodaLocalDateTimeSerializer());
            // guava ImmutableList
            ImmutableListSerializer.registerSerializers(this);
            ImmutableListSerializer.registerSerializers(this);
            ImmutableSetSerializer.registerSerializers(this);
            ImmutableMapSerializer.registerSerializers(this);
            ImmutableMultimapSerializer.registerSerializers(this);
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
