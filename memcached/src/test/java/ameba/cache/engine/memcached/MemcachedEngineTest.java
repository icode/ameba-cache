package ameba.cache.engine.memcached;

import ameba.cache.CacheEngine;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author icode
 */
public class MemcachedEngineTest {

    CacheEngine<String, Object> engine;

    @Before
    public void init() {
        engine = MemcachedEngine.create(Sets.<SocketAddress>newHashSet(new InetSocketAddress("127.0.0.1", 11211)));
    }

    @Test
    public void testSafeSet() throws InterruptedException {
        Data d = new Data("a", "b", "c");
        engine.safeSet("test1", d, 3);

        Assert.assertEquals(d, engine.get("test1"));

        Data d1 = new Data("a1", "b1", "c1");
        engine.set("test1", d1, 3);
        Assert.assertEquals(d1, engine.get("test1"));

        Thread.sleep(3 * 1000);
        Assert.assertNotEquals(d, engine.get("test1"));
    }

    @Test
    public void testSet() throws InterruptedException {
        Data d = new Data("a", "b", "c");
        engine.set("test1", d, 3);

        Thread.sleep(100);

        Assert.assertEquals(d, engine.get("test1"));


        Data d1 = new Data("a1", "b1", "c1");
        engine.set("test1", d1, 3);
        Thread.sleep(100);
        Assert.assertEquals(d1, engine.get("test1"));

        Thread.sleep(3 * 1000);
        Assert.assertNotEquals(d, engine.get("test1"));
    }

    @Test
    public void testAdd() throws InterruptedException {
        Data d = new Data("a", "b", "c");
        engine.add("test1", d, 3);

        Thread.sleep(100);

        Assert.assertEquals(d, engine.get("test1"));

        Data d1 = new Data("a1", "b1", "c1");
        engine.add("test1", d1, 3);
        Thread.sleep(100);
        Assert.assertNotEquals(d1, engine.get("test1"));

        Thread.sleep(3 * 1000);
        Assert.assertNotEquals(d, engine.get("test1"));
    }

    @Test
    public void testSafeAdd() throws InterruptedException {
        Data d = new Data("a", "b", "c");
        engine.safeAdd("test1", d, 3);

        Assert.assertEquals(d, engine.get("test1"));

        Data d1 = new Data("a1", "b1", "c1");
        engine.safeAdd("test1", d1, 3);
        Assert.assertNotEquals(d1, engine.get("test1"));

        Thread.sleep(3 * 1000);
        Assert.assertNotEquals(d, engine.get("test1"));
    }

    public static class Data implements Serializable {
        public String a;
        public String b;
        public String c;

        public Data(String a, String b, String c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Data && StringUtils.equals(((Data) obj).a, a)
                    && StringUtils.equals(((Data) obj).c, c)
                    && StringUtils.equals(((Data) obj).c, c);
        }

        @Override
        public String toString() {
            return "a:" + a + ",b:" + b + ",c:" + c;
        }
    }
}
