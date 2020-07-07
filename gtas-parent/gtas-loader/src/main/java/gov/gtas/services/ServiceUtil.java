/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.Passenger;
import gov.gtas.parsers.vo.DocumentVo;
import gov.gtas.parsers.vo.PassengerVo;
import gov.gtas.repository.FlightPassengerRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.summary.PassengerDocument;
import gov.gtas.summary.PassengerSummary;
import gov.gtas.util.EntityResolverUtils;
import gov.gtas.vo.lookup.AirportVo;
import gov.gtas.vo.passenger.CountDownVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

		AirportVo a = airportService.getAirportByThreeLetterCode(airport);
		if (a == null) {
			return "USA";
		}
		return a.getCountry();

	}

	public Integer getHoursBeforeTakeOff(Flight primeFlight, Date transmissionDate) {
		Integer hoursBeforeTakeOff = null;
		if (transmissionDate != null && primeFlight.getMutableFlightDetails().getEtd() != null) {
			Date flightDate = primeFlight.getMutableFlightDetails().getEtd();
			CountDownCalculator countDownCalculator = new CountDownCalculator(transmissionDate);
			CountDownVo countDownVo = countDownCalculator.getCountDownFromDate(flightDate);
			if (countDownVo.getMillisecondsFromDate() > 0
					&& countDownVo.getMillisecondsFromDate() <= Integer.MAX_VALUE) {
				hoursBeforeTakeOff = (int) TimeUnit.MILLISECONDS.toHours(countDownVo.getMillisecondsFromDate());
			} else if (countDownVo.getMillisecondsFromDate() >= Integer.MAX_VALUE) {
				logger.error("Hours before take off was difference of *2,147,483,647+* hours! "
						+ "This indicates a likely data issue!");
				hoursBeforeTakeOff = Integer.MAX_VALUE;
			} else {
				hoursBeforeTakeOff = 0;
			}
		}
		return hoursBeforeTakeOff;
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
		} catch (Exception e) {
			logger.error("ERROR! An error occured when trying to create passengerIdTag", e);
		}

		return passengerIdTag;

	}

	private boolean isNull(Object obj) {
		return obj == null;
	}

	// checks if the two passenger shares at least one document.
	private boolean haveSameDocument(Passenger pax, List<DocumentVo> pvoDocuments) {
		Set<Document> paxDocuments = pax.getDocuments();
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

	private boolean haveSameDateOfBirth(Passenger pax, Date pvoDob) {
		Date paxDob = pax.getPassengerDetails().getDob();

		if (isNull(paxDob) || isNull(pvoDob)) {
			return false;
		}

		return paxDob.equals(pvoDob);
	}

	private boolean haveSameGender(Passenger pax, String pvoGender) {
		String paxGender = pax.getPassengerDetails().getGender();

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
		Long flightId = f.getId();
		String firstName = pvo.getFirstName();
		String lastName = pvo.getLastName();
		Date dob = pvo.getDob();
		String gender = pvo.getGender();
		String recordLocator = pvo.getPnrRecordLocator();
		String ref = pvo.getPnrReservationReferenceNumber();
		List<DocumentVo> documentVo = pvo.getDocuments();
		String passengerIdTag = createPassengerIdTag(pvo);
		return getPassenger(flightId, firstName, lastName, dob, gender, recordLocator, ref, documentVo, passengerIdTag);
	}


	@Override
	@Transactional()
	public Optional<Passenger> findPassengerOnFlight(Flight f, PassengerSummary passengerSummary, String recordLocatorNumber) {
		if (isNull(f.getId())) {
			return Optional.empty();
		}
		Long flightId = f.getId();
		String firstName = passengerSummary.getPassengerBiographic().getFirstName();
		String lastName = passengerSummary.getPassengerBiographic().getLastName();
		Date dob = passengerSummary.getPassengerBiographic().getDob();
		String gender = passengerSummary.getPassengerBiographic().getGender();
		String recordLocator = passengerSummary.getPassengerTrip().getPnrReservationReferenceNumber();
		String passengerIdTag = passengerSummary.getPassengerIds().getIdTag();
		String ref = passengerSummary.getPassengerTrip().getPnrReservationReferenceNumber();
		List<DocumentVo> documentVo = convertDocs(passengerSummary.getPassengerDocumentsList());
		return Optional.ofNullable(getPassenger(flightId, firstName, lastName, dob, gender, recordLocator, ref, documentVo, passengerIdTag)) ;

	}


	private List<DocumentVo> convertDocs(List<PassengerDocument> docs) {
		return docs.stream()
				.map(d -> {
					DocumentVo documentVo = new DocumentVo();
					documentVo.setDocumentNumber(d.getDocumentNumber());
					documentVo.setDocumentType(d.getDocumentType());
					return documentVo;
				}).collect(Collectors.toList());
	}

	private Passenger getPassenger(Long flightId, String firstName, String lastName, Date dob, String gender, String recordLocator, String ref, List<DocumentVo> documentVo, String passengerIdTag) {
		Passenger existingPassenger = null;
		Set<Passenger> passengerSet;
		boolean newPaxHasRecordLocator = !isNull(recordLocator);
		boolean newPaxHasRef = !isNull(ref);
		boolean newPaxHasGender = !isNull(gender);
		boolean newPaxHasDob = !isNull(dob);
		boolean newPaxHasDocument = !CollectionUtils.isEmpty(documentVo);

		boolean foundPassenger = false;

		// Resolve passenger by Flight -> pnr recordLocator# -> pnr REF number
		if (newPaxHasRecordLocator && newPaxHasRef) {
			passengerSet = passengerRepository.getPassengerUsingREF(flightId, ref, recordLocator);
			foundPassenger = !CollectionUtils.isEmpty(passengerSet);
			existingPassenger = foundPassenger ? passengerSet.iterator().next() : null;
		}

		passengerSet = passengerRepository.returnAPassengerFromParameters(flightId, firstName, lastName);
		if (!foundPassenger && !CollectionUtils.isEmpty(passengerSet)) {

			// Resolve passenger by passengerIdTag
			if (newPaxHasGender && newPaxHasDob) {
				for (Passenger pax : passengerSet) {
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
					for (Passenger pax : passengerSet) {
						if (haveSameDocument(pax, documentVo) && haveSameDateOfBirth(pax, dob)) {
							existingPassenger = pax;
							foundPassenger = true;
							break;
						}
					}
				}

				// Find passenger by First Name, Last Name, document# and gender
				if (!foundPassenger && newPaxHasDocument && newPaxHasGender) {
					for (Passenger pax : passengerSet) {
						if (haveSameDocument(pax, documentVo) && haveSameGender(pax,gender)) {
							existingPassenger = pax;
							foundPassenger = true;
							break;
						}
					}
				}

				// Find passenger by First Name, Last Name, and document#
				if (!foundPassenger && newPaxHasDocument) {
					for (Passenger pax : passengerSet) {
						if (haveSameDocument(pax, documentVo)) {
							existingPassenger = pax;
							foundPassenger = true;
							break;
						}
					}
				}

				// Find passenger by First Name, Last Name, and dob
				if (!foundPassenger && newPaxHasDob) {
					for (Passenger pax : passengerSet) {
						if (haveSameDateOfBirth(pax, dob)) {
							existingPassenger = pax;
							break;
						}
					}
				}

				// Find passenger by First Name, Last Name, and gender
				if (!foundPassenger && newPaxHasGender) {
					for (Passenger pax : passengerSet) {
						if (haveSameGender(pax, gender) && isNull(pax.getPassengerDetails().getDob())
								&& isNull(pax.getDocuments())) {
							existingPassenger = pax;
							break;
						}
					}
				}

				// Find passenger by First Name and Last Name
				if (!foundPassenger) {
					for (Passenger pax : passengerSet) {
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
