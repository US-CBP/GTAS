package gov.gtas.parsers.redisson.jms;

import gov.gtas.config.CachingConfig;
import gov.gtas.config.CommonServicesConfig;
import gov.gtas.jms.config.JmsConfiguration;
import gov.gtas.jms.config.MessagingListnerConfiguration;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;

//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CommonServicesConfig.class,
        CachingConfig.class,JmsConfiguration.class,MessagingListnerConfiguration.class })
@ComponentScan("gov.gtas.parsers.redisson")
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JMSRedissonTest {

    @Autowired
    private InboundQMessageSender sender;

    @Autowired
    private InboundQMessageListener receiver;

    //@Test
    public void testJMSPostRedis(){
        Assert.assertTrue(true);
    }

    //@Test
    public void testJMSPreRedis(){
        Assert.assertTrue(true);
    }

}
