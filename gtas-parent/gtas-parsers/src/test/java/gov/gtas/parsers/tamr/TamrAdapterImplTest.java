/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.parsers.tamr;

import gov.gtas.model.*;
import gov.gtas.parsers.tamr.model.TamrPassengerSendObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.*;

import static java.time.ZoneOffset.UTC;

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
		p.setDocuments(documentSet);

		testFlight = flight;
		testPassenger = p;
	}

	@Test
	public void tamrConversionTest() {
		TamrAdapterImpl tamrAdapter = new TamrAdapterImpl();
		List<TamrPassengerSendObject> tamrPassengerSendObjectList = tamrAdapter.convert(testFlight,
				Collections.singleton(testPassenger));
		TamrPassengerSendObject tpso = tamrPassengerSendObjectList.get(0);
		Assert.assertEquals(birthDateTest, tpso.getDOB_Date());
		Assert.assertEquals("WALLY", tpso.getFirst_name());
		Assert.assertEquals(etaTestDate, tpso.getETA_DT());
		Assert.assertEquals("IAD", tpso.getAPIS_ARVL_APRT_CD());
		Assert.assertEquals("ABC", tpso.getAPIS_DPRTR_APRT_CD());
		Assert.assertEquals("M", tpso.getGNDR_CD());
		Assert.assertEquals("5", tpso.getGtasId());
		Assert.assertEquals("HUND", tpso.getLast_name());
	}
}
