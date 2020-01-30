/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Iterables;

import gov.gtas.config.TestCommonServicesConfig;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.repository.FlightRepository;
import gov.gtas.repository.PassengerIDTagRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.services.LoaderStatistics;
import gov.gtas.services.PassengerService;

/**
 * End-to-end integration tests with Tamr.
 * @author Cassidy Laidlaw
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestCommonServicesConfig.class })
@Rollback(true)
public class TamrIT extends AbstractTransactionalJUnit4SpringContextTests {
    private ClassLoader classLoader;
    
    @Autowired
    private LoaderScheduler loaderScheduler;

    @Autowired
    private FlightRepository flightRepository;
    
    @Autowired
    private PassengerRepository passengerRepository;
    
    @Autowired
    private PassengerService passengerService;
    
    @Autowired
    private PassengerIDTagRepository passengerIDTagRepository;
    
    @Autowired
    private TamrIntegrationTestUtils tamrUtils;
    
    @Autowired
    private TamrMock tamrMock;

    @Before
    public void setUp() throws Exception {
        classLoader = getClass().getClassLoader();
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
    
    private void loadFlight(String messageFilePath, String[] primeFlightKey)
            throws Exception {
        File messageFile = new File(classLoader.getResource(
                messageFilePath).getFile());
        LoaderStatistics stats = new LoaderStatistics();
        loaderScheduler.processSingleFile(messageFile, stats, primeFlightKey);
    }
    
    private long getPassengerIdFromName(String firstName, String lastName) {
        Long passengerId = null;
        for (Flight flight: flightRepository.findAll()) {
            Set<Passenger> passengers = 
                    passengerRepository.returnAPassengerFromParameters(
                            flight.getId(), firstName, lastName);
            if (passengers.isEmpty()) {
                // Keep trying more flights.
            } else {
                assertEquals(1, passengers.size());
                if (passengerId == null) {
                    passengerId = Iterables.getOnlyElement(passengers).getId();
                } else {
                    fail(String.format("Multiple passengers matched %s %s",
                            firstName, lastName));
                }
            }
        }
        assertNotNull(passengerId);
        return passengerId;
    }
    
    /**
     * Makes sure the tamrIds match for the passengers with the given names
     * and also that GTAS traveler history groups them together.
     */
    private void assertTamrIdsMatch(String ...names) {
        Set<Long> passengerIds = Arrays.stream(names).map((name) -> {
            String[] nameParts = name.split(" ");
            assertEquals(2, nameParts.length);
            String firstName = nameParts[0], lastName = nameParts[1];
            return getPassengerIdFromName(firstName, lastName);
        }).collect(Collectors.toSet());
        
        Set<String> tamrIds = passengerIds.stream()
                .map((passengerId) -> passengerIDTagRepository
                        .findByPaxId(passengerId).getTamrId())
                .collect(Collectors.toSet());
        // Should be only one tamrId for all these passengers.
        assertEquals(1, tamrIds.size());
        
        for (long passengerId: passengerIds) {
            Set<Long> historyMatches = passengerService
                    .getBookingDetailHistoryByPaxID(passengerId)
                    .stream()
                    .map((passenger) -> passenger.getId())
                    .collect(Collectors.toSet());
            // Looking up booking history should give all matching passengers.
            assertEquals(passengerIds, historyMatches);
        }
    }
    
    @Test
    @Transactional
    public void testTamrIntegration() throws Exception {
        tamrUtils.disableJmsListeners();
        
        this.createWatchlistItems();
        this.sendWatchlistItemsToTamr();
        this.assertDerogListReceived();

        // Flight 1
        this.loadFlight(
           "tamr-integration-data/flight1.txt",
            new String[] { "SCL", "JFK", "LA", "0532",
                    "1579392000000", "1579394000000" }
        );
        // 10 passengers should be processed by Tamr.
        assertEquals(10, tamrMock.respondToQuery());
        // Let GTAS process responses (should be 2).
        assertEquals(2, tamrUtils.synchronouslyProcessMessagesFromTamr());
        
        // TODO: check derog hits.

        // Flight 2
        this.loadFlight(
            "tamr-integration-data/flight2.txt",
            new String[] { "IAD", "BRU", "YY", "0123",
                    "1587686400000", "1587686500000" }
        );
        // 10 passengers should be processed by Tamr.
        assertEquals(10, tamrMock.respondToQuery());
        
        // Flight 3
        this.loadFlight(
            "tamr-integration-data/flight3.txt",
            new String[] { "KEF", "JFK", "FI", "0615",
                    "1582675200000", "1582675400000" }
        );
        // 10 passengers should be processed by Tamr.
        assertEquals(10, tamrMock.respondToQuery());

        // Let GTAS process responses (should be 4).
        assertEquals(4, tamrUtils.synchronouslyProcessMessagesFromTamr());
        tamrUtils.synchronouslyProcessMessagesFromTamr();

        assertTamrIdsMatch(
            "HSIUYUAN JIANG",
            "XIUYUAN JIANG",
            "XIUYUAN CHIANG"
        );
        assertTamrIdsMatch(
            "GAYLA JOSEPH",
            "GAELLA JOSEPH"
        );
    }
}
