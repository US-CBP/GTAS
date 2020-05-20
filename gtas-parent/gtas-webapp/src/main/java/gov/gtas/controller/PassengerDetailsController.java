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

import gov.gtas.common.PassengerDetailService;
import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.model.*;
import gov.gtas.repository.BookingDetailRepository;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.*;
import gov.gtas.services.dto.PassengerNoteSetDto;
import gov.gtas.util.PaxDetailVoUtil;
import gov.gtas.vo.HitDetailVo;
import gov.gtas.vo.NoteTypeVo;
import gov.gtas.vo.NoteVo;
import gov.gtas.vo.passenger.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import gov.gtas.enumtype.Status;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.json.KeyValue;
import gov.gtas.repository.ApisMessageRepository;
import gov.gtas.services.matcher.MatchingService;
import gov.gtas.services.matching.PaxWatchlistLinkVo;

import static java.util.stream.Collectors.*;

@Controller
public class PassengerDetailsController {

	private static final Logger logger = LoggerFactory.getLogger(PassengerDetailsController.class);

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
	private ApisMessageRepository apisMessageRepository;

	@Autowired
	private HitDetailService hitDetailService;
	
	@Autowired
	private PassengerNoteService paxNoteService;
	
	@Autowired
	private NoteTypeService noteTypeService;

	@Autowired
	private PassengerDetailService passengerDetailService;

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/passengers/passenger/{id}/details", method = RequestMethod.GET)
	public PassengerVo getPassengerByPaxIdAndFlightId(@PathVariable(value = "id") String paxId,
			@RequestParam(value = "flightId") String flightId) {
		return passengerDetailService.generatePassengerVO(paxId, flightId);
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
		Passenger p = pService.findById(Long.valueOf(paxId));
		boolean linkFlightHistory = true;
		if (!((!p.getDataRetentionStatus().isMaskedPNR()
				&& p.getDataRetentionStatus().isHasPnrMessage())
				|| (!p.getDataRetentionStatus().isMaskedAPIS() && p.getDataRetentionStatus().isHasApisMessage()))) {
			linkFlightHistory = false;
		}
		if (!((!p.getDataRetentionStatus().isDeletedPNR()
				&& p.getDataRetentionStatus().isHasPnrMessage())
				|| (!p.getDataRetentionStatus().isDeletedAPIS() && p.getDataRetentionStatus().isHasApisMessage()))) {
			linkFlightHistory = false;
		}
		List<Passenger> passengersWithSamePassengerIdTag = pService.getBookingDetailHistoryByPaxID(Long.valueOf(paxId));
		return copyBookingDetailFlightModelToVo(passengersWithSamePassengerIdTag, linkFlightHistory);

	}

	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/passengers/passenger/hitdetailhistory", method = RequestMethod.GET)
	public List<HitDetailVo> getHitHistory(@RequestParam Long paxId) {

		List<Passenger> passengersWithSamePassengerIdTag = pService.getBookingDetailHistoryByPaxID(paxId);
		Set<Passenger> passengerSet = new HashSet<>(passengersWithSamePassengerIdTag);
		Passenger p = pService.findById(paxId);
		passengerSet.remove(p);
		List<HitDetailVo> hitDetailVos = hitDetailService.getLast10RecentHits(passengerSet, p);
		if ((!(!p.getDataRetentionStatus().isDeletedAPIS()
				&& p.getDataRetentionStatus().isHasApisMessage()
				|| (!p.getDataRetentionStatus().isDeletedPNR() && p.getDataRetentionStatus().isHasPnrMessage())))) {
			hitDetailVos.forEach( hdv ->
			{
				hdv.setPaxId(null);
				hdv.setFlightId(null);
				hdv.deletePII();
			});
		}else if ((!(!p.getDataRetentionStatus().isMaskedAPIS()
				&& p.getDataRetentionStatus().isHasApisMessage()
				|| (!p.getDataRetentionStatus().isMaskedPNR() && p.getDataRetentionStatus().isHasPnrMessage())))) {
			hitDetailVos.forEach( hdv ->
			{
				hdv.setPaxId(null);
				hdv.setFlightId(null);
				hdv.maskPII();
			});
		}
		return hitDetailVos;
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

//				// Bag
//				if (currString.contains(BAG)) {
//					for (BagVo b : targetVo.getBags()) {
//						if (isRelatedToTifPassenger(tifSegment, b)) {
//							segment.append(BAG);
//							segment.append(b.getPassFirstName());
//							segment.append(b.getPassLastName());
//							segment.append(" ");
//						}
//					}
//				}
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

//	private boolean isRelatedToTifPassenger(String tifSegment, BagVo b) {
//		return b.getData_source().equalsIgnoreCase("PNR") && b.getPassFirstName() != null
//				&& tifSegment.contains(b.getPassFirstName()) && b.getPassLastName() != null
//				&& tifSegment.contains(b.getPassLastName());
//	}

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
			List<Passenger> allPassengersRelatingToSingleIdTag, boolean linkFlightHistory) {

		List<FlightVoForFlightHistory> flightsAndBookingDetailsRelatingToSamePaxIdTag = new ArrayList<>();

		try {

			Set<Passenger> flightPassengersList = new HashSet<>(allPassengersRelatingToSingleIdTag);

			Set<Pair<Passenger, Flight>> associatedPaxFlights = flightPassengersList.stream()
					.map(p -> new ImmutablePair<>(p, p.getFlight()))
					.collect(Collectors.toSet());

			List<FlightVoForFlightHistory> flightHistory = associatedPaxFlights.stream().map(passengerFlightPair -> {
				FlightVoForFlightHistory flightVo = new FlightVoForFlightHistory();
				PaxDetailVoUtil.populateFlightVoWithFlightDetail(passengerFlightPair.getRight(), flightVo);
				Long pId = passengerFlightPair.getLeft().getId();
				flightVo.setBookingDetail(false);
				if (!linkFlightHistory) {
					flightVo.setDisabledLink(true);
					flightVo.setPassId(null);
					flightVo.setFlightId(null);
				} else {
					flightVo.setPassId(pId.toString());
				}
				return flightVo;
			}).collect(toList());

			List<BookingDetail> passengerBookingDetails = allPassengersRelatingToSingleIdTag.stream()
					.map(Passenger::getBookingDetails).flatMap(Collection::stream).distinct().collect(toList());

			List<FlightVoForFlightHistory> bookingDetailsHistory = passengerBookingDetails.stream()
					.map(bookingDetail -> {
						FlightVoForFlightHistory flightVo = new FlightVoForFlightHistory();
						PaxDetailVoUtil.populateFlightVoWithBookingDetail(bookingDetail, flightVo);
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

	@RequestMapping(value = "/seats/{flightId}", method = RequestMethod.GET)
	public @ResponseBody java.util.List<SeatVo> getSeatsByFlightId(@PathVariable(value = "flightId") Long flightId) {

		return fService.getSeatsByFlightId(flightId);
	}

}
