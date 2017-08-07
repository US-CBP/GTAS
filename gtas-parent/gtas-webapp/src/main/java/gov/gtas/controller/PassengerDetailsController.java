/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.base.Strings;

import gov.gtas.enumtype.Status;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.Address;
import gov.gtas.model.Agency;
import gov.gtas.model.ApisMessage;
import gov.gtas.model.Bag;
import gov.gtas.model.CreditCard;
import gov.gtas.model.Disposition;
import gov.gtas.model.Document;
import gov.gtas.model.Email;
import gov.gtas.model.Flight;
import gov.gtas.model.FlightLeg;
import gov.gtas.model.FrequentFlyer;
import gov.gtas.model.Passenger;
import gov.gtas.model.Phone;
import gov.gtas.model.Pnr;
import gov.gtas.model.Seat;
import gov.gtas.model.lookup.DispositionStatus;
import gov.gtas.repository.ApisMessageRepository;
import gov.gtas.repository.BagRepository;
import gov.gtas.repository.SeatRepository;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.DispositionData;
import gov.gtas.services.FlightService;
import gov.gtas.services.PassengerService;
import gov.gtas.services.PnrService;
import gov.gtas.services.security.RoleData;
import gov.gtas.services.security.UserService;
import gov.gtas.util.DateCalendarUtils;
import gov.gtas.util.LobUtils;
import gov.gtas.vo.BaseVo;
import gov.gtas.vo.passenger.AddressVo;
import gov.gtas.vo.passenger.AgencyVo;
import gov.gtas.vo.passenger.ApisMessageVo;
import gov.gtas.vo.passenger.BagVo;
import gov.gtas.vo.passenger.CaseVo;
import gov.gtas.vo.passenger.CreditCardVo;
import gov.gtas.vo.passenger.DispositionVo;
import gov.gtas.vo.passenger.DocumentVo;
import gov.gtas.vo.passenger.EmailVo;
import gov.gtas.vo.passenger.FlightHistoryVo;
import gov.gtas.vo.passenger.FlightLegVo;
import gov.gtas.vo.passenger.FlightVo;
import gov.gtas.vo.passenger.FrequentFlyerVo;
import gov.gtas.vo.passenger.PassengerVo;
import gov.gtas.vo.passenger.PhoneVo;
import gov.gtas.vo.passenger.PnrVo;
import gov.gtas.vo.passenger.SeatVo;

@Controller
public class PassengerDetailsController {

	@Autowired
	private PassengerService pService;

	@Autowired
	private FlightService fService;

	@Autowired
	private PnrService pnrService;

	@Autowired
	private UserService uService;
	
	@Resource
	private BagRepository bagRepository;
	
	@Resource
	private SeatRepository seatRepository;
	
	@Resource
	private ApisMessageRepository apisMessageRepository;

