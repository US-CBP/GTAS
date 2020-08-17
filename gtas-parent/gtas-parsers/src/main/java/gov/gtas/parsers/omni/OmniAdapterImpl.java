/*
 *  All Application code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  Please see LICENSE.txt for details.
 */

package gov.gtas.parsers.omni;

import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerDetails;
import gov.gtas.parsers.omni.model.OmniDerogListEntry;
import gov.gtas.parsers.omni.model.OmniDocument;
import gov.gtas.parsers.omni.model.OmniPassenger;
import gov.gtas.parsers.omni.util.DateHelper;
import gov.gtas.parsers.omni.model.OmniRawProfile;
import gov.gtas.services.FlightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OmniAdapterImpl implements OmniAdapter {

    private FlightService flightService;

    private Logger logger = LoggerFactory.getLogger(OmniAdapterImpl.class);

    public OmniAdapterImpl(FlightService flightService) {
        this.flightService = flightService;
    }

	@Override
	public List<OmniPassenger> convertPassengers(
	        Flight flight, Set<Passenger> passengers) {
	    return passengers.stream()
	            .map(passenger ->
	                    convertPassengerToOmniRawProfile(flight, passenger))
	            .collect(Collectors.toList());
	}

    @Override
	public OmniPassenger convertPassengerToOmniRawProfile(Flight flight, Passenger passenger) {
		OmniPassenger omniPassenger = new OmniPassenger();
        OmniRawProfile omniRawProfile = new OmniRawProfile();

		PassengerDetails passengerDetails = passenger.getPassengerDetails();
		Set<Document> documentSet = passenger.getDocuments();
		List<Document> documentList = documentSet.stream().collect(Collectors.toList());
		List<String> documentationCountryList = new ArrayList<>();
		List<String> documentationTypeList = new ArrayList<>();

        // Basic passenger information
        omniPassenger.setGtasId(passenger.getId().toString());
        omniPassenger.setFirstName(passengerDetails.getFirstName());
        omniPassenger.setMiddleName(passengerDetails.getMiddleName());
        omniPassenger.setLastName(passengerDetails.getLastName());
        omniPassenger.setDob(passengerDetails.getDob());

        // Convert documents
        omniPassenger.setDocuments(passenger.getDocuments().stream()
                .map(document -> convertDocumentToOmniDocument(document))
                .collect(Collectors.toList()));

        // Raw Profile information
		omniRawProfile.setUniqueId(passenger.getUuid().toString());
		omniRawProfile.setPassengerNumber(passengerDetails.getPassengerId());
		omniRawProfile.setGender(passengerDetails.getGender());
		omniRawProfile.setAgeBin(DateHelper.convertIntegerToAgeBin(passengerDetails.getAge()));
		omniRawProfile.setEta(flight.getMutableFlightDetails().getEta().getTime()/1000); // The ETA is in seconds
		// Nationalities (only one in GTAS)
		omniRawProfile.setPassengerCitizenCountry(Collections
				.singletonList(passengerDetails.getNationality()));
		for (Document document: documentList)  {
			documentationCountryList.add(document.getIssuanceCountry());
			documentationTypeList.add(document.getDocumentType());
		}
		omniRawProfile.setDocumentationCountry(documentationCountryList);
		omniRawProfile.setDocumentationType(documentationTypeList);

		omniRawProfile.setFlightNumber(flight.getFlightNumber());
		omniRawProfile.setCarrier(flight.getCarrier());
		omniRawProfile.setFlightOriginCountry(flight.getOriginCountry());
		omniRawProfile.setFlightOriginAirport(flight.getOrigin());
		omniRawProfile.setFlightArrivalCountry(flight.getDestinationCountry());
		omniRawProfile.setFlightArrivalAirport(flight.getDestination());

		omniPassenger.setOmniRawProfile(omniRawProfile);

		return omniPassenger;
	}

    private OmniDocument convertDocumentToOmniDocument(Document document) {
        OmniDocument omniDocument = new OmniDocument();

        omniDocument.setDocumentId(document.getDocumentNumber());
        omniDocument.setDocumentType(document.getDocumentType());
        omniDocument.setDocumentIssuingCountry(document.getIssuanceCountry());

        return omniDocument;
    }
}
