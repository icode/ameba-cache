package ameba.cache.engine.memcached;

import ameba.cache.CacheEngine;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @author icode
 */
public class MemcachedEngineTest {

    CacheEngine<String, Object> engine;

    @Before
    public void init() {
        engine = MemcachedEngine.create(Sets.newHashSet(new InetSocketAddress("localhost", 11211)));
    }

    @After
    public void tearDown() {
        engine.shutdown();
    }

    @Test
    public void testReplace() throws InterruptedException {
        Data d = new Data("a", "b", "c");
        engine.syncSet("testReplace", d, 3);
        Assert.assertEquals(d, engine.get("testReplace"));
        Data d1 = new Data("a1", "b1", "c1");

        engine.replace("testReplace", d1, 1);
        Thread.sleep(500);
        Assert.assertEquals(d1, engine.get("testReplace"));
    }

    @Test
    public void testGet() {
        Data d = new Data("a", "b", "c");
        engine.syncSet("testGet", d, 3);
        Assert.assertEquals(d, engine.get("testGet"));

        Data d1 = new Data("a1", "b1", "c1");
        engine.syncSet("testGet2", d1, 3);
        Assert.assertEquals(d1, engine.get("testGet2"));

        Map<String, Object> objs = engine.get("testGet", "testGet2");

        Assert.assertEquals(d, objs.get("testGet"));
        Assert.assertEquals(d1, objs.get("testGet2"));
    }

    @Test
    public void testSafeDecr() {
        engine.syncDelete("testSafeDecr");
        long it = engine.syncDecr("testSafeDecr", 1, 5, 3);

        Assert.assertEquals(5, it);

        it = engine.syncDecr("testSafeDecr", 5, 0, 1);
        Assert.assertEquals(0, it);
    }

    @Test
    public void testClear() {

        Data d = new Data("a", "b", "c");
        engine.syncSet("testClear", d, 3);
        Assert.assertEquals(d, engine.get("testClear"));

        engine.syncSet("testClear", d, 3);
        Assert.assertEquals(d, engine.get("testClear"));

        engine.clear();

        Assert.assertNotEquals(d, engine.get("testClear"));
        Assert.assertNotEquals(d, engine.get("testClear"));
    }

    @Test
    public void testDelete() throws InterruptedException {

        Data d = new Data("a", "b", "c");
        engine.syncSet("testDelete", d, 3);
        Assert.assertEquals(d, engine.get("testDelete"));

        engine.delete("testDelete");

        Thread.sleep(3 * 5000);

        Assert.assertNotEquals(d, engine.get("testDelete"));
    }

    @Test
    public void testSafeIncr() {

        Assert.assertEquals(1L, engine.syncIncr("testSafeIncr", 1, 1, 3));

        Assert.assertEquals(6L, engine.syncIncr("testSafeIncr", 5, 1, 1));
    }

    @Test
    public void testSafeReplace() {
        Data d = new Data("a", "b", "c");
        engine.syncSet("testSafeReplace", d, 3);
        Assert.assertEquals(d, engine.get("testSafeReplace"));
        Data d1 = new Data("a1", "b1", "c1");

        engine.syncReplace("testSafeReplace", d1, 1);
        Assert.assertEquals(d1, engine.get("testSafeReplace"));
    }

    @Test
    public void testSafeDelete() {
        Data d = new Data("a", "b", "c");
        engine.syncSet("testSafeDelete", d, 3);
        Assert.assertEquals(d, engine.get("testSafeDelete"));

        engine.syncDelete("testSafeDelete");

        Assert.assertNotEquals(d, engine.get("testSafeDelete"));
    }

    @Test
    public void testGat() throws InterruptedException {
        Data d = new Data("a", "b", "c");
        engine.syncSet("testGat", d, 3);

        Assert.assertEquals(d, engine.gat("testGat", 1));
        Thread.sleep(5000);

        Assert.assertNotEquals(d, engine.get("testGat"));
    }

    @Test
    public void testSafeSet() throws InterruptedException {
        Data d = new Data("a", "b", "c");
        engine.syncSet("testSafeSet", d, 3);

        Assert.assertEquals(d, engine.get("testSafeSet"));

        Data d1 = new Data("a1", "b1", "c1");
        engine.set("testSafeSet", d1, 3);
        Assert.assertEquals(d1, engine.get("testSafeSet"));

        Thread.sleep(3 * 5000);
        Assert.assertNotEquals(d, engine.get("testSafeSet"));
    }

    @Test
    public void testSet() throws InterruptedException {
        Data d = new Data("a", "b", "c");
        engine.set("testSet", d, 3);

        Thread.sleep(500);

        Assert.assertEquals(d, engine.get("testSet"));


        Data d1 = new Data("a1", "b1", "c1");
        engine.set("testSet", d1, 3);
        Thread.sleep(500);
        Assert.assertEquals(d1, engine.get("testSet"));

        Thread.sleep(3 * 5000);
        Assert.assertNotEquals(d, engine.get("testSet"));
    }

    @Test
    public void testTouch() throws InterruptedException {
        Data d = new Data("a", "b", "c");
        engine.syncAdd("testTouch", d, 5);

        Assert.assertEquals(d, engine.get("testTouch"));

        engine.touch("testTouch", 5);
        Assert.assertEquals(d, engine.get("testTouch"));

        Thread.sleep(3 * 5000);
        Assert.assertNotEquals(d, engine.get("testTouch"));
    }


    @Test
    public void testAdd() throws InterruptedException {
        engine.clear();

        Data d = new Data("a", "b", "c");
        engine.syncAdd("testAdd", d, 1);

        Thread.sleep(500);

        Assert.assertEquals(d, engine.get("testAdd"));

        Data d1 = new Data("a1", "b1", "c1");
        engine.add("testAdd", d1, 3);
        Thread.sleep(500);
        Assert.assertNotEquals(d1, engine.get("testAdd"));

        Thread.sleep(3 * 5000);
        Assert.assertNotEquals(d, engine.get("testAdd"));
    }

    @Test
    public void testSafeAdd() throws InterruptedException {
        Data d = new Data("a", "b", "c");
        engine.syncAdd("testSafeAdd", d, 3);

        Assert.assertEquals(d, engine.get("testSafeAdd"));

        Data d1 = new Data("a1", "b1", "c1");
        engine.syncAdd("testSafeAdd", d1, 3);
        Assert.assertNotEquals(d1, engine.get("testSafeAdd"));

        Thread.sleep(3 * 5000);
        Assert.assertNotEquals(d, engine.get("testSafeAdd"));
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
