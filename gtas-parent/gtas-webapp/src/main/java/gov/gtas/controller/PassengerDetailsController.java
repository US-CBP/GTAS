/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import gov.gtas.common.BagStatisticCalculator;
import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.model.*;
import gov.gtas.repository.BookingDetailRepository;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.*;
import gov.gtas.services.dto.PassengerNoteSetDto;
import gov.gtas.vo.HitDetailVo;
import gov.gtas.vo.NoteTypeVo;
import gov.gtas.vo.NoteVo;
import gov.gtas.vo.passenger.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import gov.gtas.enumtype.Status;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.json.KeyValue;
import gov.gtas.repository.ApisMessageRepository;
import gov.gtas.repository.BagRepository;
import gov.gtas.services.matcher.MatchingService;
import gov.gtas.services.matching.PaxWatchlistLinkVo;
import gov.gtas.services.search.FlightPassengerVo;
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
	private BookingDetailRepository bookingDetailService;

	@Autowired
	private PnrService pnrService;

	@Autowired
	private MatchingService matchingService;

	@Resource
	private BagRepository bagRepository;

	@Resource
	private ApisMessageRepository apisMessageRepository;

	@Autowired
	private HitDetailService hitDetailService;

	@Autowired
	private PassengerNoteService paxNoteService;

	@Autowired
	private NoteTypeService noteTypeService;

	@Autowired
	private SeatService seatService;

	static final String EMPTY_STRING = "";

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/passengers/passenger/{id}/details", method = RequestMethod.GET)
	public PassengerVo getPassengerByPaxIdAndFlightId(@PathVariable(value = "id") String paxId,
			@RequestParam(value = "flightId", required = false) String flightId) {
		PassengerVo vo = new PassengerVo();
		Passenger t = pService.findByIdWithFlightAndDocuments(Long.valueOf(paxId));
		Flight flight = fService.findById(Long.parseLong(flightId));
		List<Bag> bagList = new ArrayList<>();
		if (flightId != null && flight.getId().toString().equals(flightId)) {
			vo.setFlightNumber(flight.getFlightNumber());
			vo.setCarrier(flight.getCarrier());
			vo.setFlightOrigin(flight.getOrigin());
			vo.setFlightDestination(flight.getDestination());
			vo.setEta(flight.getMutableFlightDetails().getEta());
			vo.setEtd(flight.getMutableFlightDetails().getEtd());
			vo.setFlightOrigin(flight.getOrigin());
			vo.setFlightDestination(flight.getDestination());
			vo.setFlightId(flight.getId().toString());
			vo.setFlightIdTag(flight.getIdTag());

			String seatNumber = seatService.findSeatNumberByFlightIdAndPassengerId(flight.getId(), t.getId());
			vo.setSeat(seatNumber);

			bagList = new ArrayList<>(bagRepository.findFromFlightAndPassenger(flight.getId(), t.getId()));
		}
		vo.setPaxId(String.valueOf(t.getId()));
		if (t.getPassengerIDTag() != null) {
			vo.setPaxIdTag(t.getPassengerIDTag().getIdTag());
		}
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

		// Gather PNR Details
		List<Pnr> pnrList = pnrService.findPnrByPassengerIdAndFlightId(t.getId(), new Long(flightId));

		if (!pnrList.isEmpty()) {
			// APB - why are we not getting the passengerIds from the latest PNR??
			List<Long> passengerIds = pnrList.get(0).getPassengers().stream().map(Passenger::getId).collect(toList());
			Set<Bag> pnrBag = bagRepository.getBagsByPassengerIds(passengerIds);

			// APB - Here we are using "getLatestPnrFromList" to choose which element from
			// pnrList to use,
			// so why use pnrList.get(0) for the pax data ???
			Pnr source = getLatestPnrFromList(pnrList);
			vo.setPnrVo(mapPnrToPnrVo(source));
			PnrVo tempVo = vo.getPnrVo();
			BagSummaryVo bagSummaryVo = BagSummaryVo.createFromFlightAndBookingDetails(pnrBag);
			tempVo.setBagSummaryVo(bagSummaryVo);

			// Assign seat for every passenger on pnr
			for (Passenger p : pnrList.get(0).getPassengers()) {
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
			BagStatisticCalculator bagStatisticCalculator = new BagStatisticCalculator(new HashSet<>(bagList)).invoke("PNR");
			tempVo.setBagCount(bagStatisticCalculator.getBagCount());
			tempVo.setBaggageWeight(bagStatisticCalculator.getBagWeight());
			tempVo.setTotal_bag_count(bagStatisticCalculator.getBagCount());
			parseRawMessageToSegmentList(tempVo);
			vo.setPnrVo(tempVo);
		}
		List<ApisMessage> apisList = apisMessageRepository.findByFlightIdAndPassengerId(Long.parseLong(flightId),
				t.getId());
		if (!apisList.isEmpty()) {
			ApisMessage apis = apisList.get(0);
			ApisMessageVo apisVo = new ApisMessageVo();
			apisVo.setApisRecordExists(true);
			apisVo.setTransmissionDate(apis.getEdifactMessage().getTransmissionDate());

			Passenger passenger = apisMessageRepository
					.findPaxByFlightIdandPassengerId(Long.parseLong(flightId), t.getId());
			String refNumber = passenger.getPassengerTripDetails().getReservationReferenceNumber();
			BagStatisticCalculator bagStatisticCalculator = new BagStatisticCalculator(passenger).invoke("APIS");
			int bagCount = bagStatisticCalculator.getBagCount();
			double bagWeight = bagStatisticCalculator.getBagWeight();
			apisVo.setBagCount(bagCount);
			apisVo.setBagWeight(bagWeight);

			if (refNumber != null) {
				List<FlightPassengerVo> fpList = apisControllerService.generateFlightPassengerList(refNumber,
						Long.parseLong(flightId));
				for (FlightPassengerVo flightPassengerVo : fpList) {
					apisVo.addFlightpax(flightPassengerVo);
				}
			} else {
				logger.warn("There is no APIS Ref number here!");
			}

			for (Bag b : bagList) {
				if (b.getData_source().equalsIgnoreCase("apis")) {
					BagVo bagVo = new BagVo();
					bagVo.setBagId(b.getBagId());
					bagVo.setData_source(b.getData_source());
					bagVo.setDestination(b.getDestinationAirport());
					apisVo.addBag(bagVo);
				}
			}

			Iterator<Phone> phoneIter = apis.getPhones().iterator();
			while (phoneIter.hasNext()) {
				Phone p = phoneIter.next();
				PhoneVo pVo = new PhoneVo();
				pVo.setNumber(p.getNumber());
				apisVo.addPhoneNumber(pVo);
			}
			vo.setApisMessageVo(apisVo);
		}


		return vo;
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
	public List<FlightVo> getTravelHistoryByPassengerAndItinerary(@RequestParam String paxId,
			@RequestParam String flightId) {
		if (paxId == null || flightId == null) {
			throw new IllegalArgumentException("flightId and passengerID required.");
		}
		long longPaxId = Long.parseLong(paxId);
		long longFlightId = Long.parseLong(flightId);
		List<Flight> passengerFlights = fService.getFlightByPaxId(longPaxId);
		List<BookingDetail> passengerBookingDetails = bookingDetailService.bookingDetailsByPassengerId(longPaxId,
				longFlightId);
		/*
		 * FlightVo is returned here for backwards compatibility. On fields that booking
		 * detail doesn't have or doesnt make sense null is returned.
		 */
		// Passenger only ever has 1 flight, the rest are booking details. They are will
		// always have a flight so this will never throw index out of bounds.
		FlightVo flightVo = FlightVo.from(passengerFlights.get(0));
		List<FlightVo> flightVoList = new ArrayList<>();
		flightVoList.add(flightVo);
		if (!passengerBookingDetails.isEmpty()) {
			List<FlightVo> mappedBookingDetails = passengerBookingDetails.stream().map(FlightVo::from)
					.collect(toList());
			flightVoList.addAll(mappedBookingDetails);
		}
		flightVoList.sort(Comparator.comparing(FlightVo::getEta));
		return flightVoList;
	}

	/**
	 * Gets the travel history by passenger and document.
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
	@RequestMapping(value = "/passengers/passenger/travelhistory", method = RequestMethod.GET)
	public List<FlightVo> getTravelHistoryByPassengerAndNotItinerary(@RequestParam String paxId,
			@RequestParam String flightId) throws ParseException {

		List<Pnr> pnrs = pnrService.findPnrByPassengerIdAndFlightId(Long.parseLong(paxId), Long.parseLong(flightId));
		List<String> pnrRefList = apisMessageRepository.findApisRefByFlightIdandPassengerId(Long.parseLong(flightId),
				Long.parseLong(paxId));
		String pnrRef = !pnrRefList.isEmpty() ? pnrRefList.get(0) : null;
		Long pnrId = !pnrs.isEmpty() ? pnrs.get(0).getId() : null;

		return pService.getTravelHistoryNotByItinerary(Long.valueOf(paxId), pnrId, pnrRef).stream().map(flight -> {
			FlightVo flightVo = new FlightVo();
			copyModelToVo(flight, flightVo);
			flightVo.setFlightId(flight.getId().toString());
			return flightVo;
		}).collect(Collectors.toCollection(LinkedList::new));
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/passengers/passenger/bookingdetailhistory", method = RequestMethod.GET)
	public List<FlightVoForFlightHistory> getBookingDetailHistoryByPassenger(@RequestParam String paxId,
			@RequestParam String flightId) {

		List<Passenger> passengersWithSamePassengerIdTag = pService.getBookingDetailHistoryByPaxID(Long.valueOf(paxId));
		return copyBookingDetailFlightModelToVo(passengersWithSamePassengerIdTag);

	}

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/passengers/passenger/hitdetailhistory", method = RequestMethod.GET)
	public List<HitDetailVo> getHitHistory(@RequestParam Long paxId) {

		List<Passenger> passengersWithSamePassengerIdTag = pService.getBookingDetailHistoryByPaxID(paxId);
		Set<Passenger> passengerSet = new HashSet<>(passengersWithSamePassengerIdTag);
		Passenger p = pService.findById(paxId);
		passengerSet.remove(p);
		return hitDetailService.getLast10RecentHits(passengerSet);
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/passengers/passenger/savewatchlistlink", method = RequestMethod.GET)
	public void saveWatchListMatchByPaxId(@RequestParam String paxId) {
		matchingService.performFuzzyMatching(Long.valueOf(paxId));
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/passengers/passenger/getwatchlistlink", method = RequestMethod.GET)
	public Set<PaxWatchlistLinkVo> getWatchListMatchByPaxId(@RequestParam String paxId) throws IOException {
		if (paxId == null) {
			return new HashSet<>();
		}
		Set<HitDetail> hitDetailSet = hitDetailService.getByPassengerId(Long.parseLong(paxId));
		Set<PaxWatchlistLinkVo> paxWatchlistLinkVos = new HashSet<>();
		for (HitDetail hitDetail : hitDetailSet) {
			HitTypeEnum hitTypeEnum = hitDetail.getHitEnum();
			if (HitTypeEnum.WATCHLIST_PASSENGER == hitTypeEnum || HitTypeEnum.PARTIAL_WATCHLIST == hitTypeEnum) {
				PaxWatchlistLinkVo paxWatchlistLinkVo = PaxWatchlistLinkVo.fromHitDetail(hitDetail);
				paxWatchlistLinkVos.add(paxWatchlistLinkVo);
			}
		}

		return paxWatchlistLinkVos;
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = "/passengers/passenger/notes")
	public PassengerNoteSetDto getAllPassengerHistoricalNotes(@RequestParam Long paxId, @RequestParam Boolean historicalNotes) {
		if (historicalNotes != null && historicalNotes) {
			return paxNoteService.getPrevious10PassengerNotes(paxId);
		} else {
			return paxNoteService.getAllEventNotes(paxId);
		}
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@GetMapping(value = "/passengers/passenger/notetypes")
	public List<NoteTypeVo> getAllNoteTypes() {
		return noteTypeService.getAllNoteTypes();
	}

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@PostMapping(value = "/passengers/passenger/note", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void savePassengerNote (@RequestBody NoteVo note) {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		paxNoteService.saveNote(note, userId);
	}

	@RequestMapping(value = "/dispositionstatuses", method = RequestMethod.GET)
	public @ResponseBody List<Object> getDispositionStatuses() {
		return new ArrayList<>();
	}

	@RequestMapping(value = "/allcases", method = RequestMethod.GET)
	public @ResponseBody List<CaseVo> getAllDispositions() {
		return new ArrayList<>();
	}

	@RequestMapping(value = "/createoreditdispstatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JsonServiceResponse createOrEditDispositionStatus() {
		return new JsonServiceResponse(Status.SUCCESS, "Creation or Edit of disposition status successful");
	}

	@RequestMapping(value = "/deletedispstatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody JsonServiceResponse deleteDispositionStatus() {
		return new JsonServiceResponse(Status.SUCCESS, "Deletion of disposition status successful");
	}

	/**
	 * Util method to map PNR model object to VO
	 *
	 * @param source
	 * @return
	 */
	public PnrVo mapPnrToPnrVo(Pnr source) {
		PnrVo target = new PnrVo();

		if (source.getRecordLocator() == null || source.getRecordLocator().isEmpty()) {
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
				if (fl.getFlight() != null) {
					flVo.setFlightNumber(fl.getFlight().getFullFlightNumber());
					flVo.setOriginAirport(fl.getFlight().getOrigin());
					flVo.setDestinationAirport(fl.getFlight().getDestination());
					flVo.setEtd(fl.getFlight().getMutableFlightDetails().getEtd());
					flVo.setEta(fl.getFlight().getMutableFlightDetails().getEta());
					flVo.setFlightId(Long.toString(fl.getFlight().getId()));
					flVo.setDirection(fl.getFlight().getDirection());
				} else {
					flVo.setFlightNumber(fl.getBookingDetail().getFullFlightNumber());
					flVo.setOriginAirport(fl.getBookingDetail().getOrigin());
					flVo.setDestinationAirport(fl.getBookingDetail().getDestination());
					flVo.setEtd(fl.getBookingDetail().getEtd());
					flVo.setEta(fl.getBookingDetail().getEta());
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
				for (Document d : documents) {
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
	 * Segments PnrRaw String Required for Frontend to highlight segment
	 * corresponding to pnr section
	 *
	 * @param targetVo
	 */
	protected void parseRawMessageToSegmentList(PnrVo targetVo) {
		if (targetVo != null && targetVo.getRaw() != null) {

			StringTokenizer _tempStr = new StringTokenizer(targetVo.getRaw(), "\n");
			List<KeyValue> segmentList = new ArrayList<>();

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

				// Itinerary
				if (currString.contains(ITIN)) {
					for (FlightLegVo f : targetVo.getFlightLegs()) {
						if (currString.contains(f.getOriginAirport())) {
							segment.append(ITIN);
							segment.append(f.getOriginAirport());
							segment.append(" ");
						}
					}
				}
				// PNR names
				if (currString.contains(NAME)) {
					for (PassengerVo p : targetVo.getPassengers()) {
						if (currString.contains(p.getFirstName())) {
							segment.append(NAME);
							segment.append(p.getFirstName());
							segment.append(" ");
						}
					}
				}
				// Doc Numbers
				if (currString.contains(DOC)) {
					for (DocumentVo d : targetVo.getDocuments()) {
						if (d.getDocumentNumber() != null && currString.contains(d.getDocumentNumber())) {
							segment.append(DOC);
							segment.append(d.getDocumentNumber());
							segment.append(" ");
						}
					}
				}
				// Addresses
				if (currString.contains(ADD)) {
					for (AddressVo a : targetVo.getAddresses()) {
						if (a.getCity() != null && currString.contains(a.getCity())) {
							segment.append(ADD);
							segment.append(a.getCity());
							segment.append(" ");
						}
					}
				}
				// FOP
				if (currString.contains(CC)) {
					for (CreditCardVo c : targetVo.getCreditCards()) {
						if (currString.contains(c.getNumber().substring(c.getNumber().length() - 4))) {
							segment.append(CC);
							segment.append(c.getNumber());
							segment.append(" ");
						}
					}
				}
				// Frequent Flyer
				if (currString.contains(FF)) {
					for (FrequentFlyerVo f : targetVo.getFrequentFlyerDetails()) {
						if (currString.contains(f.getNumber())) {
							segment.append(FF);
							segment.append(f.getNumber());
							segment.append(" ");
						}
					}
				}

				/*
				 * GR.7 TIF - the checked-in name. Used to link bags to passengers.
				 */
				if (currString.contains(TIF)) {
					tifSegment = currString;
				}

				// Bag
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
				// Phone
				for (PhoneVo p : targetVo.getPhoneNumbers()) {
					if (currString.contains(p.getNumber().substring(p.getNumber().length() - 4))) {
						segment.append("PHONE");
						segment.append(p.getNumber());
						segment.append(" ");
					}
				}

				// Email
				for (EmailVo e : targetVo.getEmails()) {
					boolean isMatch = true;
					String[] words = e.getAddress().split("[^a-zA-Z0-9']+");

					for (String word : words) {
						if (!currString.contains(word)) {
							isMatch = false;
							break;
						}
					}
					if (words.length > 0 && isMatch) {
						segment.append("EMAIL");
						segment.append(e.getAddress());
						segment.append(" ");
					}
				}

				// Seat
				for (SeatVo s : targetVo.getSeatAssignments()) {
					if (currString.contains(s.getNumber())) {
						segment.append("SEAT");
						segment.append(s.getNumber());
						segment.append(" ");
					}
				}

				// Agency
				for (AgencyVo a : targetVo.getAgencies()) {
					if (a.getIdentifier() != null && currString.contains(a.getIdentifier())) {
						segment.append("AGEN");
						segment.append(a.getIdentifier());
						segment.append(" ");
					}
				}

				if (segment.toString().isEmpty()) {
					KeyValue kv = new KeyValue(indexInteger.toString(), currString);
					segmentList.add(kv);
				} else {
					KeyValue kv2 = new KeyValue(segment.toString(), currString);
					segmentList.add(kv2);
					;
				}

				indexInteger++;
			}
			targetVo.setSegmentList(segmentList);
		}
	}

	private boolean isRelatedToTifPassenger(String tifSegment, BagVo b) {
		return b.getData_source().equalsIgnoreCase("PNR") && b.getPassFirstName() != null
				&& tifSegment.contains(b.getPassFirstName()) && b.getPassLastName() != null
				&& tifSegment.contains(b.getPassLastName());
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
	 * @param allPassengersRelatingToSingleIdTag
	 *            - Passengers represent a single person on a flight once. Passenger
	 *            Id Tag is how multiple passengers are stored together in one
	 *            identity. The list passed in is several passengers who are mapped
	 *            to a single passenger id tag / passenger identity. The Method will
	 *            return a list of booking details and flights from the list of
	 *            passenger ids.
	 */
	private List<FlightVoForFlightHistory> copyBookingDetailFlightModelToVo(
			List<Passenger> allPassengersRelatingToSingleIdTag) {

		List<FlightVoForFlightHistory> flightsAndBookingDetailsRelatingToSamePaxIdTag = new ArrayList<>();

		try {

			Set<Passenger> flightPassengersList = new HashSet<>(allPassengersRelatingToSingleIdTag);

			Set<Pair<Passenger, Flight>> associatedPaxFlights = flightPassengersList.stream()
					.map(p -> new ImmutablePair<>(p, p.getFlight()))
					.collect(Collectors.toSet());

			List<FlightVoForFlightHistory> flightHistory = associatedPaxFlights.stream().map(passengerFlightPair -> {
				FlightVoForFlightHistory flightVo = new FlightVoForFlightHistory();
				populateFlightVoWithFlightDetail(passengerFlightPair.getRight(), flightVo);
				Long pId = passengerFlightPair.getLeft().getId();
				flightVo.setPassId(pId.toString());
				flightVo.setBookingDetail(false);
				return flightVo;
			}).collect(toList());

			List<BookingDetail> passengerBookingDetails = allPassengersRelatingToSingleIdTag.stream()
					.map(Passenger::getBookingDetails).flatMap(Collection::stream).distinct().collect(toList());

			List<FlightVoForFlightHistory> bookingDetailsHistory = passengerBookingDetails.stream()
					.map(bookingDetail -> {
						FlightVoForFlightHistory flightVo = new FlightVoForFlightHistory();
						populateFlightVoWithBookingDetail(bookingDetail, flightVo);
						flightVo.setBookingDetail(true);
						return flightVo;
					}).collect(toList());

			flightsAndBookingDetailsRelatingToSamePaxIdTag.addAll(flightHistory);
			flightsAndBookingDetailsRelatingToSamePaxIdTag.addAll(bookingDetailsHistory);

		} catch (Exception e) {
			logger.error("error copying model to vo.", e);
		}

		return flightsAndBookingDetailsRelatingToSamePaxIdTag;
	}

	/**
	 *
	 * @param source
	 * @param target
	 */
	private void populateFlightVoWithBookingDetail(BookingDetail source, FlightVo target) {
		try {

			target.setFlightNumber(((BookingDetail) source).getFlightNumber());
			target.setFullFlightNumber(((BookingDetail) source).getFullFlightNumber());
			target.setCarrier(((BookingDetail) source).getFlightNumber());
			target.setEtaDate(((BookingDetail) source).getEtaDate());
			target.setEtdDate(((BookingDetail) source).getEtdDate());
			target.setOriginCountry(((BookingDetail) source).getOriginCountry());
			target.setOrigin(((BookingDetail) source).getOrigin());
			target.setDestinationCountry(((BookingDetail) source).getDestinationCountry());
			target.setDestination(((BookingDetail) source).getDestination());
			target.setEtd(((BookingDetail) source).getEtd());
			target.setEta(((BookingDetail) source).getEta());
			// target.setFullFlightNumber(((BookingDetail)source).getFullFlightNumber());
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
	private void populateFlightVoWithFlightDetail(Flight source, FlightVo target) {
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
			target.setIdTag(source.getIdTag());
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
			if (srcValue == null)
				emptyNames.add(pd.getName());
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

	@RequestMapping(value = "/seats/{flightId}", method = RequestMethod.GET)
	public @ResponseBody java.util.List<SeatVo> getSeatsByFlightId(@PathVariable(value = "flightId") Long flightId) {

		return fService.getSeatsByFlightId(flightId);
	}

	private Pnr getLatestPnrFromList(List<Pnr> pnrList) {
		Pnr latest = pnrList.get(0);
		for (Pnr p : pnrList) {
			if (p.getId() > latest.getId()) {
				latest = p;
			}
		}
		return latest;
	}

}
