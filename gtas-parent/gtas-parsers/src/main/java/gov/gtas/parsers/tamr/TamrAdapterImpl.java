/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.parsers.tamr;

import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.parsers.tamr.model.TamrDocumentSendObject;
import gov.gtas.parsers.tamr.model.TamrPassengerSendObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TamrAdapterImpl implements TamrAdapter {

	@Override
	public List<TamrPassengerSendObject> convert(Flight flight, Set<Passenger> passengers) {
		List<TamrPassengerSendObject> tamrPassengers = new ArrayList<TamrPassengerSendObject>();
		for (Passenger passenger : passengers) {
			// per passenger convert to tamrPax
			TamrPassengerSendObject tamrPax = convertPassengerToTamrPassenger(flight, passenger);
			// get docs from passengers
			for (Document doc : passenger.getDocuments()) {
				// add tamrDoc to tamrPax doc list
				tamrPax.getDocuments().add(convertDocumentToTamrDocument(doc));
			}
			tamrPassengers.add(tamrPax);
		}
		return tamrPassengers;
	}

	private TamrPassengerSendObject convertPassengerToTamrPassenger(Flight flight, Passenger passenger) {
		TamrPassengerSendObject tamrPax = new TamrPassengerSendObject();
		// initializing doc list
		List<TamrDocumentSendObject> docs = new ArrayList<>();
		tamrPax.setDocuments(docs);

		// flight related
		tamrPax.setAPIS_ARVL_APRT_CD(flight.getDestination());
		tamrPax.setAPIS_DPRTR_APRT_CD(flight.getOrigin());
		tamrPax.setETA_DT(flight.getMutableFlightDetails().getEta());
		tamrPax.setIATA_CARR_CD(flight.getCarrier());
		tamrPax.setFLIT_NBR(flight.getFullFlightNumber());

		// pax related
		List<String> nationalities = new ArrayList<>();
		nationalities.add(passenger.getPassengerDetails().getNationality());
		tamrPax.setNATIONALITY_CD(nationalities);
		tamrPax.setDOB_Date(passenger.getPassengerDetails().getDob());
		tamrPax.setFirst_name(passenger.getPassengerDetails().getFirstName());
		tamrPax.setGNDR_CD(passenger.getPassengerDetails().getGender());
		tamrPax.setGtasId(passenger.getId().toString());
		tamrPax.setLast_name(passenger.getPassengerDetails().getLastName());

		// temp values
		tamrPax.setUid("");
		tamrPax.setFlt("test");

		return tamrPax;
	}

	private TamrDocumentSendObject convertDocumentToTamrDocument(Document doc) {
		TamrDocumentSendObject tamrDoc = new TamrDocumentSendObject();

		tamrDoc.setDOC_CTRY_CD(doc.getIssuanceCountry());
		tamrDoc.setDOC_ID(doc.getDocumentNumber());
		tamrDoc.setDOC_TYP_NM(doc.getDocumentType());

		return tamrDoc;
	}
}
