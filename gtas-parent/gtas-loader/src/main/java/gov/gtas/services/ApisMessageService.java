/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.util.*;
import java.util.stream.Collectors;

import gov.gtas.config.ParserConfig;
import gov.gtas.model.*;
import gov.gtas.model.lookup.Airport;
import gov.gtas.parsers.tamr.TamrAdapter;
import gov.gtas.parsers.tamr.model.TamrPassenger;
import gov.gtas.parsers.vo.BagVo;
import gov.gtas.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import gov.gtas.error.ErrorUtils;
import gov.gtas.parsers.edifact.EdifactParser;
import gov.gtas.parsers.paxlst.PaxlstParserUNedifact;
import gov.gtas.parsers.paxlst.PaxlstParserUSedifact;
import gov.gtas.parsers.vo.ApisMessageVo;
import gov.gtas.parsers.vo.MessageVo;
import gov.gtas.util.LobUtils;

import javax.transaction.Transactional;

@Service
public class ApisMessageService extends MessageLoaderService {
	private static final Logger logger = LoggerFactory.getLogger(ApisMessageService.class);

	@Autowired
	private ApisMessageRepository msgDao;

	@Autowired
	private BagRepository bagDao;

	@Autowired
	private BookingBagRepository bookingBagRepository;
	
	@Autowired
	private TamrAdapter tamrAdapter;

	@Autowired
	private LookUpRepository lookupRepo;

	@Value("${tamr.enabled}")
	private Boolean tamrEnabled;

	@Autowired
	private ParserConfig parserConfig;

	@Autowired
	private PassengerTripRepository passengerTripRepository;

	@Value("${additional.processing.enabled.passenger}")
	private Boolean additionalProcessing;

	@Override
	public List<String> preprocess(String message) {
		return Collections.singletonList(message);
	}

	@Override
	@Transactional
	public MessageDto parse(MessageDto msgDto) {
		ApisMessage apis = new ApisMessage();
		apis.setCreateDate(new Date());
		apis.setFilePath(msgDto.getFilepath());
		apis = msgDao.save(apis);
		MessageStatus messageStatus = new MessageStatus(apis.getId(), MessageStatusEnum.RECEIVED);
		msgDto.setMessageStatus(messageStatus);
		apis.setStatus(messageStatus);
		MessageVo vo = null;
		try {
			EdifactParser<ApisMessageVo> parser = null;
			if (isUSEdifactFile(msgDto.getRawMsg())) {
				parser = new PaxlstParserUSedifact(parserConfig);
			} else {
				parser = new PaxlstParserUNedifact(parserConfig);
			}

			vo = parser.parse(msgDto.getRawMsg());
			loaderRepo.checkHashCode(vo.getHashCode());
			apis.setRaw(LobUtils.createClob(vo.getRaw()));

			msgDto.getMessageStatus().setMessageStatusEnum(MessageStatusEnum.PARSED);
			msgDto.getMessageStatus().setNoLoadingError(true);
			apis.setHashCode(vo.getHashCode());
			EdifactMessage em = new EdifactMessage();
			em.setTransmissionDate(vo.getTransmissionDate());
			em.setTransmissionSource(vo.getTransmissionSource());
			em.setMessageType(vo.getMessageType());
			em.setVersion(vo.getVersion());
			apis.setEdifactMessage(em);
			msgDto.setMsgVo(vo);
		} catch (Exception e) {
			msgDto.getMessageStatus().setMessageStatusEnum(MessageStatusEnum.FAILED_PARSING);
			msgDto.getMessageStatus().setNoLoadingError(false);
			GtasLoaderImpl.handleException(e, apis);
		} finally {
			if (!loaderRepo.createMessage(apis)) {
				msgDto.getMessageStatus().setNoLoadingError(false);
				msgDto.getMessageStatus().setMessageStatusEnum(MessageStatusEnum.FAILED_PARSING);
			}
		}
		msgDto.setApis(apis);
		return msgDto;
	}

