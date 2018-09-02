package gov.gtas.repository;

import gov.gtas.config.CachingConfig;
import gov.gtas.config.CommonServicesConfig;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CommonServicesConfig.class,CachingConfig.class })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Ignore //Ignored because there are no unit test in class.
public class PassengerRepositoryIT {

    @Autowired
    private PassengerRepository passengerDao;
    private static final Logger logger = LoggerFactory
            .getLogger(PassengerRepositoryIT.class);

    //@Test
    //@Transactional
    public void testRetrieveNotNullIdTagPax() {

    }

}
