/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.search;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.gtas.config.TestCommonServicesConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestCommonServicesConfig.class })
public class SearchServiceIT {
	@Autowired
	SearchService searchService;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSearch() {
		searchService.findPassengers("garywilliam", 1, 10, "firstName", "desc");
	}
}
