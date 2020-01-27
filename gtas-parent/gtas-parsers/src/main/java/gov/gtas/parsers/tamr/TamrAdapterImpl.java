/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.parsers.tamr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerDetails;
import gov.gtas.parsers.tamr.model.TamrDocument;
import gov.gtas.parsers.tamr.model.TamrPassenger;
import gov.gtas.services.FlightService;

public class TamrAdapterImpl implements TamrAdapter {

    @Autowired
    FlightService flightService;

	@Override
	public List<TamrPassenger> convert(Flight flight, Set<Passenger> passengers) {
	    return passengers.stream()
	            .map(passenger ->
	                    convertPassengerToTamrPassenger(flight, passenger))
	            .collect(Collectors.toList());
	}

	private TamrPassenger convertPassengerToTamrPassenger(Flight flight, Passenger passenger) {
		TamrPassenger tamrPassenger = new TamrPassenger();

		// Basic passenger information
		PassengerDetails passengerDetails = passenger.getPassengerDetails();
		tamrPassenger.setGtasId(passenger.getId().toString());
		tamrPassenger.setFirstName(passengerDetails.getFirstName());
		tamrPassenger.setMiddleName(passengerDetails.getMiddleName());
		tamrPassenger.setLastName(passengerDetails.getLastName());
		tamrPassenger.setGender(passengerDetails.getGender());
		tamrPassenger.setDob(passengerDetails.getDob());
		
		// Convert documents
		tamrPassenger.setDocuments(passenger.getDocuments().stream()
		        .map(document -> convertDocumentToTamrDocument(document))
		        .collect(Collectors.toList()));
		
		// Nationalities (only one in GTAS)
		tamrPassenger.setCitizenshipCountry(Collections
		        .singletonList(passengerDetails.getNationality()));

		return tamrPassenger;
	}

	private TamrDocument convertDocumentToTamrDocument(Document document) {
		TamrDocument tamrDocument = new TamrDocument();

		tamrDocument.setDocumentId(document.getDocumentNumber());
		tamrDocument.setDocumentType(document.getDocumentType());
		tamrDocument.setDocumentIssuingCountry(document.getIssuanceCountry());

		return tamrDocument;
	}
}
