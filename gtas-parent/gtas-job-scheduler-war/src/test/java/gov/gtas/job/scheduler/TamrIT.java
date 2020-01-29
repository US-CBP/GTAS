/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import gov.gtas.config.CachingConfig;
import gov.gtas.config.TestCommonServicesConfig;
import gov.gtas.job.scheduler.LoaderScheduler;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.services.LoaderStatistics;

/**
 * End-to-end integration tests with Tamr.
 * @author Cassidy Laidlaw
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestCommonServicesConfig.class, CachingConfig.class })
@Rollback(true)
public class TamrIT extends AbstractTransactionalJUnit4SpringContextTests {
	private File flightMessage1;
	private File flightMessage2;
	
	@Autowired
	private LoaderScheduler loaderScheduler;

	@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// TODO: edit file paths
		this.flightMessage1 = new File(
		        classLoader.getResource("tamr.application.properties").getFile());
        this.flightMessage2 = new File(
                classLoader.getResource("tamr.application.properties").getFile());
	}

	private void createWatchlistItems() {
	    // TODO: implement
	}
	
	private void sendWatchlistItemsToTamr() {
        // TODO: implement
	}
	
	private void assertDerogListReceived() {   
        // TODO: implement
	}
	
	private void loadFlight(File messageFile, String[] primeFlightKey)
	        throws Exception {
	    LoaderStatistics stats = new LoaderStatistics();
	    loaderScheduler.processSingleFile(messageFile, stats, primeFlightKey);
	}
	
	private void assertPassengersReceived(String passengerJson) {
        // TODO: implement
	}
	
	private void respondWithTravelerHistories(String historiesJson) {
        // TODO: implement
	}
	
	private void respondWithDerogHits(String derogHitsJson) {
        // TODO: implement
	}
	
	@Test()
	@Transactional
	public void testTamrIntegration() throws ParseException, Exception {
	    this.createWatchlistItems();
	    this.sendWatchlistItemsToTamr();
	    this.assertDerogListReceived();

	    // Flight 1
	    this.loadFlight(flightMessage1, new String[] { "flight 1 key TODO" });
	    this.assertPassengersReceived("flight 1 travelers TODO");
	    this.respondWithTravelerHistories("flight 1 histories TODO");
	    // TODO: assert tamrIds set for passengers.
	    this.respondWithDerogHits("flight 1 derog hits TODO");
	    // TODO: check HitDetail is created.
	    
	    // Flight 2
        this.loadFlight(flightMessage2, new String[] { "flight 2 key TODO" });
        this.assertPassengersReceived("flight 2 travelers TODO");
        this.respondWithTravelerHistories("flight 2 histories TODO");
	    // TODO: check tamrIds set for passengers.
	    
	    // TODO: check traveler history groups passengers with same tamrId.
	}
}
