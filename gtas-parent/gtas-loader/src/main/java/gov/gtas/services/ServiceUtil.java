/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.FlightPassenger;
import gov.gtas.model.Passenger;
import gov.gtas.model.lookup.Airport;
import gov.gtas.parsers.vo.DocumentVo;
import gov.gtas.parsers.vo.PassengerVo;
import gov.gtas.repository.FlightPassengerRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.util.EntityResolverUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

		String passengerIdTag = null;

		try {
			passengerIdTag = EntityResolverUtils.makeHashForPassenger(pvo.getFirstName(), pvo.getLastName(),
					pvo.getGender(), pvo.getDob());
		} catch (NoSuchAlgorithmException e) {
			logger.error("ERROR! An error occured when trying to create passengerIdTag", e);
		} catch (UnsupportedEncodingException e) {
			logger.error("ERROR! An error occured when trying to create passengerIdTag", e);
		}

		return passengerIdTag;

	}

	private boolean isNull(Object obj) {
		return obj == null;
	}

	// checks if the two passenger shares at least one document.
	private boolean haveSameDocument(Passenger pax, PassengerVo pvo) {
		Set<Document> paxDocuments = pax.getDocuments();
		List<DocumentVo> pvoDocuments = pvo.getDocuments();
		boolean foundSharedDocument = false;

		if (!isNull(paxDocuments) && !isNull(pvoDocuments)) {
			for (Document paxDoc : paxDocuments) {
				for (DocumentVo pvoDoc : pvoDocuments) {
					String paxDocType = paxDoc.getDocumentType();
					String paxDocNumber = paxDoc.getDocumentNumber();
					String pvoDocType = pvoDoc.getDocumentType();
					String pvoDocNumber = pvoDoc.getDocumentNumber();

					if (paxDocType.equals(pvoDocType) && paxDocNumber.equals(pvoDocNumber)) {
						foundSharedDocument = true;
					}
				}
			}
		}
		return foundSharedDocument;
	}

	private boolean haveSameDateOfBirth(Passenger pax, PassengerVo pvo) {
		Date paxDob = pax.getPassengerDetails().getDob();
		Date pvoDob = pvo.getDob();

		if (isNull(paxDob) || isNull(pvoDob)) {
			return false;
		}

		return paxDob.equals(pvoDob);
	}

	private boolean haveSameGender(Passenger pax, PassengerVo pvo) {
		String paxGender = pax.getPassengerDetails().getGender();
		String pvoGender = pvo.getGender();

		if (isNull(paxGender) || isNull(pvoGender)) {
			return false;
		}
		return paxGender.equals(pvoGender);
	}

	/**
	 * Resolve passengers as follows (we assume first name and last name always
	 * exist)
	 * 
	 * - if 'allowLoosenResolution' set to false, we resolve only by pnr REF number
	 * and pnr Record Locator number and passengerIdTag.
	 * 
	 * - if the incoming passenger has pnr REF number and pnr Record Locator number,
	 * resolve by these two fields.
	 * 
	 * - if no passenger found and the incoming passenger has passengerIdTag,
	 * resolve by passengerIdTag. (this is equivalent to resolving by first name,
	 * last name, dob, and gender)
	 * 
	 * - if 'allowLoosenResolution' set to true, continue resolving in the following
	 * order; we stop when we resolve to a passenger.
	 * 
	 * - if the incoming passenger has doc number and dob, resolve by first name,
	 * last name, doc number, and dob.
	 * 
	 * - if the incoming passenger has doc number and gender, resolve by first name,
	 * last name, doc number, and gender.
	 * 
	 * - if the incoming passenger has only doc number, resolve by first name, last
	 * name, and doc number.
	 * 
	 * - if the incoming passenger has only dob, resolve by first name, last name,
	 * and dob,
	 * 
	 * - if the incoming passenger has only gender, resolve by first name, last
	 * name, and gender
	 * 
	 * - finally we resolve by first name and last name. (To resolve by first name
	 * and last name, the existing passenger should have null value for dob, gender,
	 * and document in order not to overwrite data.)
	 * 
	 * 
	 */

	@Override
	@Transactional()
	public Passenger findPassengerOnFlight(Flight f, PassengerVo pvo) {
		if (isNull(f.getId())) {
			return null;
		}

		Set<Passenger> flightPaxList;
		Passenger existingPassenger = null;

		Long flightId = f.getId();
		String firstName = pvo.getFirstName();
		String lastName = pvo.getLastName();
		Date dob = pvo.getDob();
		String gender = pvo.getGender();
		String recordLocator = pvo.getPnrRecordLocator();
		String ref = pvo.getPnrReservationReferenceNumber();
		List<DocumentVo> documentVo = pvo.getDocuments();

		boolean newPaxHasRecordLocator = !isNull(recordLocator);
		boolean newPaxHasRef = !isNull(ref);
		boolean newPaxHasGender = !isNull(gender);
		boolean newPaxHasDob = !isNull(dob);
		boolean newPaxHasDocument = !CollectionUtils.isEmpty(documentVo);

		boolean foundPassenger = false;

		// Resolve passenger by Flight -> pnr recordLocator# -> pnr REF number
		if (newPaxHasRecordLocator && newPaxHasRef) {
			flightPaxList = passengerRepository.getPassengerUsingREF(flightId, ref, recordLocator);
			foundPassenger = !CollectionUtils.isEmpty(flightPaxList);
			existingPassenger = foundPassenger ? flightPaxList.iterator().next() : null;
		}

		flightPaxList = passengerRepository.returnAPassengerFromParameters(flightId, firstName, lastName);
		if (!foundPassenger && !CollectionUtils.isEmpty(flightPaxList)) {

			// Resolve passenger by passengerIdTag
			if (newPaxHasGender && newPaxHasDob) {
				String passengerIdTag = createPassengerIdTag(pvo);
				for (Passenger pax : flightPaxList) {
					if (pax.getPassengerIDTag() != null) {
						String paxIdTag = pax.getPassengerIDTag().getIdTag();
						if (paxIdTag.equals(passengerIdTag)) {
							existingPassenger = pax;
							foundPassenger = true;
						}
					}
				}
			}

			// Loosen resolution
			if (!foundPassenger && allowLoosenResolution) {
				// Find passenger by First Name, Last Name, document# and dob
				if (newPaxHasDocument && newPaxHasDob) {
					for (Passenger pax : flightPaxList) {
						if (haveSameDocument(pax, pvo) && haveSameDateOfBirth(pax, pvo)) {
							existingPassenger = pax;
							foundPassenger = true;
							break;
						}
					}
				}

				// Find passenger by First Name, Last Name, document# and gender
				if (!foundPassenger && newPaxHasDocument && newPaxHasGender) {
					for (Passenger pax : flightPaxList) {
						if (haveSameDocument(pax, pvo) && haveSameGender(pax, pvo)) {
							existingPassenger = pax;
							foundPassenger = true;
							break;
						}
					}
				}

				// Find passenger by First Name, Last Name, and document#
				if (!foundPassenger && newPaxHasDocument) {
					for (Passenger pax : flightPaxList) {
						if (haveSameDocument(pax, pvo)) {
							existingPassenger = pax;
							foundPassenger = true;
							break;
						}
					}
				}

				// Find passenger by First Name, Last Name, and dob
				if (!foundPassenger && newPaxHasDob) {
					for (Passenger pax : flightPaxList) {
						if (haveSameDateOfBirth(pax, pvo)) {
							existingPassenger = pax;
							break;
						}
					}
				}

				// Find passenger by First Name, Last Name, and gender
				if (!foundPassenger && newPaxHasGender) {
					for (Passenger pax : flightPaxList) {
						if (haveSameGender(pax, pvo) && isNull(pax.getPassengerDetails().getDob())
								&& isNull(pax.getDocuments())) {
							existingPassenger = pax;
							break;
						}
					}
				}

				// Find passenger by First Name and Last Name
				if (!foundPassenger) {
					for (Passenger pax : flightPaxList) {
						if (isNull(pax.getPassengerDetails().getDob()) && isNull(pax.getPassengerDetails().getGender())
								&& isNull(pax.getDocuments())) {
							existingPassenger = pax;

						}
					}
				}
			}

		}

		return existingPassenger;
	}
}
