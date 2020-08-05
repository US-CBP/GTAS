/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.enumtype.TripTypeEnum;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import gov.gtas.model.*;
import gov.gtas.parsers.tamr.TamrAdapter;
import gov.gtas.parsers.tamr.model.TamrPassenger;
import gov.gtas.parsers.vo.*;
import gov.gtas.repository.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import gov.gtas.model.lookup.Airport;
import gov.gtas.parsers.edifact.EdifactParser;
import gov.gtas.parsers.pnrgov.PnrGovParser;
import gov.gtas.parsers.pnrgov.PnrUtils;
import gov.gtas.util.LobUtils;

@Service
public class PnrMessageService extends MessageLoaderService {
	private static final Logger logger = LoggerFactory.getLogger(PnrMessageService.class);

	private final PnrRepository msgDao;

	private final LoaderUtils utils;

	private final LookUpRepository lookupRepo;

	private final FlightPaxRepository flightPaxRepository;

	private final BagRepository bagDao;

	private final BookingBagRepository bookingBagRepository;
    
    private final TamrAdapter tamrAdapter;

	@Value("${tamr.enabled}")
	private Boolean tamrEnabled;

	@Value("${additional.processing.enabled.passenger}")
	private Boolean additionalProcessing;


	@Autowired
	public PnrMessageService(PnrRepository msgDao, LoaderUtils utils,
							 LookUpRepository lookupRepo, FlightPaxRepository flightPaxRepository, BagRepository bagRepository,
							 BookingBagRepository bookingBagRepository, TamrAdapter tamrAdapter) {
		this.msgDao = msgDao;
		this.utils = utils;
		this.lookupRepo = lookupRepo;
		this.flightPaxRepository = flightPaxRepository;
		this.bagDao = bagRepository;
		this.bookingBagRepository = bookingBagRepository;
		this.tamrAdapter = tamrAdapter;
	}

	@Override
	public List<String> preprocess(String message) {
		return PnrUtils.getPnrs(message);
	}

	@Override
	@Transactional
	public MessageDto parse(MessageDto msgDto) {
		logger.debug("@ parse");
		long startTime = System.nanoTime();
		Pnr pnr = new Pnr();
		pnr.setCreateDate(new Date());
		pnr.setFilePath(msgDto.getFilepath());
		pnr = msgDao.save(pnr); // make an ID for the PNR
		msgDto.setPnr(pnr);
		MessageStatus messageStatus;
		MessageVo vo = null;
		try {
			EdifactParser<PnrVo> parser = new PnrGovParser();
			vo = parser.parse(msgDto.getRawMsg());
			loaderRepo.checkHashCode(vo.getHashCode());
			pnr.setRaw(LobUtils.createClob(vo.getRaw()));
			messageStatus = new MessageStatus(pnr.getId(), MessageStatusEnum.PARSED);
			messageStatus.setNoLoadingError(true);
			msgDto.setMessageStatus(messageStatus);
			pnr.setHashCode(vo.getHashCode());
			EdifactMessage em = new EdifactMessage();
			em.setTransmissionDate(vo.getTransmissionDate());
			em.setTransmissionSource(vo.getTransmissionSource());
			em.setMessageType(vo.getMessageType());
			em.setVersion(vo.getVersion());
			pnr.setEdifactMessage(em);
			msgDto.setMsgVo(vo);
		} catch (Exception e) {
			messageStatus = new MessageStatus(pnr.getId(), MessageStatusEnum.FAILED_PARSING);
			msgDto.setMessageStatus(messageStatus);
			msgDto.getMessageStatus().setNoLoadingError(false);
			pnr.setStatus(messageStatus);
			GtasLoaderImpl.handleException(e, pnr);
		} finally {
			msgDto.setPnr(pnr);
			if (!loaderRepo.createMessage(pnr)) {
				msgDto.getMessageStatus().setNoLoadingError(false);
				msgDto.getMessageStatus().setMessageStatusEnum(MessageStatusEnum.FAILED_PARSING);
			}
		}
		logger.debug("load time = " + (System.nanoTime() - startTime) / 1000000);
		return msgDto;
	}

