package ameba.cache;

import ameba.cache.model.FSTModel;
import ameba.cache.util.FSTSerializer;
import ameba.cache.util.Serializer;
import com.avaje.ebean.Ebean;
import com.google.common.collect.Lists;
import org.avaje.agentloader.AgentLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author icode
 */
public class FSTSerializerTest {
    private static Logger logger = LoggerFactory.getLogger(FSTSerializerTest.class);

    static {
        logger.debug("... preStart");
        if (!AgentLoader.loadAgentFromClasspath("avaje-ebeanorm-agent", "debug=1;packages=ameba.cache.*")) {
            logger.info("avaje-ebeanorm-agent not found in classpath - not dynamically loaded");
        }
    }

    public void testFST() {
        Serializer serializer = new FSTSerializer();
        FSTModel model = new FSTModel();
        model.setF1("test fst");
        model.setF2(2);
        model.setF3(Lists.newArrayList());

        byte[] bytes = serializer.asBytes(model);
        serializer.asObject(bytes);

        Ebean.save(model);
        model = Ebean.find(FSTModel.class).findUnique();
        bytes = serializer.asBytes(model);
        serializer.asObject(bytes);
    }
}
