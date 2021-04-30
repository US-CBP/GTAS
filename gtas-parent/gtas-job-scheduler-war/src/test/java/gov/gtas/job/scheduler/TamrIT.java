/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Iterables;

import gov.gtas.model.Flight;
import gov.gtas.model.HitDetail;
import gov.gtas.model.Passenger;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.parsers.tamr.TamrDerogReplaceScheduler;
import gov.gtas.repository.FlightRepository;
import gov.gtas.repository.HitDetailRepository;
import gov.gtas.repository.PassengerIDTagRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.repository.PendingHitDetailRepository;
import gov.gtas.repository.watchlist.WatchlistItemRepository;
import gov.gtas.services.PassengerService;

/**
 * End-to-end integration tests with Tamr.
 * @author Cassidy Laidlaw
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TamrIntegrationTestConfig.class })
@Rollback(true)
public class TamrIT extends AbstractTransactionalJUnit4SpringContextTests {
    private Logger logger = LoggerFactory.getLogger(TamrIT.class);
        
    @Autowired
    private ApplicationContext applicationContext;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private FlightRepository flightRepository;
    
    @Autowired
    private PassengerRepository passengerRepository;
    
    @Autowired
    private PassengerService passengerService;
    
    @Autowired
    private PassengerIDTagRepository passengerIDTagRepository;
    
    @Autowired
    private PendingHitDetailRepository pendingHitDetailRepository;
    
    @Autowired
    private HitDetailRepository hitDetailRepository;
    
    @Autowired
    private WatchlistItemRepository watchlistItemRepository;
    
    @Autowired
    private TamrDerogReplaceScheduler derogReplaceScheduler;
    
    @Autowired
    private TamrIntegrationTestUtils tamrUtils;
    
    @Autowired
    private TamrMock tamrMock;
    
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
     * Clears Passenger objects from Hibernate's cache, because the cache
     * within the test transaction causes issues with loading passengers'
     * associated flights.
     */
    private void clearCachedPassengers() {
        Session session = entityManager.unwrap(Session.class);
        passengerRepository.findAll().forEach((passenger) -> {
            session.evict(passenger);
        });
    }
    
    /**
     * Runs the normally scheduled task of persisting pending watchlist hits
     * to the database.
     */
    private void persistWatchlistHits() {
//        AsyncHitPersistenceThread persistenceThread =
//                new AsyncHitPersistenceThread(
//                        pendingHitDetailRepository, applicationContext);
//        Set<Long> allFlightIds = flightRepository.findAll()
//                .stream()
//                .map((flight) -> flight.getId())
//                .collect(Collectors.toSet());
//        persistenceThread.setFlightIds(allFlightIds);
//        persistenceThread.call();
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
        
        tamrUtils.createWatchlistItems();
        derogReplaceScheduler.jobScheduling();
        // 3 derog entries should be processed by Tamr.
        assertEquals(3, tamrMock.respondToDerogReplace());
        // GTAS should get acknowledgement from Tamr.
        assertEquals(1, tamrUtils.synchronouslyProcessMessagesFromTamr());

        // Flight 1
        tamrUtils.loadFlight(
           "tamr-integration-data/flight1.txt",
            new String[] { "SCL", "JFK", "LA", "0532",
                    "1579392000000", "1579394000000" }
        );
        // 10 passengers should be processed by Tamr.
        assertEquals(10, tamrMock.respondToQuery());
        // Let GTAS process responses (should be 2).
        assertEquals(2, tamrUtils.synchronouslyProcessMessagesFromTamr());

        // Flight 2
        tamrUtils.loadFlight(
            "tamr-integration-data/flight2.txt",
            new String[] { "KEF", "JFK", "FI", "0615",
                    "1582675200000", "1582675400000" }
        );
        // 10 passengers should be processed by Tamr.
        assertEquals(10, tamrMock.respondToQuery());
        
        // Flight 3
        tamrUtils.loadFlight(
            "tamr-integration-data/flight3.txt",
            new String[] { "IAD", "BRU", "YY", "0123",
                    "1587686400000", "1587686500000" }
        );
        // 10 passengers should be processed by Tamr.
        assertEquals(10, tamrMock.respondToQuery());
        
        clearCachedPassengers();

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
        
        // Check derog hit.
        this.persistWatchlistHits();
        long hitPassengerId = getPassengerIdFromName(
                "REUBEN", "THEBAULT");
        Set<HitDetail> hitDetailSet =
                hitDetailRepository.getSetFromPassengerId(hitPassengerId);
        assertEquals(1, hitDetailSet.size());
        HitDetail hitDetails = hitDetailSet.iterator().next();
        assertEquals(0.6, hitDetails.getPercentage(), 0.01);
        assertTrue(hitDetails.getTitle().contains("Tamr"));
        assertTrue(hitDetails.getDescription().contains("Tamr"));
        WatchlistItem hitWatchlistItem = watchlistItemRepository.findOne(
                hitDetails.getHitMakerId());
        assertNotNull(hitWatchlistItem);
        assertTrue(hitWatchlistItem.getItemData().contains("RUBEN"));
        assertTrue(hitWatchlistItem.getItemData().contains("THEBAULT"));
        assertTrue(hitWatchlistItem.getItemData().contains("1945-01-11"));
    }
}