	@Override
	@Transactional
	public MessageInformation load(MessageDto msgDto) {
		msgDto.getMessageStatus().setNoLoadingError(true);
		MessageInformation messageInformation = new MessageInformation();
		Pnr pnr = msgDto.getPnr();
		try {
			PnrVo vo = (PnrVo) msgDto.getMsgVo();
			utils.convertPnrVo(pnr, vo);
			Flight primeFlight = loaderRepo.processFlightsAndBookingDetails(vo.getFlights(), pnr.getFlights(),
					pnr.getFlightLegs(), msgDto.getPrimeFlightKey(), pnr.getBookingDetails());
			PassengerInformationDTO passengerInformationDTO = loaderRepo.makeNewPassengerObjects(primeFlight,
					vo.getPassengers(), pnr.getPassengers(), pnr.getBookingDetails(), pnr);
			loaderRepo.processPnr(pnr, vo);
			int createdPassengers = loaderRepo.createPassengers(passengerInformationDTO.getNewPax(),
				 pnr.getPassengers(), primeFlight, pnr.getBookingDetails());
			loaderRepo.updateFlightPassengerCount(primeFlight, createdPassengers);
			Set<Bag> bagList = createBagInformation(vo, pnr, primeFlight);
			WeightCountDto weightCountDto = getBagStatistics(bagList);
			pnr.setBagCount(weightCountDto.getCount());
			pnr.setBaggageWeight(weightCountDto.getWeight());
			createFlightPax(pnr);
			// update flight legs
			for (FlightLeg leg : pnr.getFlightLegs()) {
				leg.setMessage(pnr);
			}
			loaderRepo.calculateDwellTimes(pnr);
			updatePaxEmbarkDebark(pnr);
			loaderRepo.createFormPfPayments(vo, pnr);
			setCodeShareFlights(pnr);
			msgDto.getMessageStatus().setMessageStatusEnum(MessageStatusEnum.LOADED);
			msgDto.getMessageStatus().setFlightId(primeFlight.getId());
			msgDto.getMessageStatus().setFlight(primeFlight);
			pnr.setPassengerCount(pnr.getPassengers().size());
			TripTypeEnum tripType = loaderRepo.calculateTripType(pnr.getFlightLegs(), pnr.getDwellTimes());
			pnr.setTripType(tripType.toString());
			if (tamrEnabled) {
				List<TamrPassenger> tamrPassengers = tamrAdapter
						.convertPassengers(pnr.getFlights().iterator().next(), pnr.getPassengers());
				messageInformation.setTamrPassengers(tamrPassengers);
			}
			if (additionalProcessing) {
				String rawMessage = msgDto.getRawMsg();
				String [] pflightKey = msgDto.getPrimeFlightKey();
				loaderRepo.prepareAdditionalProcessing(messageInformation, pnr, pflightKey, rawMessage);
			}
		} catch (Exception e) {
			msgDto.getMessageStatus().setMessageStatusEnum(MessageStatusEnum.FAILED_LOADING);
			msgDto.getMessageStatus().setNoLoadingError(false);
			pnr.setError(e.toString());
			logger.error("ERROR", e);
		} finally {
			boolean success = loaderRepo.createMessage(pnr);
			if (!success) {
				msgDto.getMessageStatus().setNoLoadingError(false);
				msgDto.getMessageStatus().setMessageStatusEnum(MessageStatusEnum.FAILED_LOADING);
			}
		}
		messageInformation.setMessageStatus(msgDto.getMessageStatus());
		return messageInformation;
	}