	static final String EMPTY_STRING = "";

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/passengers/passenger/{id}/details", method = RequestMethod.GET)
	public PassengerVo getPassengerByPaxIdAndFlightId(
			@PathVariable(value = "id") String paxId,
			@RequestParam(value = "flightId", required = false) String flightId) {
		PassengerVo vo = new PassengerVo();
		Passenger t = pService.findById(Long.valueOf(paxId));
		Flight flight = fService.findById(Long.parseLong(flightId));

		if (flightId != null && flight.getId().toString().equals(flightId)) {
			vo.setFlightNumber(flight.getFlightNumber());
			vo.setCarrier(flight.getCarrier());
			vo.setFlightOrigin(flight.getOrigin());
			vo.setFlightDestination(flight.getDestination());
			vo.setFlightETA((flight.getEta() != null) ? DateCalendarUtils
					.formatJsonDateTime(flight.getEta()) : EMPTY_STRING);
			vo.setFlightETD((flight.getEtd() != null) ? DateCalendarUtils
					.formatJsonDateTime(flight.getEtd()) : EMPTY_STRING);
			vo.setFlightId(flight.getId().toString());
			List<Seat> seatList = seatRepository.findByFlightIdAndPassengerId(
					flight.getId(), t.getId());
			if (CollectionUtils.isNotEmpty(seatList)) {			
				List<String> seats = seatList.stream().map(seat -> seat.getNumber())
						.distinct().collect(Collectors.toList());
				if (seats.size() == 1) {
					vo.setSeat(seats.get(0));
				}
			}
		}
		vo.setPaxId(String.valueOf(t.getId()));
		vo.setPassengerType(t.getPassengerType());
		vo.setLastName(t.getLastName());
		vo.setFirstName(t.getFirstName());
		vo.setMiddleName(t.getMiddleName());
		vo.setCitizenshipCountry(t.getCitizenshipCountry());
		vo.setDebarkation(t.getDebarkation());
		vo.setDebarkCountry(t.getDebarkCountry());
		vo.setDob(t.getDob());
		vo.setEmbarkation(t.getEmbarkation());
		vo.setEmbarkCountry(t.getEmbarkCountry());
		vo.setGender(t.getGender() != null ? t.getGender() : "");
		vo.setResidencyCountry(t.getResidencyCountry());
		vo.setSuffix(t.getSuffix());
		vo.setTitle(t.getTitle());

		Iterator<Document> docIter = t.getDocuments().iterator();
		while (docIter.hasNext()) {
			Document d = docIter.next();
			DocumentVo docVo = new DocumentVo();
			docVo.setDocumentNumber(d.getDocumentNumber());
			docVo.setDocumentType(d.getDocumentType());
			docVo.setIssuanceCountry(d.getIssuanceCountry());
			docVo.setExpirationDate(d.getExpirationDate());
			docVo.setIssuanceDate(d.getIssuanceDate());
			vo.addDocument(docVo);
		}


		List<Disposition> cases = pService.getPassengerDispositionHistory(Long.valueOf(paxId),
				Long.parseLong(flightId));
		if (CollectionUtils.isNotEmpty(cases)) {
			List<DispositionVo> history = new ArrayList<>();
			for (Disposition d : cases) {
				DispositionVo dvo = new DispositionVo();
				dvo.setComments(d.getComments());
				dvo.setCreatedAt(d.getCreatedAt());
				dvo.setStatus(d.getStatus().getName());
				dvo.setCreatedBy(d.getCreatedBy());
				dvo.setStatusId(d.getStatus().getId());
				history.add(dvo);
			}
			vo.setDispositionHistory(history);
		}

		// Gather PNR Details
		List<Pnr> pnrList = pnrService.findPnrByPassengerIdAndFlightId(
				t.getId(), new Long(flightId));

		if (!pnrList.isEmpty()) {
			vo.setPnrVo(mapPnrToPnrVo(pnrList.get(0)));
			
			PnrVo tempVo = vo.getPnrVo();			
			Iterator<Bag> bagIter = t.getBags().iterator();
			while (bagIter.hasNext()) {
				Bag b = bagIter.next();
				if(b.getData_source().equalsIgnoreCase("pnr")){
					BagVo bagVo = new BagVo();
					bagVo.setBagId(b.getBagId());
					bagVo.setData_source(b.getData_source());
					bagVo.setDestination(b.getDestination());
					tempVo.addBag(bagVo);
				}
			}
			vo.setPnrVo(tempVo);
		}
		
		List<ApisMessage> apisList = apisMessageRepository.findByFlightIdAndPassengerId(new Long(flightId), t.getId());
		if(!apisList.isEmpty()) {
			ApisMessage apis = apisList.get(0);
			ApisMessageVo apisVo = new ApisMessageVo();
			apisVo.setApisRecordExists(true);
			apisVo.setDebarkation(apis.getDebarkation());
			apisVo.setEmbarkation(apis.getEmbarkation());
			apisVo.setTravelerType(apis.getTravelerType());
			apisVo.setPortOfFirstArrival(apis.getPortOfFirstArrival());
			apisVo.setResidenceCountry(apis.getResidenceCountry());
			apisVo.setTransmissionDate(apis.getEdifactMessage().getTransmissionDate());
			
			Iterator<Bag> bagIter = t.getBags().iterator();
			while (bagIter.hasNext()) {
				Bag b = bagIter.next();
				if(b.getData_source().equalsIgnoreCase("apis")){
					BagVo bagVo = new BagVo();
					bagVo.setBagId(b.getBagId());
					bagVo.setData_source(b.getData_source());
					bagVo.setDestination(b.getDestination());
					apisVo.addBag(bagVo);
				}
			}
			
			Iterator<Phone> phoneIter = apis.getPhones().iterator();
			while(phoneIter.hasNext()) {
				Phone p = phoneIter.next();
				PhoneVo pVo = new PhoneVo();
				pVo.setNumber(p.getNumber());
				apisVo.addPhoneNumber(pVo);
			}
			if(apis.getInstallationAddress() != null){
				AddressVo add = new AddressVo();
				Address installAdd = apis.getInstallationAddress();
				add.setLine1(installAdd.getLine1());
				add.setLine2(installAdd.getLine2());
				add.setLine3(installAdd.getLine3());
				add.setCity(installAdd.getCity());
				add.setCountry(installAdd.getCountry());
				add.setPostalCode(installAdd.getPostalCode());
				add.setState(installAdd.getState());
				apisVo.setInstallationAddress(add);
				vo.setApisMessageVo(apisVo);				
			}

		}
		return vo;
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/passengers/passenger/flighthistory", method = RequestMethod.GET)
	public FlightHistoryVo getFlightHistoryByPassengerAndDocuments(
			@RequestParam String paxId) {

		FlightHistoryVo flightHistoryVo = new FlightHistoryVo();
		List<FlightVo> flightVoList = new ArrayList<>();
		Passenger t = pService.findById(Long.valueOf(paxId));

		// Gather Flight History Details
		Map<Document, List<Flight>> flightHistoryMap = fService.getFlightsByPassengerNameAndDocument(
				t.getFirstName(), t.getLastName(), t.getDocuments());

		for (Document document : flightHistoryMap.keySet()) {
			for (Document doc : t.getDocuments()) {
				if ((document.getDocumentNumber() != null)
						&& (document.getDocumentNumber().equals(
								doc.getDocumentNumber()) && (document
								.getDocumentType().equalsIgnoreCase(doc
								.getDocumentType())))) {
					flightVoList.clear();
					for (Flight flight : flightHistoryMap.get(document)) {
						FlightVo _tempFlightVo = new FlightVo();
						copyModelToVo(flight, _tempFlightVo);
						flightVoList.add(_tempFlightVo);
					}
					flightHistoryVo.getFlightHistoryMap().put(
							doc.getDocumentNumber(), flightVoList);
				}
			}
		}

		return flightHistoryVo;
	}

	/**
	 * Gets the travel history by passenger and document.
	 *
	 * @param paxId
	 *            the passenger id
	 * @param docNum
	 *            the doc num
	 * @param docIssuCountry
	 *            the doc issu country
	 * @param docExpiration
	 *            the doc expiration
	 * @return the travel history by passenger and document
	 * @throws ParseException
	 */
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/passengers/passenger/travelhistory", method = RequestMethod.GET)
	public List<FlightVo> getTravelHistoryByPassengerAndDocument(
			@RequestParam String paxId,
			@RequestParam String docNum,
			@RequestParam String docIssuCountry,
			@RequestParam String docExpiration)
			throws ParseException {
		Date docExpDate = null;
		if (!Strings.isNullOrEmpty(docExpiration)) {
			docExpDate = DateCalendarUtils.parseJsonDate(docExpiration);
		}
		return pService
				.getTravelHistory(Long.valueOf(paxId), docNum, docIssuCountry,
						docExpDate).stream().map(flight -> {
					FlightVo flightVo = new FlightVo();
					copyModelToVo(flight, flightVo);
					return flightVo;
				}).collect(Collectors.toCollection(LinkedList::new));
	}
	
	@RequestMapping(value = "/dispositionstatuses", method = RequestMethod.GET)
	public @ResponseBody List<DispositionStatus> getDispositionStatuses() {
		return pService.getDispositionStatuses();
	}

	@RequestMapping(value = "/disposition", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JsonServiceResponse createDisposition(
			@RequestBody DispositionData disposition) {
		JsonServiceResponse response = checkIfValidCaseStatusAction(disposition);
		if (response.getStatus().equals(Status.SUCCESS)) {
			String userId = GtasSecurityUtils.fetchLoggedInUserId();
			pService.createDisposition(disposition, uService.fetchUser(userId));
		}
		return response;
	}

	@RequestMapping(value = "/allcases", method = RequestMethod.GET)
	public @ResponseBody List<CaseVo> getAllDispositions() {
		return pService.getAllDispositions();
	}

	@RequestMapping(value = "/createoreditdispstatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JsonServiceResponse createOrEditDispositionStatus(
			@RequestBody DispositionStatus ds) {
		pService.createOrEditDispositionStatus(ds);
		return new JsonServiceResponse(Status.SUCCESS,
				"Creation or Edit of disposition status successful");
	}

	@RequestMapping(value = "/deletedispstatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JsonServiceResponse deleteDispositionStatus(
			@RequestBody DispositionStatus ds) {
		if (!isRemovableDispositionStatus(ds)) {
			return new JsonServiceResponse(Status.FAILURE,
					"This status is irremovable");
		}
		try {
			pService.deleteDispositionStatus(ds);
		} catch (ConstraintViolationException e) {
			return new JsonServiceResponse(Status.FAILURE,
					"Case already exists with " + ds.getName()
							+ ". You may not remove this status");
		}
		return new JsonServiceResponse(Status.SUCCESS,
				"Deletion of disposition status successful");
	}

	/**
	 * Util method to map PNR model object to VO
	 * 
	 * @param source
	 * @return
	 */
	public PnrVo mapPnrToPnrVo(Pnr source) {
		PnrVo target = new PnrVo();

		if (source.getRecordLocator() == null
				|| source.getRecordLocator().isEmpty()) {
			target.setPnrRecordExists(false);
			return target;
		}
		target.setPnrRecordExists(true);
		target.setRecordLocator(source.getRecordLocator());
		target.setBagCount(source.getBagCount());
		target.setDateBooked(source.getDateBooked());
		target.setCarrier(source.getCarrier());
		target.setDaysBookedBeforeTravel(source.getDaysBookedBeforeTravel());
		target.setDepartureDate(source.getDepartureDate());
		target.setFormOfPayment(source.getFormOfPayment());
		target.setOrigin(source.getOrigin());
		target.setOriginCountry(source.getOriginCountry());
		target.setPassengerCount(source.getPassengerCount());
		target.setDateReceived(source.getDateReceived());
		target.setRaw(LobUtils.convertClobToString(source.getRaw()));
		target.setTransmissionDate(source.getEdifactMessage().getTransmissionDate());
		target.setTotalbagCount(source.getTotal_bag_count());
		target.setTotalbagWeight(source.getTotal_bag_weight());
		parseRawMessageToList(target);


		if (!source.getAddresses().isEmpty()) {
			Iterator it = source.getAddresses().iterator();
			while (it.hasNext()) {
				Address a = (Address) it.next();
				AddressVo aVo = new AddressVo();

				try {

					BeanUtils.copyProperties(aVo, a);

				} catch (IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				} 

				target.getAddresses().add(aVo);

			} // End of While Loop

		}

		if (CollectionUtils.isNotEmpty(source.getAgencies())) {
			AgencyVo aVo = new AgencyVo();
			for (Agency agency : source.getAgencies()) {
				copyModelToVo(agency, aVo);
				target.getAgencies().add(aVo);
			}
		}

		if (!source.getCreditCards().isEmpty()) {
			Iterator<CreditCard> it1 = source.getCreditCards().iterator();
			while (it1.hasNext()) {
				CreditCard cc = it1.next();
				CreditCardVo cVo = new CreditCardVo();
				copyModelToVo(cc, cVo);
				target.getCreditCards().add(cVo);
			}
		}
		if (!source.getFrequentFlyers().isEmpty()) {
			Iterator<FrequentFlyer> it2 = source.getFrequentFlyers().iterator();
			while (it2.hasNext()) {
				FrequentFlyer ff = it2.next();
				FrequentFlyerVo fVo = new FrequentFlyerVo();
				copyModelToVo(ff, fVo);
				target.getFrequentFlyerDetails().add(fVo);
			}
		}

		if (!source.getEmails().isEmpty()) {
			Iterator<Email> it3 = source.getEmails().iterator();
			while (it3.hasNext()) {
				Email e = it3.next();
				EmailVo eVo = new EmailVo();
				copyModelToVo(e, eVo);
				target.getEmails().add(eVo);
			}
		}

		if (!source.getPhones().isEmpty()) {
			Iterator<Phone> it4 = source.getPhones().iterator();
			while (it4.hasNext()) {
				Phone p = it4.next();
				PhoneVo pVo = new PhoneVo();
				copyModelToVo(p, pVo);
				target.getPhoneNumbers().add(pVo);
			}
		}

		if (!source.getFlightLegs().isEmpty()) {
			List<FlightLeg> _tempFL = source.getFlightLegs();
			for (FlightLeg fl : _tempFL) {
				FlightLegVo flVo = new FlightLegVo();
				flVo.setLegNumber(fl.getLegNumber().toString());
				flVo.setFlightNumber(fl.getFlight().getFullFlightNumber());
				flVo.setOriginAirport(fl.getFlight().getOrigin());
				flVo.setDestinationAirport(fl.getFlight().getDestination());
				flVo.setFlightDate(fl.getFlight().getFlightDate().toString());
				flVo.setEtd(DateCalendarUtils.formatJsonDateTime(fl.getFlight().getEtd()));
				flVo.setEta(DateCalendarUtils.formatJsonDateTime(fl.getFlight().getEta()));
				target.getFlightLegs().add(flVo);
			}
		}

		if (!source.getPassengers().isEmpty()) {
			Iterator it4 = source.getPassengers().iterator();
			while (it4.hasNext()) {
				Passenger p = (Passenger) it4.next();
				PassengerVo pVo = new PassengerVo();
				pVo.setLastName(p.getLastName());
				pVo.setFirstName(p.getFirstName());
				pVo.setMiddleName(p.getMiddleName());
				pVo.setAge(p.getAge());
				pVo.setGender(p.getGender());
				target.getPassengers().add(pVo);

				Set<Seat> seats = p.getSeatAssignments();
				for (Seat s : seats) {
					if (!s.getApis()) {
						SeatVo seatVo = new SeatVo();
						seatVo.setFirstName(s.getPassenger().getFirstName());
						seatVo.setLastName(s.getPassenger().getLastName());
						seatVo.setNumber(s.getNumber());
						seatVo.setFlightNumber(s.getFlight()
								.getFullFlightNumber());
						target.getSeatAssignments().add(seatVo);
					}
				}
			}
		}

		return target;
	}

	/**
	 * Util Method To Parse PNR Raw Format Message to List For The Front End
	 * 
	 * @param targetVo
	 */
	private void parseRawMessageToList(PnrVo targetVo) {

		if (targetVo != null && targetVo.getRaw() != null) {
			StringTokenizer _tempStr = new StringTokenizer(targetVo.getRaw(),
					"\n");
			ArrayList<String> _tempList = new ArrayList<>();
			while (_tempStr.hasMoreTokens()) {
				_tempList.add(_tempStr.nextToken());
			}
			targetVo.setRawList(_tempList);
		}
	}

	/**
	 * 
	 * @param source
	 * @param target
	 */
	private void copyModelToVo(Object source, Object target) {

		try {
			BeanUtils.copyProperties(target, source);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		} 
	}

	private boolean isRemovableDispositionStatus(DispositionStatus ds) {
		// Prevent deletion of any of the original disp status ids (New, Closed,
		// Open, Re-opened, Pending Closure)
		if (ds.getId() <= 5L) {
			return false;
		}
		return true;
	}

	private JsonServiceResponse checkIfValidCaseStatusAction(
			DispositionData disposition) {
		DispositionStatus currentDispStatus = getCurrentDispositionStatus(disposition);

		if (currentDispStatus == null) { // if no history
			// Conditions: Action cannot close
			if (disposition.getStatusId().equals(3L)) {
				return new JsonServiceResponse(Status.FAILURE,
						"Current Status: Unknown. May not be set to CLOSE");
			}
			return new JsonServiceResponse(Status.SUCCESS,
					"Successful status change from Unknown");
		}

		if (currentDispStatus.getId().equals(1L)) { // if existing status is
													// New...
			// Conditions: Action can NOT be re-open, new, or closed
			if (disposition.getStatusId().equals(1L)
					|| disposition.getStatusId().equals(3L)
					|| disposition.getStatusId().equals(4L)) {
				return new JsonServiceResponse(Status.FAILURE,
						"Current Status: NEW. Cannot be set to RE-OPEN or CLOSED");
			} else {
				return new JsonServiceResponse(Status.SUCCESS,
						"Successful state change from NEW");
			}
		}

		if (currentDispStatus.getId().equals(2L)) { // If existing status is
													// Open...
			// Conditions: Action can NOT be set to re-open or closed.
			if (disposition.getStatusId().equals(2L)
					|| disposition.getStatusId().equals(3L)) {
				return new JsonServiceResponse(Status.FAILURE,
						"Current status: OPEN. Cannot be set to CLOSED or RE-OPEN");
			} else {
				return new JsonServiceResponse(Status.SUCCESS,
						"Successful status change from OPEN");
			}
		}

		if (currentDispStatus.getId().equals(3L)) {// If existing status is
													// Closed
			// Conditions: Action may only be set to 'Re-open' AND be admin
			boolean isAdmin = false;
			if (disposition.getStatusId().equals(4L)) {// If is re-open
				for (RoleData r : uService.findById(disposition.getUser())
						.getRoles()) { // determine if current user is Admin
					if (r.getRoleId() == 1) {
						isAdmin = true;
					}
				}
				if (isAdmin) {
					return new JsonServiceResponse(Status.SUCCESS,
							"Successful status change from CLOSED");
				} else {
					return new JsonServiceResponse(Status.FAILURE,
							"Current status: CLOSED. Only administrators may RE-OPEN a CLOSED status");
				}
			} else {
				return new JsonServiceResponse(Status.FAILURE,
						"Current status: CLOSED. May only be set to RE-OPEN.");
			}
		}

		if (currentDispStatus.getId().equals(4L)) {// If existing status is
													// Re-open
			// Conditions: Action may not be set to Open, New or Closed
			if (disposition.getStatusId().equals(1L)
					|| disposition.getStatusId().equals(2L)
					|| disposition.getStatusId().equals(3L)) {
				return new JsonServiceResponse(Status.FAILURE,
						"Current status: RE-OPEN. Cannot be set to OPEN or CLOSED");
			} else {
				return new JsonServiceResponse(Status.SUCCESS,
						"Successful status change from RE-OPEN");
			}
		}

		if (currentDispStatus.getId().equals(5L)) {// If existing status is
													// Pending Closure
			// Conditions: Action may not be set to Open or New
			if (disposition.getStatusId().equals(1L)
					|| disposition.getStatusId().equals(2L)) {
				return new JsonServiceResponse(Status.FAILURE,
						"Current status: PENDING CLOSURE. Cannot be set to OPEN");
			} else {
				return new JsonServiceResponse(Status.SUCCESS,
						"Successful status change from PENDING CLOSURE");
			}
		}
		// If the current status is not of the base 5, then we have no hard rule
		// set for them other than do not set to close or new
		if (currentDispStatus.getId() > 5L) {
			if (disposition.getStatusId().equals(1L)
					|| disposition.getStatusId().equals(3L)) {
				return new JsonServiceResponse(Status.FAILURE,
						"Current status: Custom. Cannot be set to NEW or CLOSED");
			} else {
				return new JsonServiceResponse(Status.SUCCESS,
						"Successful status change from custom status");
			}
		}

		// If the current id of the disposition is < 1 there is a problem with
		// the database.
		return new JsonServiceResponse(Status.FAILURE, "Current status is "
				+ currentDispStatus.getId()
				+ " and breaks expected conventions");

	}

	private DispositionStatus getCurrentDispositionStatus(
			DispositionData disposition) {
		List<Disposition> dispList = pService.getPassengerDispositionHistory(
				disposition.getPassengerId(), disposition.getFlightId());
		Disposition mostRecentDisposition = null;

		for (Disposition d : dispList) {
			if (mostRecentDisposition == null
					|| mostRecentDisposition.getCreatedAt().before(
							d.getCreatedAt())) {
				mostRecentDisposition = d;
			}
		}
		if (mostRecentDisposition != null) {
			return mostRecentDisposition.getStatus();
		} else {
			return null;
		}
	}
}
