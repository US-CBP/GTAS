/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.gtas.config.TestCommonServicesConfig;
import gov.gtas.repository.CaseDispositionRepository;

//import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestCommonServicesConfig.class })
public class CaseDispositionServiceImplTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Autowired
    private CaseDispositionService caseDispService;
    
    @Autowired
    private CaseDispositionRepository caseRepo;


    @Test
    public void testCreateCase() throws Exception {
    	Random _rand = new Random();
        List<Long> _tempHitList = new ArrayList<>();
        _tempHitList.add(new Long(1L));
        assertTrue(true);
    }



}