	@Transactional
	protected void updatePaxEmbarkDebark(Pnr pnr) {
		logger.debug("@ updatePaxEmbarkDebark");
		long startTime = System.nanoTime();
		List<FlightLeg> legs = pnr.getFlightLegs();
		if (CollectionUtils.isEmpty(legs)) {
			return;
		}
		String embark, debark = "";
		Date firstDeparture, finalArrival = null;

		// If flight is null in either of these checks, then the particular leg must be
		// comprised of a booking detail...
		if (legs.get(0).getFlight() != null) {
			embark = legs.get(0).getFlight().getOrigin();
			firstDeparture = legs.get(0).getFlight().getMutableFlightDetails().getEtd();
		} else { // use BD instead
			embark = legs.get(0).getBookingDetail().getOrigin();
			firstDeparture = legs.get(0).getBookingDetail().getEtd();
		}

		if (legs.get(legs.size() - 1).getFlight() != null) {
			debark = legs.get(legs.size() - 1).getFlight().getDestination();
			finalArrival = legs.get(legs.size() - 1).getFlight().getMutableFlightDetails().getEta();
		} else { // use BD instead
			debark = legs.get(legs.size() - 1).getBookingDetail().getDestination();
			finalArrival = legs.get(legs.size() - 1).getBookingDetail().getEta();
		}

		// Origin / Destination Country Issue #356 code fix.
		if (legs.size() <= 2 && (embark.equals(debark))) {
			if (legs.get(0).getFlight() != null) {
				debark = legs.get(0).getFlight().getDestination();
				finalArrival = legs.get(0).getFlight().getMutableFlightDetails().getEta();
			} else { // use BD instead
				debark = legs.get(0).getBookingDetail().getDestination();
				finalArrival = legs.get(0).getBookingDetail().getEta();
			}
		} else if (legs.size() > 2 && (embark.equals(debark))) {
			DwellTime d = getMaxDwelltime(pnr);
			if (d != null && d.getFlyingTo() != null) {
				debark = d.getFlyingTo();
				finalArrival = d.getArrivalTime();
			}
		}
		setTripDurationTimeForPnr(pnr, firstDeparture, finalArrival);
		for (Passenger p : pnr.getPassengers()) {
			p.getPassengerTripDetails().setEmbarkation(embark);
			Airport airport = utils.getAirport(embark);
			if (airport != null) {
				p.getPassengerTripDetails().setEmbarkCountry(airport.getCountry());
			}

			p.getPassengerTripDetails().setDebarkation(debark);
			airport = utils.getAirport(debark);
			if (airport != null) {
				p.getPassengerTripDetails().setDebarkCountry(airport.getCountry());
			}
		}
		logger.debug("updatePaxEmbarkDebark time = " + (System.nanoTime() - startTime) / 1000000);
	}





	private void setCodeShareFlights(Pnr pnr) {
		Set<Flight> flights = pnr.getFlights();
		Set<CodeShareFlight> codeshares = pnr.getCodeshares();
		for (Flight f : flights) {
			for (CodeShareFlight cs : codeshares) {
				if (cs.getOperatingFlightNumber().equals(f.getFullFlightNumber())) {
					cs.setOperatingFlightId(f.getId());
				}
			}
		}
	}

	private DwellTime getMaxDwelltime(Pnr pnr) {
		Double highest = 0.0;
		DwellTime dt = new DwellTime();
		for (DwellTime d : pnr.getDwellTimes()) {
			highest = d.getDwellTime();
			if (highest == null) {
				continue;
			}
			dt = d;
			for (DwellTime dChk : pnr.getDwellTimes()) {
				if (dChk.getDwellTime() != null) {
					if (dChk.getDwellTime().equals(highest) || dChk.getDwellTime() < highest) {
						continue;
					} else if ((dChk.getDwellTime() > highest) && (highest > 12)) {
						return dChk;
					}
				}

			}
		}
		return dt;
	}

