/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.config.TestCommonServicesConfig;
import gov.gtas.model.lookup.RuleCat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestCommonServicesConfig.class })
public class RuleCatServiceImpIT {

	private static final Logger logger = LoggerFactory.getLogger(RuleCatServiceImpIT.class);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Autowired
	private RuleCatService ruleCatService;

	@Test
	public void testRuleCatRetrievalTerroism() {
		RuleCat ruleCat = ruleCatService.findRuleCatByCatId(2L);
		assertEquals(ruleCat.getCatId(), new Long(2L));
		assertEquals(ruleCat.getCategory(), "Terrorism");
	}

	@Test
	public void testRuleCatRetrievalGeneral() {
		RuleCat ruleCat = ruleCatService.findRuleCatByCatId(1L);
		assertEquals(ruleCat.getCatId(), new Long(1L));
		assertEquals(ruleCat.getCategory(), "General");
	}

	@Test
	public void testRuleCatDoesntExist() {
		long notAValidRuleCategory = -1999L;
		RuleCat ruleCat = ruleCatService.findRuleCatByCatId(notAValidRuleCategory);
		assertNull(ruleCat);
	}

}
