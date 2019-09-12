/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.Flight;
import gov.gtas.model.FlightPassenger;
import gov.gtas.model.Passenger;
import gov.gtas.model.lookup.Airport;

import gov.gtas.parsers.vo.PassengerVo;
import gov.gtas.repository.FlightPassengerRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.util.EntityResolverUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ServiceUtil implements LoaderServices {
	private static final Logger logger = LoggerFactory.getLogger(ServiceUtil.class);

	@Value("${allow.loosen.resolution}")
	private Boolean allowLoosenResolution;

	@Autowired
	private AirportService airportService;

	@Autowired
	PassengerRepository passengerRepository;

	@Autowired
	FlightPassengerRepository flightPassengerRepository;

	public String getCountry(String airport) {

		Airport a = airportService.getAirportByThreeLetterCode(airport);
		if (a == null) {
			return "USA";
		}
		return a.getCountry();

	}

	public AirportService getAirportService() {
		return airportService;
	}

	public void setAirportService(AirportService airportService) {
		this.airportService = airportService;
	}


	private String createPassengerIdTag(PassengerVo pvo) {
		String input = String.join("",Arrays.asList(
				pvo.getFirstName().toUpperCase(), 
				pvo.getLastName().toUpperCase(), 
				pvo.getGender().toUpperCase(), 
				new SimpleDateFormat("MM/dd/yyyy").format(pvo.getDob())));

		String passengerIdTag = null;

		try {
			passengerIdTag = EntityResolverUtils.makeSHA1Hash(input);			
		} catch (NoSuchAlgorithmException e) {
			logger.error("ERROR! An error occured when trying to create passengerIdTag", e);
		} catch (UnsupportedEncodingException e) {
			logger.error("ERROR! An error occured when trying to create passengerIdTag", e);
		}

		return passengerIdTag;

	}

	@Override
	@Transactional()
	public Passenger findPassengerOnFlight(Flight f, PassengerVo pvo) {
		if (f.getId() == null) {
			return null;
		}
		List<FlightPassenger> pax = null;
		Long flightId = f.getId();
		String firstName = pvo.getFirstName();
		String lastName = pvo.getLastName();
		Date dob = pvo.getDob();
		String gender = pvo.getGender();
		String recordLocator = pvo.getPnrRecordLocator();
		String ref = pvo.getPnrReservationReferenceNumber();
		boolean found = false;

		//Resolve passenger by Flight -> pnr recordLocator# -> pnr REF number       
		if (recordLocator != null && ref != null) {
			pax = flightPassengerRepository.getPassengerUsingREF(flightId, ref, recordLocator);	
			found = (pax != null && pax.size() > 0);
		}


		//Resolve passenger by passengerIdTAg
		if (!found && pvo.getGender() != null && pvo.getDob() != null) {			
			String passengerIdTag = createPassengerIdTag(pvo);
			pax = flightPassengerRepository.getPassengerByIdTag(f.getId(), passengerIdTag);
			found = (pax != null && pax.size() > 0);

		}
		if (!found && allowLoosenResolution) {
			//Resolve passenger by first name, last name and dob
			if (pvo.getDob() != null) {
				pax = flightPassengerRepository
						.getPassengerByFirstNameLastNameAndDOB(flightId, firstName, lastName, dob);
				found = (pax != null && pax.size() > 0);
			}

			//Resolve passenger by first name, last name, and gender
			if (!found && pvo.getGender() != null) {
				pax = flightPassengerRepository
						.getPassengerByFirstNameLastNameAndGender(flightId, firstName, lastName, gender);
				found = (pax != null && pax.size() > 0);
			}

			//Resolve by first name and last name
			if(!found){
				pax = flightPassengerRepository
						.returnAPassengerFromParameters(flightId, firstName, lastName);
				found = (pax != null && pax.size() > 0);
			}
		}

		if (found) {
			return pax.get(0).getPassenger();
		} else {
			return null;
		}
	}
}