	private void setTripDurationTimeForPnr(Pnr pnr, Date firstDeparture, Date finalArrival) {
		if (firstDeparture != null && finalArrival != null) {
			long diff = finalArrival.getTime() - firstDeparture.getTime();
			if (diff > 0) {
				int minutes = (int) TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS);
				DecimalFormat df = new DecimalFormat("#.##");
				pnr.setTripDuration(Double.valueOf(df.format((double) minutes / 60)));
			}
		}

	}





	private void createFlightPax(Pnr pnr) {
		logger.debug("@ createFlightPax");
		Set<FlightPax> paxRecords = new HashSet<>();
		Set<Flight> flights = pnr.getFlights();
		String homeAirport = lookupRepo.getAppConfigOption(AppConfigurationRepository.DASHBOARD_AIRPORT);
		for (Flight f : flights) {
			for (Passenger p : pnr.getPassengers()) {
				FlightPax fp = p.getFlightPaxList().stream()
						.filter(flightPax -> "PNR".equalsIgnoreCase(flightPax.getMessageSource())).findFirst()
						.orElse(new FlightPax(p.getId()));

				Set<Bag> pnrBags = p.getBags().stream().filter(b -> "PNR".equalsIgnoreCase(b.getData_source()))
						.filter(Bag::isPrimeFlight).collect(Collectors.toSet());

				boolean headPool = pnrBags.stream().anyMatch(Bag::isHeadPool);
				fp.setHeadOfPool(headPool);

				WeightCountDto weightCountDto = getBagStatistics(pnrBags);
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

				fp.setDebarkation(f.getDestination());
				fp.setDebarkationCountry(f.getDestinationCountry());
				fp.setEmbarkation(f.getOrigin());
				fp.setEmbarkationCountry(f.getOriginCountry());
				fp.setPortOfFirstArrival(f.getDestination());
				fp.setMessageSource("PNR");
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
				paxRecords.add(fp);
			}
		}
		flightPaxRepository.saveAll(paxRecords);
	}
	@SuppressWarnings("Duplicates")
	// Logic similar to APIS but differ in making new bags and booking detail
	// relationship.
	private Set<Bag> createBagInformation(PnrVo pvo, Pnr pnr, Flight primeFlight) {

		Set<Bag> passengerBags = new HashSet<>();
		for (Passenger p : pnr.getPassengers()) {
			passengerBags.addAll(p.getBags());
		}
		BagVoToBagAdapter bvoAdapter = new BagVoToBagAdapter(pvo, passengerBags, pnr.getBookingDetails());
		Map<UUID, BagMeasurements> bagMeasurementsMap = loaderRepo
				.saveBagMeasurements(bvoAdapter.getBagMeasurementsVos());
		Set<Bag> newBags = makeNewBags(pnr, primeFlight, bvoAdapter.getPaxMapBagVo(), bagMeasurementsMap);
		Set<Bag> allBags = bvoAdapter.getExistingBags();
		allBags.addAll(newBags);
		bagDao.saveAll(allBags);
		// We do not have a good way to bring back the many to many relationship in
		// memory.
		// I model the join table and just save everything.
		Set<BookingBag> bdBagRelationship = addBookingDetailRelationship(primeFlight, allBags, bvoAdapter);
		bookingBagRepository.saveAll(bdBagRelationship);
		return allBags;
	}

	private Set<BookingBag> addBookingDetailRelationship(Flight primeFlight, Set<Bag> allBags,
			BagVoToBagAdapter bvoAdapter) {
		Map<UUID, BookingDetail> uuidBookingDetailMap = bvoAdapter.getUuidBookingDetailMap();
		Map<UUID, UUID> orphanToBD = bvoAdapter.getOrphanToBD();
		Set<BookingBag> joinTable = new HashSet<>();
		for (Bag bag : allBags) {
			for (UUID flightUUID : bag.getFlightVoUUID()) {
				if (uuidBookingDetailMap.containsKey(flightUUID)) {
					BookingDetail bookingDetail = uuidBookingDetailMap.get(flightUUID);
					joinTable.add(new BookingBag(bag.getId(), bookingDetail.getId()));
				} else if (orphanToBD.containsKey(flightUUID)) {
					UUID bookingDetailUUID = orphanToBD.get(flightUUID);
					BookingDetail bookingDetail = uuidBookingDetailMap.get(bookingDetailUUID);
					joinTable.add(new BookingBag(bag.getId(), bookingDetail.getId()));
				} else if (!flightUUID.equals(primeFlight.getParserUUID())) {
					logger.warn("No connection to booking detail can be made!");
				}
			}
		}
		return joinTable;
	}

	/*
	 * Converts bagVo into a bag, creates relationship with BD as appropriate,
	 * toggles as prime flight where appriorate. Returns list of bags.
	 */
	private Set<Bag> makeNewBags(Pnr pnr, Flight primeFlight, Map<UUID, Set<BagVo>> paxBagVoMap,
			Map<UUID, BagMeasurements> uuidBagMeasurementsMap) {
		Set<Bag> bagList = new HashSet<>();
		for (Passenger p : pnr.getPassengers()) {
			Set<BagVo> bagVoSet = paxBagVoMap.getOrDefault(p.getParserUUID(), Collections.emptySet());
			for (BagVo b : bagVoSet) {
				Bag bag = new Bag();
				bag.setBagId(b.getBagId());
				bag.setAirline(b.getAirline());
				bag.setData_source(b.getData_source());
				bag.setDestinationAirport(b.getDestinationAirport());
				Airport airport = utils.getAirport(b.getDestinationAirport());
				if (airport != null) {
					bag.setCountry(airport.getCountry());
					bag.setDestination(airport.getCity());
				}
				bag.setHeadPool(b.isHeadPool());
				bag.setMemberPool(b.isMemberPool());
				bag.setBagSerialCount(b.getConsecutiveTagNumber());
				bag.setFlight(primeFlight);
				bag.setPassenger(p);
				bag.setPassengerId(p.getId());
				bag.setBagMeasurements(uuidBagMeasurementsMap.get(b.getBagMeasurementUUID()));
				bag.setPrimeFlight(b.isPrimeFlight());
				bag.getFlightVoUUID().addAll(b.getFlightVoId());
				bagList.add(bag);
				p.getBags().add(bag);
			}
		}
		return bagList;
	}

	@Override
	public MessageVo parse(String message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean load(MessageVo messageVo) {
		// TODO Auto-generated method stub
		return false;
	}
}