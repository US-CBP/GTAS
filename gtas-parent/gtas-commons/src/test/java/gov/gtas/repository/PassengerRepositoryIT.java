package gov.gtas.repository;

import gov.gtas.config.CachingConfig;
import gov.gtas.config.CommonServicesConfig;
import gov.gtas.model.Passenger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CommonServicesConfig.class,CachingConfig.class })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
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
