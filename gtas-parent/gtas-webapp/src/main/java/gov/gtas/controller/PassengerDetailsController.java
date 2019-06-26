/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import gov.gtas.model.*;
import gov.gtas.services.*;
import gov.gtas.vo.passenger.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
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

import gov.gtas.enumtype.Status;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.json.KeyValue;
import gov.gtas.model.lookup.DispositionStatus;
import gov.gtas.repository.ApisMessageRepository;
import gov.gtas.repository.BagRepository;
import gov.gtas.repository.SeatRepository;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.matcher.MatchingService;
import gov.gtas.services.matching.PaxWatchlistLinkVo;
import gov.gtas.services.search.FlightPassengerVo;
import gov.gtas.services.security.RoleData;
import gov.gtas.services.security.UserService;
import gov.gtas.util.DateCalendarUtils;
import gov.gtas.util.LobUtils;

import static java.util.stream.Collectors.*;

@Controller
public class PassengerDetailsController {

	private static final Logger logger = LoggerFactory.getLogger(PassengerDetailsController.class);

	@Autowired
	private ApisControllerService apisControllerService;

	@Autowired
	private PassengerService pService;

	@Autowired
	private FlightService fService;

	@Autowired
	private PnrService pnrService;

	@Autowired
	private UserService uService;
	
	@Autowired
	private MatchingService matchingService;

	@Resource
	private BagRepository bagRepository;
	
	@Resource
	private SeatRepository seatRepository;
	
	@Resource
	private ApisMessageRepository apisMessageRepository;

	@Autowired
	private UserService userService;

