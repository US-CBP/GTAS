/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.parsers.tamr;

import static java.time.ZoneOffset.UTC;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.MutableFlightDetails;
import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerDetails;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.parsers.tamr.model.TamrDerogListEntry;
import gov.gtas.parsers.tamr.model.TamrDerogListUpdate;
import gov.gtas.parsers.tamr.model.TamrPassenger;
import gov.gtas.parsers.tamr.model.TamrQuery;

public class TamrAdapterImplTest {
    private TamrAdapterImpl tamrAdapter = new TamrAdapterImpl();

	@Test
	public void testPassengerConversion() throws JsonProcessingException {
        LocalDate eta = LocalDate.of(2020, 1, 24);
        Date etaFlightDate = Date.from(eta.atStartOfDay(UTC).toInstant());
        LocalDate localBirthDate = LocalDate.of(1988, 4, 24);
        Date birthDate = Date.from(localBirthDate.atStartOfDay(UTC).toInstant());
        Date birthDateTest = Date.from(localBirthDate.atStartOfDay(UTC).toInstant());

        Flight flight = new Flight();
        MutableFlightDetails mfd = new MutableFlightDetails();
        mfd.setEta(etaFlightDate);
        flight.setDestination("IAD");
        flight.setOrigin("ABC");
        flight.setCarrier("WAL");
        flight.setFlightNumber("ABC123");
        flight.setMutableFlightDetails(mfd);
        Passenger passenger = new Passenger();
        passenger.setId(5L);
        PassengerDetails passengerDetails = new PassengerDetails(passenger);
        passenger.setPassengerDetails(passengerDetails);
        passengerDetails.setDob(birthDate);
        passengerDetails.setNationality("USA");
        passengerDetails.setFirstName("WALLY");
        passengerDetails.setLastName("HUND");
        passengerDetails.setGender("M");
        Set<Document> documentSet = new HashSet<>();
        Document document = new Document();
        document.setId(1L);
        document.setDocumentNumber("123123123");
        document.setDocumentType("P");
        document.setIssuanceCountry("USA");
        documentSet.add(document);
        passenger.setDocuments(documentSet);
        
		List<TamrPassenger> tamrPassengers = tamrAdapter.convertPassengers(
		        flight, Collections.singleton(passenger));
		TamrPassenger tamrPassenger = tamrPassengers.get(0);
		assertEquals(birthDateTest, tamrPassenger.getDob());
		assertEquals("WALLY", tamrPassenger.getFirstName());
		assertEquals("5", tamrPassenger.getGtasId());
		assertEquals("HUND", tamrPassenger.getLastName());

        TamrQuery tamrQuery = new TamrQuery(tamrPassengers);
        
        ObjectMapper mapper = new ObjectMapper();
        String tamrQueryJson = mapper.writer()
                .writeValueAsString(tamrQuery);
        
        assertEquals("{\"passengers\":[{\"gtasId\":\"5\",\"firstName\":\"WALLY\",\"middleName\":null,\"lastName\":\"HUND\",\"gender\":\"M\",\"dob\":\"1988-04-24\",\"documents\":[{\"documentId\":\"123123123\",\"documentType\":\"P\",\"documentIssuingCountry\":\"USA\"}],\"citizenshipCountry\":[\"USA\"]}]}",
                tamrQueryJson);
	}
	
	private static final String[] WATCHLIST_ITEMS = {
        "{\"id\":null,\"action\":null,\"terms\":[{\"field\":\"firstName\",\"type\":\"string\",\"value\":\"SHING\"},{\"field\":\"lastName\",\"type\":\"string\",\"value\":\"QUAN\"},{\"field\":\"dob\",\"type\":\"date\",\"value\":\"1998-08-08\"}]}",
        "{\"id\":null,\"action\":null,\"terms\":[{\"field\":\"documentType\",\"type\":\"string\",\"value\":\"P\"},{\"field\":\"documentNumber\",\"type\":\"string\",\"value\":\"221371771\"}]}",
        "{\"id\":null,\"action\":null,\"terms\":[{\"field\":\"firstName\",\"type\":\"string\",\"value\":\"FRIEDA\"},{\"field\":\"lastName\",\"type\":\"string\",\"value\":\"DARRINGTON\"},{\"field\":\"dob\",\"type\":\"date\",\"value\":\"1963-08-13\"}]}",
        "{\"id\":null,\"action\":null,\"terms\":[{\"field\":\"documentType\",\"type\":\"string\",\"value\":\"P\"},{\"field\":\"documentNumber\",\"type\":\"string\",\"value\":\"899294368\"}]}"
	};
	
	@Test
	public void testWatchlistConversion() throws JsonProcessingException {
	    List<WatchlistItem> watchlistItems = new ArrayList<>();
	    for (int i = 0; i < WATCHLIST_ITEMS.length; i++) {
	        WatchlistItem watchlistItem = new WatchlistItem();
	        watchlistItem.setItemData(WATCHLIST_ITEMS[i]);
	        watchlistItem.setId((long) i + 1);
	        watchlistItems.add(watchlistItem);
	    }
	    
	    List<TamrDerogListEntry> derogList = 
	            tamrAdapter.convertWatchlist(watchlistItems);
	  
	    // Only 2 entries in the derog list, since document entries should
	    // be filtered out.
	    assertEquals(2, derogList.size());
	    
	    Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
	    
	    TamrDerogListEntry entry = derogList.get(0);
	    assertEquals(entry.getFirstName(), "SHING");
	    assertEquals(entry.getLastName(), "QUAN");
	    calendar.set(1998, 7, 8);
	    assertEquals(calendar.getTime(), entry.getDob());
	    assertNull(entry.getDocuments());

        entry = derogList.get(1);
        assertEquals(entry.getFirstName(), "FRIEDA");
        assertEquals(entry.getLastName(), "DARRINGTON");
        calendar.set(1963, 7, 13);
        assertEquals(calendar.getTime(), entry.getDob());
        assertNull(entry.getDocuments());
        
        TamrDerogListUpdate derogListUpdate =
                new TamrDerogListUpdate(derogList);
        
        ObjectMapper mapper = new ObjectMapper();
        String derogListUpdateJson = mapper.writer()
                .writeValueAsString(derogListUpdate);
        
        assertEquals(
                "{\"passengers\":[" +
                "{\"gtasId\":\"1\",\"firstName\":\"SHING\",\"middleName\":null,\"lastName\":\"QUAN\",\"gender\":null,\"dob\":\"1998-08-08\",\"documents\":null,\"citizenshipCountry\":null,\"derogId\":\"1\"}," +
                "{\"gtasId\":\"3\",\"firstName\":\"FRIEDA\",\"middleName\":null,\"lastName\":\"DARRINGTON\",\"gender\":null,\"dob\":\"1963-08-13\",\"documents\":null,\"citizenshipCountry\":null,\"derogId\":\"3\"}" +
                "]}",
                derogListUpdateJson);
	}
	
}