	@Override
	@Transactional
	public MessageInformation load(MessageDto msgDto) {
		MessageInformation messageInformation = new MessageInformation();
		msgDto.getMessageStatus().setNoLoadingError(true);
		ApisMessage apis = msgDto.getApis();
		try {
			ApisMessageVo m = (ApisMessageVo) msgDto.getMsgVo();
			loaderRepo.processReportingParties(apis, m.getReportingParties());

			Flight primeFlight = loaderRepo.processFlightsAndBookingDetails(m.getFlights(), apis.getFlights(),
					apis.getFlightLegs(), msgDto.getPrimeFlightKey(), apis.getBookingDetails());

			PassengerInformationDTO passengerInformationDTO = loaderRepo.makeNewPassengerObjects(primeFlight,
					m.getPassengers(), apis.getPassengers(), apis.getBookingDetails(), apis);

			int createdPassengers = loaderRepo.createPassengers(passengerInformationDTO.getNewPax(), apis.getPassengers(), primeFlight, apis.getBookingDetails());

			updateApisCoTravelerCount(apis);
			// MUST be after creation of passengers - otherwise APIS will have empty list of
			// passengers.
			createBagInformation(m, apis, primeFlight);
			createFlightPax(apis);
			loaderRepo.updateFlightPassengerCount(primeFlight, createdPassengers);
			createFlightLegs(apis);

			msgDto.getMessageStatus().setMessageStatusEnum(MessageStatusEnum.LOADED);
			msgDto.getMessageStatus().setFlightId(primeFlight.getId());
			msgDto.getMessageStatus().setFlight(primeFlight);
			apis.setPassengerCount(apis.getPassengers().size());
			if (tamrEnabled) {
				List<TamrPassenger> tamrPassengers = tamrAdapter
						.convertPassengers(apis.getFlights().iterator().next(), apis.getPassengers());
				messageInformation.setTamrPassengers(tamrPassengers);
			}
			if (additionalProcessing) {
				String rawMessage = msgDto.getRawMsg();
				String [] pflightKey = msgDto.getPrimeFlightKey();
				loaderRepo.prepareAdditionalProcessing(messageInformation, apis, pflightKey, rawMessage);
			}
		} catch (Exception e) {
			msgDto.getMessageStatus().setNoLoadingError(false);
			msgDto.getMessageStatus().setMessageStatusEnum(MessageStatusEnum.FAILED_LOADING);
			GtasLoaderImpl.handleException(e, msgDto.getApis());
		} finally {
			boolean success = loaderRepo.createMessage(apis);
			msgDto.getMessageStatus().setNoLoadingError(success);

		}
		messageInformation.setMessageStatus(msgDto.getMessageStatus());
		return messageInformation;
	}


	private void updateApisCoTravelerCount(ApisMessage apis) {
		Map<String, Integer> caching = new HashMap<>();
		for (Passenger p : apis.getPassengers()) {
			int apisCoTravelerCount = 0;
			String reservationNumber = p.getPassengerTripDetails().getReservationReferenceNumber();
			if (!StringUtils.isBlank(reservationNumber) && !caching.containsKey(reservationNumber)) {
				apisCoTravelerCount = passengerTripRepository.getCoTravelerCount(p.getId(), reservationNumber);
				caching.put(reservationNumber, apisCoTravelerCount);
			} else if (!StringUtils.isBlank(reservationNumber)){
				apisCoTravelerCount = caching.get(reservationNumber);
			}
			p.getPassengerTripDetails().setCoTravelerCount(apisCoTravelerCount);
		}
	}

	/*
	 *
	 * PNR and APIS make different assumptions about bags and are treated
	 * differently. PNR specifies which bags made it on the plane. We assume all
	 * apis flights have the same bags.
	 */
	@SuppressWarnings("Duplicates")
	// Logic similar to PNR but booking detail relationship creation and bag
	// creation differ.
	private void createBagInformation(ApisMessageVo m, ApisMessage apis, Flight primeFlight) {

		Set<Bag> passengerBags = new HashSet<>();
		for (Passenger p : apis.getPassengers()) {
			passengerBags.addAll(p.getBags());
		}
		BagVoToBagAdapter bvoAdapter = new BagVoToBagAdapter(m, passengerBags, apis.getBookingDetails());
		Map<UUID, BagMeasurements> bagMeasurementsMap = loaderRepo
				.saveBagMeasurements(bvoAdapter.getBagMeasurementsVos());
		Set<Bag> newBags = makeNewBags(apis, primeFlight, bvoAdapter.getPaxMapBagVo(), bagMeasurementsMap);
		Set<Bag> allBags = bvoAdapter.getExistingBags();
		allBags.addAll(newBags);
		bagDao.saveAll(allBags);
		// We do not have a good way to bring back the many to many relationship in
		// memory.
		// I model the join table so I do not have to pull back the whole set in memory.
		Set<BookingBag> bookingBagsJoin = new HashSet<>();
		for (Bag bag : allBags) {
			for (BookingDetail bd : apis.getBookingDetails()) {
				bookingBagsJoin.add(new BookingBag(bag.getId(), bd.getId()));
			}
		}
		bookingBagRepository.saveAll(bookingBagsJoin);
	}

