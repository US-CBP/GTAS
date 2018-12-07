/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import static org.junit.Assert.assertEquals;
import gov.gtas.config.CachingConfig;
import gov.gtas.config.CommonServicesConfig;
import gov.gtas.services.Filter.FilterData;
import gov.gtas.services.Filter.FilterService;
import gov.gtas.services.Filter.FilterServiceUtil;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.annotation.Rollback;

/**
 * The Class FilterServiceIT.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CommonServicesConfig.class,
		CachingConfig.class })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Rollback(true)
public class FilterServiceIT {

	private static final Logger logger = LoggerFactory.getLogger(FilterServiceIT.class);

	private final String USER_ID = "test";

	@Autowired
	FilterService filterService;

	@Autowired
	FilterServiceUtil filterServiceUtil;

	@Test
	public void testCreateUserFilter() {

		// Arrange
		Set<String> originAirports = new HashSet<String>();

		originAirports.add("GKA");
		originAirports.add("MAG");
		originAirports.add("HGU");

		Set<String> destinationAirports = new HashSet<String>();
		destinationAirports.add("LAE");
		destinationAirports.add("POM");
		destinationAirports.add("WWK");
		int etaStart = -1;
		int etaEnd = 1;

		FilterData acutalFilter = null;

		FilterData expectedFilter = new FilterData(USER_ID, "I",
				originAirports, destinationAirports, etaStart, etaEnd);

		// Act
		try {
			acutalFilter = filterService.create(expectedFilter);
		} catch (Exception e) {
			logger.error("error creating acutal filter", e);
		}

		// Assert
		assertEquals(expectedFilter, acutalFilter);
	}

	@Test
	public void testUpdateFilter() {
		// Arrange
		FilterData existingFilter = filterService.findById(USER_ID);

		Set<String> originAirports = new HashSet<String>();

		originAirports.add("UAK");
		originAirports.add("GOH");
		originAirports.add("SFJ");

		Set<String> destinationAirports = new HashSet<String>();
		destinationAirports.add("THU");
		destinationAirports.add("AEY");
		destinationAirports.add("EGS");

		FilterData expectedFilter = new FilterData(existingFilter.getUserId(),
				"O", originAirports, destinationAirports,
				existingFilter.getEtaStart() - 2,
				existingFilter.getEtaEnd() + 2);

		// Act
		FilterData actualFilter = filterService.update(expectedFilter);

		logger.info(actualFilter == null ? "null" : actualFilter.toString());
		// Assert
		assertEquals(expectedFilter, actualFilter);
		filterService.delete(USER_ID);
	}
}
