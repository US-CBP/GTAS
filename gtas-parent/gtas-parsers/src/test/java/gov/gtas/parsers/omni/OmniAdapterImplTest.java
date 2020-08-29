/*
 *  All Application code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.omni;

import static java.time.ZoneOffset.UTC;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.MutableFlightDetails;
import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerDetails;
import gov.gtas.parsers.omni.model.OmniPassenger;
import gov.gtas.parsers.omni.model.OmniAssessPassengersRequest;
import java.math.BigInteger;

public class OmniAdapterImplTest {
    private OmniAdapterImpl omniAdapter = new OmniAdapterImpl(null);
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testPassengerConversion() throws JsonProcessingException {
        List<JSONObject> expectedOmniPassengerList = new ArrayList<>(1);
        JSONObject expectedOmniPassenger = new JSONObject();
        LocalDate eta = LocalDate.of(2020, 1, 24);
        Date etaFlightDate = Date.from(eta.atStartOfDay(UTC).toInstant());
        LocalDate localBirthDate = LocalDate.of(1988, 4, 24);
        Date birthDate = Date.from(localBirthDate.atStartOfDay(UTC).toInstant());
        Date birthDateTest = Date.from(localBirthDate.atStartOfDay(UTC).toInstant());

        String s = "0f14d0ab-9605-4a62-a9e4-5ed26688389b";
        String s2 = s.replace("-", "");
        UUID uuid = new UUID(
                new BigInteger(s2.substring(0, 16), 16).longValue(),
                new BigInteger(s2.substring(16), 16).longValue());

        Flight flight = new Flight();
        MutableFlightDetails mfd = new MutableFlightDetails();
        mfd.setEta(etaFlightDate);
        flight.setEtdDate(etaFlightDate);
        flight.setDestinationCountry("CAN");
        flight.setDestination("YOW");
        flight.setOriginCountry("USA");
        flight.setOrigin("JFK");
        flight.setCarrier("WAL");
        flight.setFlightNumber("ABC123");
        flight.setMutableFlightDetails(mfd);
        Passenger passenger = new Passenger();
        passenger.setId(5L);
        passenger.setUuid(uuid);
        PassengerDetails passengerDetails = new PassengerDetails(passenger);
        passenger.setPassengerDetails(passengerDetails);
        passengerDetails.setPassengerId(5L);
        passengerDetails.setDob(birthDate);
        passengerDetails.setAge(32);
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

        try {
            List<OmniPassenger> omniPassengers = omniAdapter.convertPassengers(
                        flight, Collections.singleton(passenger));
            OmniPassenger omniPassenger = omniPassengers.get(0);
            assertEquals(birthDateTest, omniPassenger.getDob());
            assertEquals("WALLY", omniPassenger.getFirstName());
            assertEquals("5", omniPassenger.getGtasId());
            assertEquals("HUND", omniPassenger.getLastName());

            expectedOmniPassenger.put("passengerNumber", 5L);
            expectedOmniPassenger.put("uniqueId", uuid.toString());
            expectedOmniPassenger.put("gender", "M");
            expectedOmniPassenger.put("ageBin", "30-35");
            expectedOmniPassenger.put("eta", 1579824000L);
            expectedOmniPassenger.put("passengerCitizenCountry", "USA");
            expectedOmniPassenger.put("documentationCountry", "USA");
            expectedOmniPassenger.put("documentationType", "P");
            expectedOmniPassenger.put("flightNumber", "ABC123");
            expectedOmniPassenger.put("carrier", "WAL");
            expectedOmniPassenger.put("flightOriginCountry", "USA");
            expectedOmniPassenger.put("flightOriginAirport", "JFK");
            expectedOmniPassenger.put("flightArrivalCountry", "CAN");
            expectedOmniPassenger.put("flightArrivalAirport", "YOW");

            assertEquals(expectedOmniPassenger.get("passengerNumber"), omniPassenger.getOmniRawProfile().getPassengerNumber());
            assertEquals(expectedOmniPassenger.get("uniqueId"), omniPassenger.getOmniRawProfile().getUniqueId());
            assertEquals(expectedOmniPassenger.get("gender"), omniPassenger.getOmniRawProfile().getGender());
            assertEquals(expectedOmniPassenger.get("ageBin"), omniPassenger.getOmniRawProfile().getAgeBin());
            assertEquals(expectedOmniPassenger.get("eta"), omniPassenger.getOmniRawProfile().getEta());
            assertThat(expectedOmniPassenger.get("passengerCitizenCountry"),
                    is(omniPassenger.getOmniRawProfile().getPassengerCitizenCountry().get(0)));
            assertThat(expectedOmniPassenger.get("documentationCountry"), is(omniPassenger.getOmniRawProfile().getDocumentationCountry().get(0)));
            assertThat(expectedOmniPassenger.get("documentationType"), is(omniPassenger.getOmniRawProfile().getDocumentationType().get(0)));
            assertEquals(expectedOmniPassenger.get("flightNumber"), omniPassenger.getOmniRawProfile().getFlightNumber());
            assertEquals(expectedOmniPassenger.get("carrier"), omniPassenger.getOmniRawProfile().getCarrier());
            assertEquals(expectedOmniPassenger.get("flightOriginCountry"), omniPassenger.getOmniRawProfile().getFlightOriginCountry());
            assertEquals(expectedOmniPassenger.get("flightOriginAirport"), omniPassenger.getOmniRawProfile().getFlightOriginAirport());
            assertEquals(expectedOmniPassenger.get("flightArrivalCountry"), omniPassenger.getOmniRawProfile().getFlightArrivalCountry());
            assertEquals(expectedOmniPassenger.get("flightArrivalAirport"), omniPassenger.getOmniRawProfile().getFlightArrivalAirport());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