	private Set<Bag> makeNewBags(ApisMessage apis, Flight primeFlight, Map<UUID, Set<BagVo>> bagVoMap,
			Map<UUID, BagMeasurements> uuidBagMeasurementsMap) {
		Set<Bag> bagSet = new HashSet<>();
		for (Passenger p : apis.getPassengers()) {
			Set<BagVo> bagVoSet = bagVoMap.getOrDefault(p.getParserUUID(), Collections.emptySet());
			for (BagVo b : bagVoSet) {
				if (p.getParserUUID().equals(b.getPassengerId()) && b.getBagId() != null) {
					Bag bag = new Bag();
					bag.setBagId(b.getBagId());
					Airport airport = utils.getAirport(primeFlight.getDestination());
					if (airport != null) {
						bag.setDestination(airport.getCity());
						bag.setDestinationAirport(airport.getIata());
					}
					bag.setAirline(b.getAirline());
					bag.setData_source(b.getData_source());
					bag.setBagMeasurements(uuidBagMeasurementsMap.get(b.getBagMeasurementUUID()));
					// The following fields are derived from the flight. They match the prime flight
					// on APIS
					// but can be different on PNR records. To have consistent data
					// we fill in these fields on APIS. Because we assume all apis bags are on all
					// flights
					// APIS bags will always be on the border crossing flight and therefore are a
					// prime flight.
					bag.setFlight(primeFlight);
					bag.setDestination(primeFlight.getDestination());
					bag.setCountry(primeFlight.getDestinationCountry());
					bag.setPrimeFlight(true);
					bag.setPassenger(p);
					bag.setPassengerId(p.getId());
					primeFlight.getBags().add(bag);
					bagSet.add(bag);
					p.getBags().add(bag);
				}
			}
		}
		return bagSet;
	}

	private void createFlightLegs(ApisMessage apis) {

		if (apis != null && apis.getFlightLegs() != null) {
			for (FlightLeg leg : apis.getFlightLegs()) {
				leg.setMessage(apis);
			}
		}

	}

	@Override
	public MessageVo parse(String message) {
		return null; // unused
	}

	@Override
	public boolean load(MessageVo messageVo) {
		return false;
	}

	private boolean isUSEdifactFile(String msg) {
		// review of Citizenship from foreign APIS Issue #387 fix
		// Both UNS and PDT are mandatory for USEDIFACT.CDT doesn't exist in spec
		if (((msg.contains("PDT+P")) || (msg.contains("PDT+V")) || (msg.contains("PDT+A"))) && (msg.contains("UNS"))) {
			return true;
		}
		// return (msg.contains("CDT") || msg.contains("PDT"));
		return false;
	}
	private void createFlightPax(ApisMessage apisMessage) {
		Set<Flight> flights = apisMessage.getFlights();
		String homeAirport = lookupRepo.getAppConfigOption(AppConfigurationRepository.DASHBOARD_AIRPORT);
		for (Flight f : flights) {
			for (Passenger p : apisMessage.getPassengers()) {
				FlightPax fp = p.getFlightPaxList().stream()
						.filter(flightPax -> "APIS".equalsIgnoreCase(flightPax.getMessageSource())).findFirst()
						.orElse(new FlightPax(p.getId()));

				Set<Bag> apisBags = p.getBags().stream().filter(b -> "APIS".equalsIgnoreCase(b.getData_source()))
						.filter(Bag::isPrimeFlight).collect(Collectors.toSet());

				WeightCountDto weightCountDto = getBagStatistics(apisBags);
				fp.setAverageBagWeight(weightCountDto.average());
				if (weightCountDto.getWeight() == null) {
					fp.setBagWeight(0D);
				} else {
					fp.setBagWeight(weightCountDto.getWeight());
				}
				if (weightCountDto.getCount() == null) {
					fp.setBagCount(0);
				} else {
					fp.setBagCount(weightCountDto.getCount());
				}

				fp.getApisMessage().add(apisMessage);
				fp.setDebarkation(p.getPassengerTripDetails().getDebarkation());
				fp.setDebarkationCountry(p.getPassengerTripDetails().getDebarkCountry());
				fp.setEmbarkation(p.getPassengerTripDetails().getEmbarkation());
				fp.setEmbarkationCountry(p.getPassengerTripDetails().getEmbarkCountry());
				fp.setPortOfFirstArrival(f.getDestination());
				fp.setMessageSource("APIS");
				fp.setFlight(f);
				fp.setFlightId(f.getId());
				fp.setResidenceCountry(p.getPassengerDetails().getResidencyCountry());
				fp.setTravelerType(p.getPassengerDetails().getPassengerType());
				fp.setReservationReferenceNumber(p.getPassengerTripDetails().getReservationReferenceNumber());
				if (StringUtils.isNotBlank(fp.getDebarkation()) && StringUtils.isNotBlank(fp.getEmbarkation())) {
					if (homeAirport.equalsIgnoreCase(fp.getDebarkation())
							|| homeAirport.equalsIgnoreCase(fp.getEmbarkation())) {
						p.getPassengerTripDetails()
								.setTravelFrequency(p.getPassengerTripDetails().getTravelFrequency() + 1);
					}
				}
				apisMessage.addToFlightPax(fp);
			}
		}
	}
}
