package gov.gtas.services;

import gov.gtas.config.CommonServicesConfig;
import gov.gtas.repository.CaseDispositionRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CommonServicesConfig.class })
public class CaseDispositionServiceImplTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Autowired
    private CaseDispositionService caseDispService;


    @Test
    public void testCreateCase() throws Exception {
        //assertTrue((caseDispService.create((new Long(18)).longValue(), (new Long(18)).longValue(), (new Long(18)).longValue())) instanceof Object);
    }

}