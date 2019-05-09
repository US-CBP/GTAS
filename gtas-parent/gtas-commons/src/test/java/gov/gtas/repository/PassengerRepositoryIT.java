package gov.gtas.repository;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.gtas.config.CachingConfig;
import gov.gtas.config.TestCommonServicesConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestCommonServicesConfig.class,CachingConfig.class })
@Rollback(true)
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
