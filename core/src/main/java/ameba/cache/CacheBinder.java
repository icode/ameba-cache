package ameba.cache;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.hk2.api.*;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.internal.util.collection.Ref;

import javax.inject.Singleton;
import javax.ws.rs.core.GenericType;
import java.lang.reflect.*;

import static ameba.cache.CacheFilter.parseExpiration;

/**
 * @author icode
 */
public class CacheBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(CachedInjectionResolver.class)
                .to(new GenericType<InjectionResolver<Cached>>() {
                }).in(Singleton.class);
    }

    protected static class CachedInjectionResolver implements InjectionResolver<Cached> {

        protected Object _get(String key, int expiration, Cached cached) {
            if (cached.touch())
                return Cache.gat(key, expiration);
            else
                return Cache.get(key);
        }

        @Override
        public Object resolve(Injectee injectee, ServiceHandle<?> root) {
            AnnotatedElement element = injectee.getParent();
            Cached cached = element.getAnnotation(Cached.class);
            String key = cached.key();
            if (StringUtils.isBlank(key)) {
                if (element instanceof Field) {
                    Field field = (Field) element;
                    key = field.getName();
                } else if (element instanceof Constructor) {
                    Constructor constructor = (Constructor) element;
                    key = constructor.getParameters()[injectee.getPosition()].getName();
                } else if (element instanceof Method) {
                    Method method = (Method) element;
                    key = method.getParameters()[injectee.getPosition()].getName();
                } else {
                    throw new MultiException(new UnsatisfiedDependencyException(injectee));
                }
            }
            int expiration = parseExpiration(cached);
            Type type = injectee.getRequiredType();
            if (ReflectionHelper.isSubClassOf(type, Ref.class)) {
                String finalKey = key;
                return new Ref<Object>() {
                    @Override
                    public Object get() {
                        return _get(finalKey, expiration, cached);
                    }

                    @Override
                    public void set(Object value) {
                        if (cached.sync()) {
                            Cache.syncSet(finalKey, value, expiration);
                        } else {
                            Cache.set(finalKey, value, expiration);
                        }
                    }
                };
            } else if (ReflectionHelper.isSubClassOf(type, CacheValue.class)) {
                return new CachedValue(key, cached);
            }

            return _get(key, expiration, cached);
        }

        @Override
        public boolean isConstructorParameterIndicator() {
            return true;
        }

        @Override
        public boolean isMethodParameterIndicator() {
            return true;
        }
    }

    protected static class CachedValue implements CacheValue {

        private String key;
        private int expiration;

        public CachedValue(String key, Cached cached) {
            this.key = key;
            this.expiration = parseExpiration(cached);
        }

        @Override
        public void add(Object value) {
            Cache.add(key, value, expiration);
        }

        @Override
        public void add(Object value, int expiration) {
            Cache.add(key, value, expiration);
        }

        @Override
        public boolean syncAdd(Object value) {
            return Cache.syncAdd(key, value, expiration);
        }

        @Override
        public boolean syncAdd(Object value, int expiration) {
            return Cache.syncAdd(key, value, expiration);
        }

        @Override
        public void set(Object value) {
            Cache.set(key, value, expiration);
        }

        @Override
        public void set(Object value, int expiration) {
            Cache.set(key, value, expiration);
        }

        @Override
        public boolean syncSet(Object value) {
            return Cache.syncSet(key, value, expiration);
        }

        @Override
        public boolean syncSet(Object value, int expiration) {
            return Cache.syncSet(key, value, expiration);
        }

        @Override
        public void replace(Object value) {
            Cache.replace(key, value, expiration);
        }

        @Override
        public void replace(Object value, int expiration) {
            Cache.replace(key, value, expiration);
        }

        @Override
        public boolean syncReplace(Object value) {
            return Cache.syncReplace(key, value, expiration);
        }

        @Override
        public boolean syncReplace(Object value, int expiration) {
            return Cache.syncReplace(key, value, expiration);
        }

        @Override
        public Object get() {
            return Cache.get(key);
        }

        @Override
        public Object gat() {
            return Cache.gat(key, expiration);
        }

        @Override
        public Object gat(int expiration) {
            return Cache.gat(key, expiration);
        }

        @Override
        public boolean touch() {
            return Cache.touch(key, expiration);
        }

        @Override
        public boolean touch(int expiration) {
            return Cache.touch(key, expiration);
        }

        @Override
        public void incr() {
            Cache.incr(key, expiration);
        }

        @Override
        public void incr(int by, long initial) {
            Cache.incr(key, by, initial, expiration);
        }

        @Override
        public void incr(int by, long initial, int expirationInSecs) {
            Cache.incr(key, by, initial, expirationInSecs);
        }

        @Override
        public void decr() {
            Cache.decr(key, expiration);
        }

        @Override
        public void decr(int by, long initial) {
            Cache.decr(key, by, initial, expiration);
        }

        @Override
        public void decr(int by, long initial, int expirationInSecs) {
            Cache.decr(key, by, initial, expirationInSecs);
        }

        @Override
        public long syncIncr() {
            return Cache.syncIncr(key, expiration);
        }

        @Override
        public long syncIncr(int by, long initial) {
            return Cache.syncIncr(key, by, initial, expiration);
        }

        @Override
        public long syncIncr(int by, long initial, int expirationInSecs) {
            return Cache.syncIncr(key, by, initial, expirationInSecs);
        }

        @Override
        public long syncDecr() {
            return Cache.syncDecr(key, expiration);
        }

        @Override
        public long syncDecr(int by, long initial) {
            return Cache.syncDecr(key, by, initial, expiration);
        }

        @Override
        public long syncDecr(int by, long initial, int expirationInSecs) {
            return Cache.syncDecr(key, by, initial, expirationInSecs);
        }

        @Override
        public void delete() {
            Cache.delete(key);
        }

        @Override
        public boolean syncDelete() {
            return Cache.syncDelete(key);
        }
    }
}