	static final String EMPTY_STRING = "";

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/passengers/passenger/{id}/details", method = RequestMethod.GET)
	public PassengerVo getPassengerByPaxIdAndFlightId(
			@PathVariable(value = "id") String paxId,
			@RequestParam(value = "flightId", required = false) String flightId) {
		PassengerVo vo = new PassengerVo();
		Passenger t = pService.findByIdWithFlightPaxAndDocuments(Long.valueOf(paxId));
		Flight flight = fService.findById(Long.parseLong(flightId));
		List<Bag> bagList = new ArrayList<>();
		if (flightId != null && flight.getId().toString().equals(flightId)) {
			vo.setFlightNumber(flight.getFlightNumber());
			vo.setCarrier(flight.getCarrier());
			vo.setFlightOrigin(flight.getOrigin());
			vo.setFlightDestination(flight.getDestination());
			vo.setFlightETA((flight.getMutableFlightDetails().getEta() != null) ? DateCalendarUtils
					.formatJsonDateTime(flight.getMutableFlightDetails().getEta()) : EMPTY_STRING);
			vo.setFlightETD((flight.getMutableFlightDetails().getEtd() != null) ? DateCalendarUtils
					.formatJsonDateTime(flight.getMutableFlightDetails().getEtd()) : EMPTY_STRING);
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
			bagList = new ArrayList<>(bagRepository.findFromFlightAndPassenger(flight.getId(), t.getId()));
		}
		vo.setPaxId(String.valueOf(t.getId()));
		vo.setPassengerType(t.getPassengerDetails().getPassengerType());
		vo.setLastName(t.getPassengerDetails().getLastName());
		vo.setFirstName(t.getPassengerDetails().getFirstName());
		vo.setMiddleName(t.getPassengerDetails().getMiddleName());
		vo.setNationality(t.getPassengerDetails().getNationality());
		vo.setDebarkation(t.getPassengerTripDetails().getDebarkation());
		vo.setDebarkCountry(t.getPassengerTripDetails().getDebarkCountry());
		vo.setDob(t.getPassengerDetails().getDob());
		vo.setAge(t.getPassengerDetails().getAge());
		vo.setEmbarkation(t.getPassengerTripDetails().getEmbarkation());
		vo.setEmbarkCountry(t.getPassengerTripDetails().getEmbarkCountry());
		vo.setGender(t.getPassengerDetails().getGender() != null ? t.getPassengerDetails().getGender() : "");
		vo.setResidencyCountry(t.getPassengerDetails().getResidencyCountry());
		vo.setSuffix(t.getPassengerDetails().getSuffix());
		vo.setTitle(t.getPassengerDetails().getTitle());

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
      //APB - why are we not getting the passengerIds from the latest PNR??
			List<Long> passengerIds = pnrList.get(0).getPassengers().stream().map(Passenger::getId).collect(toList());
			Set<Bag> pnrBag = bagRepository.getBagsByPassengerIds(passengerIds);

      // APB - Here we are using "getLatestPnrFromList" to choose which element from pnrList to use,
      // so why use pnrList.get(0) for the pax data ???
			Pnr source=getLatestPnrFromList(pnrList);
			vo.setPnrVo(mapPnrToPnrVo(source));			
			PnrVo tempVo = vo.getPnrVo();
			BagSummaryVo bagSummaryVo = BagSummaryVo.createFromFlightAndBookingDetails(pnrBag);
			tempVo.setBagSummaryVo(bagSummaryVo);

			//Assign seat for every passenger on pnr
			for(Passenger p: pnrList.get(0).getPassengers()) {
				FlightPax flightPax = getPnrFlightPax(p, flight);
				Optional<BagVo> bagVoOptional = getBagOptional(flightPax);
				bagVoOptional.ifPresent(tempVo::addBag);
				for (Seat s : p.getSeatAssignments()) {
          // exclude APIS seat data
          if (!s.getApis()) {
            SeatVo seatVo = new SeatVo();
            seatVo.setFirstName(p.getPassengerDetails().getFirstName());
            seatVo.setLastName(p.getPassengerDetails().getLastName());
            seatVo.setNumber(s.getNumber());
            seatVo.setApis(s.getApis());
            seatVo.setFlightNumber(flight.getFullFlightNumber());
            tempVo.addSeat(seatVo);
          }
				}
			}

			FlightPax mainPassengerFlightPax = getPnrFlightPax(t, flight);
			Optional<BagVo> bagOptional = getBagOptional(mainPassengerFlightPax);
			bagOptional.ifPresent(bagVo -> tempVo.setBagCount(bagVo.getBag_count()));

			parseRawMessageToSegmentList(tempVo);
			vo.setPnrVo(tempVo);
		}
		List<ApisMessage> apisList = apisMessageRepository.findByFlightIdAndPassengerId(Long.parseLong(flightId), t.getId());
		if(!apisList.isEmpty()) {
			ApisMessage apis = apisList.get(0);
			ApisMessageVo apisVo = new ApisMessageVo();
			apisVo.setApisRecordExists(true);
			apisVo.setTransmissionDate(apis.getEdifactMessage().getTransmissionDate());
			
			List<String> refList = apisMessageRepository.findApisRefByFlightIdandPassengerId(Long.parseLong(flightId), t.getId());			
			List<FlightPax> apisMessageFlightPaxs = apisMessageRepository.findFlightPaxByFlightIdandPassengerId(Long.parseLong(flightId), t.getId())
					.stream().filter(a -> a.getMessageSource().equals("APIS")).collect(Collectors.toList());

			if(!apisMessageFlightPaxs.isEmpty()) {
				FlightPax apisMessageFlightPax = apisMessageFlightPaxs.get(0);
				apisVo.setBagCount(apisMessageFlightPax.getBagCount());
				apisVo.setBagWeight(apisMessageFlightPax.getBagWeight());
			}

			List<FlightPassengerVo> fpList = apisControllerService.generateFlightPaxVoByApisRef(refList.get(0));
			for (FlightPassengerVo flightPassengerVo : fpList) {
				apisVo.addFlightpax(flightPassengerVo);
			}

			for(Bag b: bagList) {
				if(b.getData_source().equalsIgnoreCase("apis")){
					BagVo bagVo = new BagVo();
					bagVo.setBagId(b.getBagId());
					bagVo.setData_source(b.getData_source());
					bagVo.setDestination(b.getDestinationAirport());
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
			vo.setApisMessageVo(apisVo);
		}
		return vo;
	}



	private FlightPax getPnrFlightPax(Passenger p, Flight flight) {
		FlightPax flightPax;
		if (p.getFlightPaxList() != null && !p.getFlightPaxList().isEmpty()) {
			flightPax = p.getFlightPaxList()
					.stream()
					.filter(fp -> isPnr(fp, flight))
					.findFirst()
					.orElse(null);
		} else {
			flightPax = null;
		}
		return flightPax;
	}

	private boolean isPnr(FlightPax fp, Flight flight) {
		return fp.getMessageSource() != null
				&& fp.getMessageSource().equalsIgnoreCase("PNR")
				&& fp.getFlight().equals(flight);
	}

	private Optional<BagVo> getBagOptional(FlightPax fp) {
		Optional<BagVo> bagVoOptional;
		if (fp != null && fp.getBagCount() > 0) {
			BagVo bagVo = new BagVo();
			bagVo.setBag_count(fp.getBagCount());
			bagVo.setAverage_bag_weight(fp.getAverageBagWeight());
			bagVo.setBag_weight(fp.getBagWeight());
			bagVo.setData_source(fp.getMessageSource());
			bagVo.setPassFirstName(fp.getPassenger().getPassengerDetails().getFirstName());
			bagVo.setPassLastName(fp.getPassenger().getPassengerDetails().getLastName());
			bagVo.setData_source(fp.getMessageSource());
			bagVoOptional = Optional.of(bagVo);
		} else {
			bagVoOptional = Optional.empty();
		}
		return bagVoOptional;
	}


	/**
	 * Gets the travel history by pnr id and pnr ref.
	 *
	 * @param paxId
	 *            the passenger id
	 * @param flightId
	 *            the flight id
		 * @return the travel history by passenger and document
	 * @throws ParseException
	 */
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/passengers/passenger/flighthistory", method = RequestMethod.GET)
	public List<FlightVo> getTravelHistoryByPassengerAndItinerary(
			@RequestParam String paxId,
			@RequestParam String flightId)
			throws ParseException {

		List<Pnr> pnrs = pnrService.findPnrByPassengerIdAndFlightId(Long.parseLong(paxId), Long.parseLong(flightId));
		List<String>pnrRefList = apisMessageRepository.findApisRefByFlightIdandPassengerId(Long.parseLong(flightId), Long.parseLong(paxId));
		String pnrRef = !pnrRefList.isEmpty()? pnrRefList.get(0):null;
		Long pnrId = !pnrs.isEmpty()? pnrs.get(0).getId():null;

		if(pnrId != null || pnrRef!=null) {
			return pService
					.getTravelHistoryByItinerary(pnrId, pnrRef)
					.stream()
					.map(flight -> {
								FlightVo flightVo = new FlightVo();
								copyModelToVo(flight, flightVo);
								return flightVo;
							}).collect(Collectors.toCollection(LinkedList::new));
		}
		else {
		return new ArrayList<FlightVo>();
		}

	}
	/**
	 * Gets the travel history by passenger and document.
	 *
	 * @param paxId
	 *            the passenger id
	 * @param flightId
	 * 			  the flight id           
	 * @return the travel history by passenger and document
	 * @throws ParseException
	 */
	
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/passengers/passenger/travelhistory", method = RequestMethod.GET)
	public List<FlightVo> getTravelHistoryByPassengerAndNotItinerary(
			@RequestParam String paxId,
			@RequestParam String flightId)
			throws ParseException {
		
		List<Pnr> pnrs = pnrService.findPnrByPassengerIdAndFlightId(Long.parseLong(paxId), Long.parseLong(flightId));
		List<String>pnrRefList = apisMessageRepository.findApisRefByFlightIdandPassengerId(Long.parseLong(flightId), Long.parseLong(paxId));
		String pnrRef = !pnrRefList.isEmpty()? pnrRefList.get(0):null;		
		Long pnrId = !pnrs.isEmpty()? pnrs.get(0).getId():null;
		
		return pService
			.getTravelHistoryNotByItinerary(Long.valueOf(paxId), pnrId, pnrRef)
			.stream().map(flight -> {
						FlightVo flightVo = new FlightVo();
						copyModelToVo(flight, flightVo);
                                                flightVo.setFlightId(flight.getId().toString());
						return flightVo;
					}).collect(Collectors.toCollection(LinkedList::new));
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/passengers/passenger/bookingdetailhistory", method = RequestMethod.GET)
	public List<FlightVoForFlightHistory> getBookingDetailHistoryByPassenger(
			@RequestParam String paxId,
			@RequestParam String flightId) {

		List<Passenger> passengersWithSamePassengerIdTag = pService.getBookingDetailHistoryByPaxID(Long.valueOf(paxId));
		return copyBookingDetailFlightModelToVo(passengersWithSamePassengerIdTag);

	}
	
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/passengers/passenger/savewatchlistlink", method = RequestMethod.GET)
	public void saveWatchListMatchByPaxId (@RequestParam String paxId) {
		matchingService.performFuzzyMatching(Long.valueOf(paxId));
	}
	
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/passengers/passenger/getwatchlistlink", method = RequestMethod.GET)
	public List<PaxWatchlistLinkVo> getWatchListMatchByPaxId (@RequestParam String paxId) {
		return matchingService.findByPassengerId(Long.valueOf(paxId));
	}
	
	@RequestMapping(value = "/dispositionstatuses", method = RequestMethod.GET)
	public @ResponseBody List<DispositionStatus> getDispositionStatuses() {

		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		User user = userService.fetchUser(userId);
		Role oneDay = new Role(7, "One Day Lookout");
		if (user.getRoles().contains(oneDay)) {
			return null;
		} else {
			return pService.getDispositionStatuses();
		}
	}

	@RequestMapping(value = "/allcases", method = RequestMethod.GET)
	public @ResponseBody List<CaseVo> getAllDispositions() {
		return pService.getAllDispositions();
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
		target.setTotal_bag_count(source.getTotal_bag_count());
		if(source.getBaggageWeight()!=null)target.setBaggageWeight(source.getBaggageWeight());
                
                target.setTripType(source.getTripType());

		if (!source.getAddresses().isEmpty()) {
			Iterator it = source.getAddresses().iterator();
			while (it.hasNext()) {
				Address a = (Address) it.next();
				AddressVo aVo = new AddressVo();

				try {

					BeanUtils.copyProperties(aVo, a);

				} catch (IllegalAccessException | InvocationTargetException e) {
					logger.error("Unable to copy properties, catching and moving to next address", e);
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
				if(fl.getFlight() != null){
					flVo.setFlightNumber(fl.getFlight().getFullFlightNumber());
					flVo.setOriginAirport(fl.getFlight().getOrigin());
					flVo.setDestinationAirport(fl.getFlight().getDestination());
					flVo.setEtd(DateCalendarUtils.formatJsonDateTime(fl.getFlight().getMutableFlightDetails().getEtd()));
					flVo.setEta(DateCalendarUtils.formatJsonDateTime(fl.getFlight().getMutableFlightDetails().getEta()));
					flVo.setFlightId(Long.toString(fl.getFlight().getId()));
					flVo.setDirection(fl.getFlight().getDirection());
				} else{
					flVo.setFlightNumber(fl.getBookingDetail().getFullFlightNumber());
					flVo.setOriginAirport(fl.getBookingDetail().getOrigin());
					flVo.setDestinationAirport(fl.getBookingDetail().getDestination());
					flVo.setEtd(DateCalendarUtils.formatJsonDateTime(fl.getBookingDetail().getEtd()));
					flVo.setEta(DateCalendarUtils.formatJsonDateTime(fl.getBookingDetail().getEta()));
					flVo.setBookingDetailId(Long.toString(fl.getBookingDetail().getId()));
				}
				target.getFlightLegs().add(flVo);
			}
		}

		if (!source.getPassengers().isEmpty()) {
			Iterator it4 = source.getPassengers().iterator();
			while (it4.hasNext()) {
				Passenger p = (Passenger) it4.next();
				PassengerVo pVo = new PassengerVo();
				pVo.setLastName(p.getPassengerDetails().getLastName());
				pVo.setFirstName(p.getPassengerDetails().getFirstName());
				pVo.setMiddleName(p.getPassengerDetails().getMiddleName());
				pVo.setAge(p.getPassengerDetails().getAge());
				pVo.setGender(p.getPassengerDetails().getGender());
				pVo.setPaxId(Long.toString(p.getId()));
				target.getPassengers().add(pVo);
				
				Set<Document> documents = p.getDocuments();
				for (Document d: documents) {
					DocumentVo documentVo = new DocumentVo();
					documentVo.setFirstName(d.getPassenger().getPassengerDetails().getFirstName());
					documentVo.setLastName(d.getPassenger().getPassengerDetails().getLastName());
					documentVo.setDocumentType(d.getDocumentType());
					documentVo.setIssuanceCountry(d.getIssuanceCountry());
					documentVo.setDocumentNumber(d.getDocumentNumber());
					documentVo.setIssuanceDate(d.getIssuanceDate());
					documentVo.setExpirationDate(d.getExpirationDate());
					target.getDocuments().add(documentVo);
				}
			}
		}
		return target;
	}
	/**
	 * Segments PnrRaw String
	 * Required for Frontend to highlight segment corresponding to pnr section
	 * @param targetVo
	 */
	private void parseRawMessageToSegmentList(PnrVo targetVo) {
		if (targetVo != null && targetVo.getRaw() != null) {
                    
			StringTokenizer _tempStr = new StringTokenizer(targetVo.getRaw(),"\n");
			List<KeyValue> segmentList= new ArrayList<>();
			
			final String ITIN = "TVL";
			final String NAME = "SSR";
			final String DOC = "DOCS";
			final String ADD = "ADD";
			final String CC = "FOP";
			final String FF = "FTI";
			final String BAG = "TBD";
			final String TIF = "TIF";

			String tifSegment = "";
                        Integer indexInteger = 0;
                        
			while (_tempStr.hasMoreTokens()) {
				String currString = _tempStr.nextToken();
				StringBuilder segment = new StringBuilder();
				
				//Itinerary
				if (currString.contains(ITIN)) {
					for(FlightLegVo f: targetVo.getFlightLegs()) {
						if(currString.contains(f.getOriginAirport())) {
							segment.append(ITIN);
							segment.append(f.getOriginAirport());
							segment.append(" ");
						}
					}
				}
				//PNR names
				if (currString.contains(NAME)) {
					for(PassengerVo p: targetVo.getPassengers()) {
						if(currString.contains(p.getFirstName())) {
							segment.append(NAME);
							segment.append(p.getFirstName());
							segment.append(" ");
						}
					}
				}
				//Doc Numbers
				if (currString.contains(DOC)) {
					for(DocumentVo d: targetVo.getDocuments()) {
						if(currString.contains(d.getDocumentNumber())) {
							segment.append(DOC);
							segment.append(d.getDocumentNumber());
							segment.append(" ");
						}
					}
				}
				//Addresses
				if (currString.contains(ADD)) {
					for(AddressVo a: targetVo.getAddresses()) {
						if(currString.contains(a.getCity())) {
							segment.append(ADD);
							segment.append(a.getCity());
							segment.append(" ");
						}
					}
				}
				//FOP
				if (currString.contains(CC)) {
					for(CreditCardVo c: targetVo.getCreditCards()) {
						if(currString.contains(c.getNumber().substring(c.getNumber().length()-4))) {
							segment.append(CC);
							segment.append(c.getNumber());
							segment.append(" ");
						}
					}
				}
				//Frequent Flyer
				if (currString.contains(FF)) {
					for(FrequentFlyerVo f: targetVo.getFrequentFlyerDetails()) {
						if(currString.contains(f.getNumber())) {
							segment.append(FF);
							segment.append(f.getNumber());
							segment.append(" ");
						}
					}
				}

				/* GR.7 TIF - the checked-in name.
				Used to link bags to passengers.*/
				if (currString.contains(TIF)) {
					tifSegment = currString;
				}

//             Bag
				if (currString.contains(BAG)) {
					for (BagVo b : targetVo.getBags()) {
						if (isRelatedToTifPassenger(tifSegment, b)) {
							segment.append(BAG);
							segment.append(b.getPassFirstName());
							segment.append(b.getPassLastName());
							segment.append(" ");
						}
					}
				}
				//Phone
				for(PhoneVo p: targetVo.getPhoneNumbers()) {
					if(currString.contains(p.getNumber().substring(p.getNumber().length()-4))) {
						segment.append("PHONE");
						segment.append(p.getNumber());
						segment.append(" ");
					}
				}
				
				//Email
				for(EmailVo e: targetVo.getEmails()) {
					boolean isMatch = true;
					String[] words = e.getAddress().split("[^a-zA-Z0-9']+");

					for(String word: words) {
						if(!currString.contains(word)) {
							isMatch = false;
							break;
						}
					}
					if(words.length>0 && isMatch) {
						segment.append("EMAIL");
						segment.append(e.getAddress());
						segment.append(" ");
					}
				}
				
				//Seat
				for(SeatVo s: targetVo.getSeatAssignments()) {
					if(currString.contains(s.getNumber())) {
						segment.append("SEAT");
						segment.append(s.getNumber());
						segment.append(" ");
					}
				}
				
				//Agency
				for(AgencyVo a: targetVo.getAgencies()) {
					if(a.getIdentifier() != null && currString.contains(a.getIdentifier())) {
						segment.append("AGEN");
						segment.append(a.getIdentifier());
						segment.append(" ");
					}
				}
                                
                                if (segment.toString().isEmpty())
                                {
                                   KeyValue kv = new KeyValue(indexInteger.toString(), currString);
                                   segmentList.add(kv); 
                                }
				else
                                {
                                   KeyValue kv2 = new KeyValue(segment.toString(), currString);
                                   segmentList.add(kv2); ;
                                }
                                
                                indexInteger++;
			}
			targetVo.setSegmentList(segmentList);
		}
	}

	private boolean isRelatedToTifPassenger(String tifSegment, BagVo b) {
		return b.getData_source().equalsIgnoreCase("PNR") &&
				b.getPassFirstName() != null &&
				tifSegment.contains(b.getPassFirstName()) &&
				b.getPassLastName() != null &&
				tifSegment.contains(b.getPassLastName());
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
			logger.error("error copying model to vo", e);
		} 
	}

    /**
     *
     * @param allPassengersRelatingToSingleIdTag -
	 * Passengers represent a single person on a flight once. Passenger Id Tag is how multiple passengers are stored together
	 *                                           in one identity. The list passed in is several passengers who are mapped to a single
	 *                                           passenger id tag / passenger identity. The Method will return a list of
	 *                                           booking details and flights from the list of passenger ids.
     */
	private List<FlightVoForFlightHistory> copyBookingDetailFlightModelToVo(List<Passenger> allPassengersRelatingToSingleIdTag) {

	    List<FlightVoForFlightHistory> flightsAndBookingDetailsRelatingToSamePaxIdTag = new ArrayList<>();

        try {

			List<FlightPax> flightPassengersList = getFlightPaxes(allPassengersRelatingToSingleIdTag);

			Set<Pair<Passenger, Flight>> associatedPaxFlights = flightPassengersList
					.stream()
					.map(flightPax -> new ImmutablePair<>(flightPax.getPassenger(), flightPax.getFlight()))
					.collect(Collectors.toSet());

			List<FlightVoForFlightHistory> flightHistory = associatedPaxFlights
					.stream()
					.map(passengerFlightPair -> {
										FlightVoForFlightHistory flightVo = new FlightVoForFlightHistory();
										populateFlightVoWithFlightDetail(passengerFlightPair.getRight(), flightVo);
										Long pId = passengerFlightPair.getLeft().getId();
										flightVo.setPassId(pId.toString());
										flightVo.setBookingDetail(false);
										return flightVo;
									})
					.collect(toList());

			List<BookingDetail> passengerBookingDetails =
					allPassengersRelatingToSingleIdTag
					.stream()
					.map(Passenger::getBookingDetails)
					.flatMap(Collection::stream)
					.distinct()
					.collect(toList());

			List<FlightVoForFlightHistory> bookingDetailsHistory = passengerBookingDetails.stream()
					.map(bookingDetail -> {
						FlightVoForFlightHistory flightVo = new FlightVoForFlightHistory();
						populateFlightVoWithBookingDetail(bookingDetail, flightVo);
						flightVo.setBookingDetail(true);
						return flightVo;
					})
					.collect(toList());

			flightsAndBookingDetailsRelatingToSamePaxIdTag.addAll(flightHistory);
			flightsAndBookingDetailsRelatingToSamePaxIdTag.addAll(bookingDetailsHistory);

		} catch (Exception e) {
			logger.error("error copying model to vo.", e);
		}

		return flightsAndBookingDetailsRelatingToSamePaxIdTag;
	}

	private List<FlightPax> getFlightPaxes(List<Passenger> allPassengersRelatingToSingleIdTag) {

		List<FlightPax> flightPaxes = allPassengersRelatingToSingleIdTag
				.stream()
				.flatMap(p -> p.getFlightPaxList().stream())
				.collect(toList());

		List<FlightPax> filteredFlightPax = new ArrayList<>();
		Map<Pair<Passenger, Flight>, Boolean> flightPaxRecorded = new HashMap<>();
		for (FlightPax fp : flightPaxes) {
			Pair<Passenger, Flight> flightPaxCombination = new ImmutablePair<>(fp.getPassenger(), fp.getFlight());
			if (!flightPaxRecorded.containsKey(flightPaxCombination)) {
				flightPaxRecorded.put(flightPaxCombination, true);
				filteredFlightPax.add(fp);
			}
		}
		return filteredFlightPax;
	}

	/**
     *
     * @param source
     * @param target
     */
	private void populateFlightVoWithBookingDetail(BookingDetail source, FlightVo target){
        try {

            target.setFlightNumber(((BookingDetail)source).getFlightNumber());
            target.setFullFlightNumber(((BookingDetail)source).getFullFlightNumber());
            target.setCarrier(((BookingDetail)source).getFlightNumber());
            target.setEtaDate(((BookingDetail)source).getEtaDate());
            target.setEtdDate(((BookingDetail)source).getEtdDate());
            target.setOriginCountry(((BookingDetail)source).getOriginCountry());
            target.setOrigin(((BookingDetail)source).getOrigin());
            target.setDestinationCountry(((BookingDetail)source).getDestinationCountry());
            target.setDestination(((BookingDetail)source).getDestination());
			target.setEtd(((BookingDetail)source).getEtd());
			target.setEta(((BookingDetail)source).getEta());
			//target.setFullFlightNumber(((BookingDetail)source).getFullFlightNumber());
            target.setFlightId(source.getId().toString());

        } catch (Exception e) {
            logger.error("error populating flight with booking details", e);
        }
    }

	/**
	 *
	 * @param source
	 * @param target
	 */
	private void populateFlightVoWithFlightDetail(Flight source, FlightVo target){
		try {

			target.setFlightNumber(source.getFlightNumber());
			target.setCarrier(source.getCarrier());
			target.setEtaDate(source.getMutableFlightDetails().getEtaDate());
			target.setEtdDate(source.getEtdDate());
			target.setOriginCountry(source.getOriginCountry());
			target.setOrigin(source.getOrigin());
			target.setDestinationCountry(source.getDestinationCountry());
			target.setDestination(source.getDestination());
			target.setEtd(source.getMutableFlightDetails().getEtd());
			target.setEta(source.getMutableFlightDetails().getEta());
			target.setFullFlightNumber(source.getFullFlightNumber());
                        target.setFlightId(source.getId().toString());

		} catch (Exception e) {
			logger.error("error populating flight vo", e);
		}
	}


	/**
	 * Static utility method to ignore nulls while copying
	 *
	 * @param source
	 * @return
	 */
	public static String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

		Set<String> emptyNames = new HashSet<String>();
		for (java.beans.PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null) emptyNames.add(pd.getName());
		}
		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}

	/**
	 * Wrapper method over BeanUtils.copyProperties
	 *
	 * @param src
	 * @param target
	 */
	public static void copyIgnoringNullValues(Object src, Object target) {
		try {
			org.springframework.beans.BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
		} catch (Exception ex) {
			logger.error("error copy properties ignoring null values", ex);
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

	@RequestMapping(value = "/seats/{flightId}", method = RequestMethod.GET)
    public @ResponseBody java.util.List<SeatVo> getSeatsByFlightId(@PathVariable(value = "flightId") Long flightId) {

    	return fService.getSeatsByFlightId(flightId);
    }

	private Pnr getLatestPnrFromList(List<Pnr> pnrList) {
		Pnr latest = pnrList.get(0);
		for(Pnr p:pnrList) {
			if(p.getId() >latest.getId()) {
				latest=p;
			}
		}
		return latest;
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
