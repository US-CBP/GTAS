/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.parsers.tamr;

import static java.time.ZoneOffset.UTC;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.MutableFlightDetails;
import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerDetails;
import gov.gtas.parsers.tamr.model.TamrPassenger;
import gov.gtas.parsers.tamr.model.TamrQuery;

public class TamrAdapterImplTest {

	Flight testFlight;
	Passenger testPassenger;
	LocalDate eta;
	Date etaFlightDate;
	Date etaTestDate;
	LocalDate localBirthDate;
	Date birthDate;
	Date birthDateTest;

	@Before
	public void setUp() {
		eta = LocalDate.of(2020, 1, 24);
		etaFlightDate = Date.from(eta.atStartOfDay(UTC).toInstant());
		etaTestDate = Date.from(eta.atStartOfDay(UTC).toInstant());
		localBirthDate = LocalDate.of(1988, 4, 24);
		birthDate = Date.from(localBirthDate.atStartOfDay(UTC).toInstant());
		birthDateTest = Date.from(localBirthDate.atStartOfDay(UTC).toInstant());

		Flight flight = new Flight();
		MutableFlightDetails mfd = new MutableFlightDetails();
		mfd.setEta(etaFlightDate);
		flight.setDestination("IAD");
		flight.setOrigin("ABC");
		flight.setCarrier("WAL");
		flight.setFlightNumber("ABC123");
		flight.setMutableFlightDetails(mfd);
		Passenger p = new Passenger();
		p.setId(5L);
		PassengerDetails passengerDetails = new PassengerDetails(p);
		p.setPassengerDetails(passengerDetails);
		passengerDetails.setDob(birthDate);
		passengerDetails.setNationality("USA");
		passengerDetails.setFirstName("WALLY");
		passengerDetails.setLastName("HUND");
		passengerDetails.setGender("M");
		Set<Document> documentSet = new HashSet<>();
		Document d = new Document();
		d.setId(1L);
		d.setDocumentNumber("123123123");
		d.setDocumentType("P");
		d.setIssuanceCountry("USA");
		documentSet.add(d);
		p.setDocuments(documentSet);

		testFlight = flight;
		testPassenger = p;
	}

	@Test
	public void passengerConversionTest() {
		TamrAdapterImpl tamrAdapter = new TamrAdapterImpl();
		List<TamrPassenger> tamrPassengers = tamrAdapter.convertPassengers(
		        testFlight, Collections.singleton(testPassenger));
		TamrPassenger tamrPassenger = tamrPassengers.get(0);
		Assert.assertEquals(birthDateTest, tamrPassenger.getDob());
		Assert.assertEquals("WALLY", tamrPassenger.getFirstName());
		Assert.assertEquals("5", tamrPassenger.getGtasId());
		Assert.assertEquals("HUND", tamrPassenger.getLastName());
	}
	
	@Test
	public void passengerJsonConversionTest() throws JsonProcessingException {
        TamrAdapterImpl tamrAdapter = new TamrAdapterImpl();
        List<TamrPassenger> tamrPassengers = tamrAdapter.convertPassengers(
                testFlight, Collections.singleton(testPassenger));
        TamrQuery tamrQuery = new TamrQuery(tamrPassengers);
        
        ObjectMapper mapper = new ObjectMapper();
        String tamrQueryJson = mapper.writer()
                .writeValueAsString(tamrQuery);
        
        Assert.assertEquals("{\"passengers\":[{\"gtasId\":\"5\",\"firstName\":\"WALLY\",\"middleName\":null,\"lastName\":\"HUND\",\"gender\":\"M\",\"dob\":\"1988-04-24\",\"documents\":[{\"documentId\":\"123123123\",\"documentType\":\"P\",\"documentIssuingCountry\":\"USA\"}],\"citizenshipCountry\":[\"USA\"]}]}",
                tamrQueryJson);
	}
}
