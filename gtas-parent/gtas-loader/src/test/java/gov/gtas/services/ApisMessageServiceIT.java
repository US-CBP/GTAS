/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.config.CachingConfig;
import gov.gtas.config.CommonServicesConfig;
import gov.gtas.parsers.exception.ParseException;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CommonServicesConfig.class,
        CachingConfig.class })
@Transactional
public class ApisMessageServiceIT extends
        AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    private Loader svc;

    private File message;

    @Before
    public void setUp() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        this.message = new File(classLoader.getResource(
                "apis-messages/airline2.edi").getFile());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test()
    public void testRunService() throws ParseException {
        svc.processMessage(this.message);
    }
}
