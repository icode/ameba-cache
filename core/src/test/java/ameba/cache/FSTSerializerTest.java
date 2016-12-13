package ameba.cache;

import ameba.cache.model.FSTModel;
import ameba.cache.util.FSTSerializer;
import ameba.cache.util.Serializer;
import com.google.common.collect.Lists;
import io.ebean.Ebean;
import org.avaje.agentloader.AgentLoader;
import org.junit.Assert;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author icode
 */
public class FSTSerializerTest {
    private static Logger logger = LoggerFactory.getLogger(FSTSerializerTest.class);

    @Before
    public void init() {
        logger.debug("... preStart");
        if (!AgentLoader.loadAgentFromClasspath("ebean-agent", "debug=1;packages=ameba.cache.*")) {
            logger.info("ebean-agent not found in classpath - not dynamically loaded");
        }
    }

    //    @Test
    public void testFST() {
        testSerializer(new FSTSerializer());
    }

//    @Test
//    public void testKryo() {
//        testSerializer(new KryoSerializer());
//    }

    private void testSerializer(Serializer serializer) {
        FSTModel model = new FSTModel();
        model.setF1("test fst");
        model.setF2(2);
        model.setF3(Lists.newArrayList());

        byte[] bytes = serializer.asBytes(model);
        serializer.asObject(bytes);

        Ebean.save(model);
        model = Ebean.find(FSTModel.class, model.getId());
        bytes = serializer.asBytes(model);
        FSTModel objModel = serializer.asObject(bytes);
        Assert.assertEquals(objModel, model);
    }

}
