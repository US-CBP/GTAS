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

	@Autowired
	private PendingHitDetailsService pendingHitDetailsService;

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
	@PostMapping(value = "/createmanualpvl")
	public void createManualPVL(@RequestParam Long paxId, @RequestParam Long flightId, @RequestParam Long hitCategoryId, @RequestParam(required = false) String desc) {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		logger.info("Creating Manual PVL");
		pendingHitDetailsService.createManualPendingHitDetail(paxId, flightId, userId, hitCategoryId, desc);
	}

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
		DataRetentionStatus status = pService.findById(Long.valueOf(paxId)).getDataRetentionStatus();
		boolean linkFlightHistory = !status.requiresMaskedPnrAndApisMessage() && !status.requiresDeletedPnrAndApisMessage();

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

		DataRetentionStatus status = p.getDataRetentionStatus();
		if (status.requiresDeletedPnrAndApisMessage()) {
			hitDetailVos.forEach( hdv ->
			{
				hdv.setPaxId(null);
				hdv.setFlightId(null);
				hdv.deletePII();
			});
		} else if (status.requiresMaskedPnrAndApisMessage()) {
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
					.filter(p -> !p.getDataRetentionStatus().requiresMaskedPnrAndApisMessage() && !p.getDataRetentionStatus().requiresDeletedPnrAndApisMessage())
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
