package gov.gtas.parsers.redisson.jms;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;

import gov.gtas.config.CachingConfig;
import gov.gtas.config.TestCommonServicesConfig;
import gov.gtas.jms.config.JmsConfiguration;
import gov.gtas.jms.config.MessagingListnerConfiguration;

//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestCommonServicesConfig.class,
        CachingConfig.class,JmsConfiguration.class,MessagingListnerConfiguration.class })
@ComponentScan("gov.gtas.parsers.redisson")
@Rollback(true)
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

