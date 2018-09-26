/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.tamr;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.parsers.tamr.jms.TamrMessageSender;
import gov.gtas.parsers.tamr.model.TamrDocumentSendObject;
import gov.gtas.parsers.tamr.model.TamrPassengerSendObject;
import gov.gtas.repository.FlightRepository;
import gov.gtas.services.FlightService;

@Component
public class TamrConversionServiceImpl implements TamrConversionService {
		
		@Autowired
		FlightService fService;
		
		@Autowired
		FlightRepository flightRepo;
		
		@Autowired
		TamrMessageSender messageSender;
		
		static final Logger logger = LoggerFactory.getLogger(TamrConversionService.class);
		
		//Convenience method. Receives list of Flights to convert to send objects for batch dump uses other single flight method
		public List<TamrPassengerSendObject> convertGTASFlightToTamrMessage(Set<Flight> flights){
			List<TamrPassengerSendObject> passengers = new ArrayList<TamrPassengerSendObject>();
			
			for(Flight flight : flights){
				passengers.addAll(convertGTASFlightToTamrMessage(flight));
			}
			
			return passengers;
		}
		
		public List<TamrPassengerSendObject> convertGTASFlightToTamrMessage(Flight flight){
			List<TamrPassengerSendObject> passengers = new ArrayList<TamrPassengerSendObject>();
			
			//get passengers from flight
			for(Passenger passenger: fService.getAllPassengers(flight.getId())){
				//per passenger convert to tamrPax
				TamrPassengerSendObject tamrPax = convertPassengerToTamrPassenger(flight, passenger);
				//get docs from passengers
				for(Document doc : passenger.getDocuments()){
					//add tamrDoc to tamrPax doc list
					tamrPax.getDocuments().add(convertDocumentToTamrDocument(doc));
				}
			}
			return passengers;
		}
		
		private List<TamrPassengerSendObject> convertGTASFlightAndPassengersToTamrMessage(Flight flight, Set<Passenger> passengers){
			List<TamrPassengerSendObject> tamrPassengers = new ArrayList<TamrPassengerSendObject>();
			for(Passenger passenger: passengers){
				//per passenger convert to tamrPax
				TamrPassengerSendObject tamrPax = convertPassengerToTamrPassenger(flight, passenger);
				//get docs from passengers
				for(Document doc : passenger.getDocuments()){
					//add tamrDoc to tamrPax doc list
					tamrPax.getDocuments().add(convertDocumentToTamrDocument(doc));
				}
				tamrPassengers.add(tamrPax);
			}
			return tamrPassengers; 
		}
		
		private TamrPassengerSendObject convertPassengerToTamrPassenger(Flight flight, Passenger passenger){
			TamrPassengerSendObject tamrPax = new TamrPassengerSendObject();
			//initializing doc list
			List<TamrDocumentSendObject> docs = new ArrayList<TamrDocumentSendObject>(); 
			tamrPax.setDocuments(docs);
			
			//flight related
			tamrPax.setAPIS_ARVL_APRT_CD(flight.getDestination());
			tamrPax.setAPIS_DPRTR_APRT_CD(flight.getOrigin());
			tamrPax.setETA_DT(flight.getEtaDate());
			tamrPax.setIATA_CARR_CD(flight.getCarrier());
			tamrPax.setFLIT_NBR(flight.getFullFlightNumber());
			
			//pax related
			List<String> citizenshipCountries = new ArrayList<String>();
			citizenshipCountries.add(passenger.getCitizenshipCountry());
			tamrPax.setCTZNSHP_CTRY_CD(citizenshipCountries);
			tamrPax.setDOB_Date(passenger.getDob());
			tamrPax.setFirst_name(passenger.getFirstName());		
			tamrPax.setGNDR_CD(passenger.getGender());
			tamrPax.setGtasId(passenger.getId().toString());
			tamrPax.setLast_name(passenger.getLastName());
			
			//temp values
			tamrPax.setUid("");
			tamrPax.setFlt("test");
			
			
			return tamrPax;
		}
		
		private TamrDocumentSendObject convertDocumentToTamrDocument(Document doc){
			TamrDocumentSendObject tamrDoc = new TamrDocumentSendObject();
			
			tamrDoc.setDOC_CTRY_CD(doc.getIssuanceCountry());
			tamrDoc.setDOC_ID(doc.getDocumentNumber());
			tamrDoc.setDOC_TYP_NM(doc.getDocumentType());
			
			return tamrDoc;
		}
		
		private String convertGTASObjectToJson(List<TamrPassengerSendObject> passengers){
			String tamrJSON = "";
			if(passengers != null && !passengers.isEmpty()){
				logger.debug("Converting model object to JSON String");
				logger.debug("Total number of passengers to convert: "+ passengers.size());
				ObjectMapper mapper = new ObjectMapper();
				try {
					tamrJSON = mapper.writeValueAsString(passengers);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
			return tamrJSON;
		}

		@Override
		public void sendPassengersToTamr(Set<Flight> flights, Set<Passenger> passengers) {
			List<TamrPassengerSendObject> tamrPassengers = new ArrayList<TamrPassengerSendObject>();
			for(Flight flight:flights){
				tamrPassengers.addAll(convertGTASFlightAndPassengersToTamrMessage(flight, passengers));
			}
			String tamrJSON = convertGTASObjectToJson(tamrPassengers);
			try {
				//Commented out for commit, uncomment with new Tamr credentials if wanting to use.
				//messageSender.sendMessageToTamr("InboundQueue", tamrJSON);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//System.out.println(tamrJSON);
		}
		
}
