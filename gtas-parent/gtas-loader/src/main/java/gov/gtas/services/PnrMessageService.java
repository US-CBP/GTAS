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
import gov.gtas.parsers.vo.*;
import gov.gtas.repository.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.gtas.error.ErrorUtils;
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


    private final BagRepository bagDao;

    private final
    FlightPaxRepository flightPaxRepository;

    private final
    BookingDetailRepository bookingDetailDao;



    @Autowired
    public PnrMessageService(PnrRepository msgDao,
                             LoaderUtils utils,
                             LookUpRepository lookupRepo,
                             FlightPaxRepository flightPaxRepository,
                             BagRepository bagRepository,
                             BookingDetailRepository bookingDetailDao
    ) {
        this.msgDao = msgDao;
        this.utils = utils;
        this.lookupRepo = lookupRepo;
        this.flightPaxRepository = flightPaxRepository;
        this.bagDao = bagRepository;
        this.bookingDetailDao = bookingDetailDao;

    }

    @Override
    public List<String> preprocess(String message) {
        return PnrUtils.getPnrs(message);
    }

    @Override
    public MessageDto parse(MessageDto msgDto) {
        logger.debug("@ parse");
        long startTime = System.nanoTime();
        Pnr pnr = new Pnr();
        pnr.setCreateDate(new Date());
        pnr.setFilePath(msgDto.getFilepath());
        pnr = msgDao.save(pnr); //make an ID for the PNR
        msgDto.setPnr(pnr);
        MessageStatus messageStatus;
        MessageVo vo = null;
        try {
            EdifactParser<PnrVo> parser = new PnrGovParser();
            vo = parser.parse(msgDto.getRawMsg());
            loaderRepo.checkHashCode(vo.getHashCode());
            pnr.setRaw(LobUtils.createClob(vo.getRaw()));
            messageStatus = new MessageStatus(pnr.getId(), MessageStatusEnum.PARSED);
            messageStatus.setSuccess(true);
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
            msgDto.getMessageStatus().setSuccess(false);
            handleException(e, pnr);
        } finally {
            msgDto.setPnr(pnr);
            if (!createMessage(pnr)) {
                msgDto.getMessageStatus().setSuccess(false);
                msgDto.getMessageStatus().setMessageStatusEnum(MessageStatusEnum.FAILED_PARSING);
            }
        }
        logger.debug("load time = " + (System.nanoTime() - startTime) / 1000000);
        return msgDto;
    }


    @Override
    public MessageStatus load(MessageDto msgDto) {
        msgDto.getMessageStatus().setSuccess(true);
        Pnr pnr = msgDto.getPnr();
        try {
            PnrVo vo = (PnrVo)msgDto.getMsgVo();
				utils.convertPnrVo(pnr, vo);
				Flight primeFlight = loaderRepo.processFlightsAndBookingDetails(
						vo.getFlights(),
						pnr.getFlights(),
						pnr.getFlightLegs(),
						msgDto.getPrimeFlightKey(),
						pnr.getBookingDetails());
				PassengerInformationDTO passengerInformationDTO = loaderRepo.makeNewPassengerObjects(primeFlight,
						vo.getPassengers(),
						pnr.getPassengers(),
						pnr.getBookingDetails(),
						pnr);
				loaderRepo.processPnr(pnr, vo);

            int createdPassengers = loaderRepo.createPassengers(
                    passengerInformationDTO.getNewPax(),
                    passengerInformationDTO.getOldPax(),
                    pnr.getPassengers(),
                    primeFlight,
                    pnr.getBookingDetails());
            loaderRepo.updateFlightPassengerCount(primeFlight, createdPassengers);
            Set<Bag> bagList = createBagInformation(vo, pnr, primeFlight);
            WeightCountDto weightCountDto = getBagStatistics(bagList);
            pnr.setBagCount(weightCountDto.getCount());
            pnr.setBaggageWeight(weightCountDto.getWeight());
            loaderRepo.createBookingDetails(pnr, passengerInformationDTO.getBdSet());
            createFlightPax(pnr);
            // update flight legs
            for (FlightLeg leg : pnr.getFlightLegs()) {
                leg.setMessage(pnr);
            }
            for (BookingDetail bD : pnr.getBookingDetails()) {
                bD.getPnrs().add(pnr);
            }
            calculateDwellTimes(pnr);
            updatePaxEmbarkDebark(pnr);
            loaderRepo.createFormPfPayments(vo, pnr);
            setCodeShareFlights(pnr);
            msgDto.getMessageStatus().setMessageStatusEnum(MessageStatusEnum.LOADED);
            pnr.setPassengerCount(pnr.getPassengers().size());

            TripTypeEnum tripType = calculateTripType(pnr.getFlightLegs(), pnr.getDwellTimes());
            pnr.setTripType(tripType.toString());

        } catch (Exception e) {
            msgDto.getMessageStatus().setMessageStatusEnum(MessageStatusEnum.FAILED_LOADING);
            msgDto.getMessageStatus().setSuccess(false);
            pnr.setError(e.toString());
            logger.error("ERROR", e);
        } finally {
            if (!createMessage(pnr)) {
                msgDto.getMessageStatus().setSuccess(false);
                msgDto.getMessageStatus().setMessageStatusEnum(MessageStatusEnum.FAILED_LOADING);
            }
        }
        return msgDto.getMessageStatus();
    }

    private WeightCountDto getBagStatistics(Set<Bag> bagSet) {
       Set<BagMeasurements> bagMeasurementsSet = bagSet.stream().map(Bag::getBagMeasurements).collect(Collectors.toSet());
       Integer bagCount = 0;
       Double bagWeight = 0D;
       for (BagMeasurements bagMeasurements : bagMeasurementsSet) {
           if (bagMeasurements.getBagCount() != null) {
               bagCount += bagMeasurements.getBagCount();
           }
           if (bagMeasurements.getWeight() != null) {
               bagWeight += bagMeasurements.getWeight();
           }
       }
       WeightCountDto weightCountDto = new WeightCountDto();
       weightCountDto.setCount(bagCount);
       weightCountDto.setWeight(bagWeight);
       return weightCountDto;
}


    @Transactional
    protected void updatePaxEmbarkDebark(Pnr pnr)  {
        logger.debug("@ updatePaxEmbarkDebark");
        long startTime = System.nanoTime();
        List<FlightLeg> legs = pnr.getFlightLegs();
        if (CollectionUtils.isEmpty(legs)) {
            return;
        }
        String embark, debark = "";
        Date firstDeparture, finalArrival = null;

        //If flight is null in either of these checks, then the particular leg must be comprised of a booking detail...
        if (legs.get(0).getFlight() != null) {
            embark = legs.get(0).getFlight().getOrigin();
            firstDeparture = legs.get(0).getFlight().getMutableFlightDetails().getEtd();
        } else { //use BD instead
            embark = legs.get(0).getBookingDetail().getOrigin();
            firstDeparture = legs.get(0).getBookingDetail().getEtd();
        }

        if (legs.get(legs.size() - 1).getFlight() != null) {
            debark = legs.get(legs.size() - 1).getFlight().getDestination();
            finalArrival = legs.get(legs.size() - 1).getFlight().getMutableFlightDetails().getEta();
        } else { //use BD instead
            debark = legs.get(legs.size() - 1).getBookingDetail().getDestination();
            finalArrival = legs.get(legs.size() - 1).getBookingDetail().getEta();
        }

        //Origin / Destination Country Issue #356 code fix.
        if (legs.size() <= 2 && (embark.equals(debark))) {
            if (legs.get(0).getFlight() != null) {
                debark = legs.get(0).getFlight().getDestination();
                finalArrival = legs.get(0).getFlight().getMutableFlightDetails().getEta();
            } else { //use BD instead
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
    
    private TripTypeEnum calculateTripType(List<FlightLeg> flightLegList, Set<DwellTime> dwellTimeSet) {
        String firstLegOrigin = "";
        String lastLegDestination = "";
        Integer maxFlightLegNumber = 0;
        TripTypeEnum tripType = null;
        boolean hasLongDwellTime = false;

        if (flightLegList.size() > 1)
        {
            for (FlightLeg flightLeg : flightLegList)
            {
                Integer flightLegNumber = flightLeg.getLegNumber();
                if (flightLegNumber > maxFlightLegNumber)
                {
                    maxFlightLegNumber = flightLegNumber;
                }
            }

            for (int i = 0;i <  flightLegList.size(); i++)
            {
               if (flightLegList.get(i).getLegNumber().equals(0))
               {
                  FlightLeg firstFlightLeg = flightLegList.get(i);
                  if (firstFlightLeg.getBookingDetail() != null)
                  {
                    firstLegOrigin = firstFlightLeg.getBookingDetail().getOrigin();
                  }
                  else // we have a prime flight
                  {
                    firstLegOrigin = firstFlightLeg.getFlight().getOrigin();
                  }
               }

               if (flightLegList.get(i).getLegNumber().equals(maxFlightLegNumber))
               {
                  FlightLeg lastFlightLeg = flightLegList.get(i);
                  if (lastFlightLeg.getBookingDetail() != null)
                  {
                    lastLegDestination = lastFlightLeg.getBookingDetail().getDestination();
                  }
                  else // we have a prime flight
                  {
                    lastLegDestination = lastFlightLeg.getFlight().getDestination();
                  }                               
               }
            } 

            if (firstLegOrigin.equalsIgnoreCase(lastLegDestination)) {
                tripType = TripTypeEnum.ROUNDTRIP;
            } else {
                tripType = TripTypeEnum.ONEWAY;
            }

            // check dwell times for ones over 24 hours
            for (DwellTime dwellTime : dwellTimeSet) {
                if (dwellTime.getDwellTime() != null) {
                    double dwellTimeHours = dwellTime.getDwellTime();
                    if (dwellTimeHours > 24.0) {
                        hasLongDwellTime = true;
                        break;
                    }
                }
            }

            if (tripType.equals(TripTypeEnum.ONEWAY) && hasLongDwellTime) {
                tripType = TripTypeEnum.MULTICITY;
            }

        } else {
            tripType = TripTypeEnum.ONEWAY;
        }

        return tripType;
    }

    private void calculateDwellTimes(Pnr pnr){
    	logger.debug("@ calculateDwellTimes");
    	long startTime = System.nanoTime();
    	List<FlightLeg> legs=pnr.getFlightLegs();
        if (CollectionUtils.isEmpty(legs)) {
            return;
        }

        for (int i = 0; i < legs.size(); i++) {
            if (i + 1 < legs.size()) { //If the 'next' leg actually exists
                //4 different combinations of flights and booking details n^2, where n = 2. FxF, FxB, BxF, BxB. Order matters due to time calc
                if (legs.get(i).getFlight() != null) {
                    if (legs.get(i + 1).getFlight() != null) { //FxF
                        utils.setDwellTime(legs.get(i).getFlight(), legs.get(i + 1).getFlight(), pnr);
                    } else { //next leg is a booking detail //FxB
                        utils.setDwellTime(legs.get(i).getFlight(), legs.get(i + 1).getBookingDetail(), pnr);
                    }
                } else if (legs.get(i + 1).getFlight() != null) { //first leg is booking detail BxF
                    utils.setDwellTime(legs.get(i).getBookingDetail(), legs.get(i + 1).getFlight(), pnr);
                } else { //both legs are booking details BxB
                    utils.setDwellTime(legs.get(i).getBookingDetail(), legs.get(i + 1).getBookingDetail(), pnr);
                }
            }
        }
        logger.debug("calculateDwellTime time = " + (System.nanoTime() - startTime) / 1000000);
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

    private void handleException(Exception e, Pnr pnr) {
        // set all the collections to null so we can save the message itself
        pnr.setFlights(null);
        pnr.setPassengers(null);
        pnr.setFlightLegs(null);
        pnr.setCreditCards(null);
        pnr.setAddresses(null);
        pnr.setAgencies(null);
        pnr.setEmails(null);
        pnr.setFrequentFlyers(null);
        pnr.setPhones(null);
        pnr.setDwellTimes(null);
        pnr.setPaymentForms(null);
        String stacktrace = ErrorUtils.getStacktrace(e);
        pnr.setError(stacktrace);
        logger.error(stacktrace);
    }

    private boolean createMessage(Pnr m) {
        boolean ret = true;
        logger.debug("@createMessage");
        long startTime = System.nanoTime();
        try {
            m = msgDao.save(m);
            ret = true;
/*			if (useIndexer) {
				indexer.indexPnr(m);
			}*/
        } catch (Exception e) {
            handleException(e, m);
            ret = false;
            try {
                m = msgDao.save(m);
            } catch (Exception ignored) {
            }
            logger.warn("Error saving message!", e);
        } finally {
            logger.debug("createMessage time = " + (System.nanoTime() - startTime) / 1000000);
        }
        return ret;
    }

    private void createFlightPax(Pnr pnr) {
        logger.debug("@ createFlightPax");
        Set<FlightPax> paxRecords = new HashSet<>();
        Set<Flight> flights = pnr.getFlights();
        String homeAirport = lookupRepo.getAppConfigOption(AppConfigurationRepository.DASHBOARD_AIRPORT);
        for (Flight f : flights) {
            for (Passenger p : pnr.getPassengers()) {
                FlightPax fp = p.getFlightPaxList().stream()
                        .filter(flightPax -> "PNR".equalsIgnoreCase(flightPax.getMessageSource()))
                        .findFirst()
                        .orElse(new FlightPax());

                Set<Bag> pnrBags = p.getBags()
                        .stream()
                        .filter(b -> "PNR".equalsIgnoreCase(b.getData_source()))
                        .collect(Collectors.toSet());

                boolean headPool = pnrBags.stream().anyMatch(Bag::isHeadPool);
                fp.setHeadOfPool(headPool);

                WeightCountDto weightCountDto = getBagStatistics(pnrBags);
                fp.setAverageBagWeight(weightCountDto.average());
                fp.setBagWeight(weightCountDto.getWeight());
                fp.setBagCount(weightCountDto.getCount());

                fp.setDebarkation(f.getDestination());
                fp.setDebarkationCountry(f.getDestinationCountry());
                fp.setEmbarkation(f.getOrigin());
                fp.setEmbarkationCountry(f.getOriginCountry());
                fp.setPortOfFirstArrival(f.getDestination());
                fp.setMessageSource("PNR");
                fp.setFlightId(f.getId());
                fp.setResidenceCountry(p.getPassengerDetails().getResidencyCountry());
                fp.setTravelerType(p.getPassengerDetails().getPassengerType());
                fp.setPassengerId(p.getId());
                fp.setReservationReferenceNumber(p.getPassengerTripDetails().getReservationReferenceNumber());
                if (StringUtils.isNotBlank(fp.getDebarkation()) && StringUtils.isNotBlank(fp.getEmbarkation())) {
                    if (homeAirport.equalsIgnoreCase(fp.getDebarkation()) || homeAirport.equalsIgnoreCase(fp.getEmbarkation())) {
                        p.getPassengerTripDetails().setTravelFrequency(p.getPassengerTripDetails().getTravelFrequency() + 1);
                    }
                }
                paxRecords.add(fp);
            }
        }
        flightPaxRepository.saveAll(paxRecords);
    }

    private Set<Bag> createBagInformation(PnrVo pvo, Pnr pnr, Flight primeFlight) {

        Set<Bag> passengerBags = new HashSet<>();
        for (Passenger p : pnr.getPassengers()) {
            passengerBags.addAll(p.getBags());
        }

        BagInformationDTO bagInformationDTO = loaderRepo.handleDuplicateBags(pvo.getBags(), passengerBags);
        Set<BagVo> bagVoList = bagInformationDTO.getNewBags();
        Set<BagMeasurementsVo> bagMeasurementsToSave = bagVoList.stream().map(BagVo::getBagMeasurementsVo).collect(Collectors.toSet());
        Map<UUID, BagMeasurements> uuidBagMeasurementsMap = loaderRepo.saveBagMeasurements(bagMeasurementsToSave);
        Map<UUID, UUID> orphanToBD = getOrphanMap(pvo, pnr);
        Map<UUID, BookingDetail> uuidBookingDetailMap = pnr.getBookingDetails()
                .stream()
                .collect(Collectors.toMap(BookingDetail::getParserUUID, bd -> bd));


        Set<Bag> newBags = makeNewBags(pnr, primeFlight, bagVoList, uuidBagMeasurementsMap);
        bagDao.saveAll(newBags);
        Set<Bag> allBags = new HashSet<>(bagInformationDTO.getExistingBags());
        allBags.addAll(newBags);


        for (Bag bag : allBags ) {
            for (UUID flightUUID : bag.getFlightVoUUID()) {
                if (uuidBookingDetailMap.containsKey(flightUUID)) {
                    BookingDetail bookingDetail = uuidBookingDetailMap.get(flightUUID);
                    bookingDetail.getBags().add(bag);
                } else if (orphanToBD.containsKey(flightUUID)) {
                    UUID bookingDetailUUID = orphanToBD.get(flightUUID);
                    BookingDetail bookingDetail = uuidBookingDetailMap.get(bookingDetailUUID);
                    bookingDetail.getBags().add(bag);
                } else if (!flightUUID.equals(primeFlight.getParserUUID())) {
                    logger.warn("No connection to booking detail can be made!");
                }
            }
        }
        //Save relationship to booking details.
        bookingDetailDao.saveAll(pnr.getBookingDetails());
        return allBags;
    }

    /*
     * Converts bagVo into a bag, creates relationship with BD as appropriate, toggles as prime flight where appriorate.
     * Returns list of bags.
     * */
    private Set<Bag> makeNewBags(Pnr pnr,
                                  Flight primeFlight,
                                  Set<BagVo> bagVoList,
                                  Map<UUID, BagMeasurements> uuidBagMeasurementsMap ) {
        Set<Bag> bagList = new HashSet<>();
        for (BagVo b : bagVoList) {
            for (Passenger p : pnr.getPassengers()) {
                if (p.getParserUUID().equals(b.getPassengerId()) && b.getBagId() != null) {
                    Bag bag = new Bag();
                    bag.setBagId(b.getBagId());
                    bag.setAirline(b.getAirline());
                    bag.setData_source(b.getData_source());
                    bag.setDestinationAirport(b.getDestinationAirport());
                    Airport airport = utils.getAirport(b.getDestinationAirport());
                    if (airport != null) {
                        bag.setCountry(airport.getCountry());
                    }
                    bag.setHeadPool(b.isHeadPool());
                    bag.setMemberPool(b.isMemberPool());
                    bag.setBagSerialCount(b.getConsecutiveTagNumber());
                    bag.setFlight(primeFlight);
                    bag.setPassenger(p);
                    bag.setBagMeasurements(uuidBagMeasurementsMap.get(b.getBagMeasurementUUID()));
                    bag.setPrimeFlight(b.isPrimeFlight());
                    bag.getFlightVoUUID().addAll(b.getFlightVoId());
                    primeFlight.getBags().add(bag);
                    bagList.add(bag);
                    p.getBags().add(bag);
                }
            }
        }
        return bagList;
    }

    private Map<UUID, UUID> getOrphanMap(PnrVo pvo, Pnr pnr) {
        Map<UUID, UUID> orphanToBD = new HashMap<>();
        if (pnr.getBookingDetails().size() < pvo.getFlights().size() - 1) {
            Set<FlightVo> orphanedFlightVo = new HashSet<>();
            Set<UUID> bookingDetails = pnr.getBookingDetails().stream().map(BookingDetail::getParserUUID).collect(Collectors.toSet());
            for (FlightVo flightVo : pvo.getFlights()) {
                if (!bookingDetails.contains(flightVo.getUuid())) {
                    orphanedFlightVo.add(flightVo);
                }
            }
            for (FlightVo orphan : orphanedFlightVo) {
                for (BookingDetail bookingDetail : pnr.getBookingDetails()) {
                    if (orphan.equalsThisBD(bookingDetail)) {
                        if (!orphanToBD.containsKey(orphan.getUuid())) {
                            orphanToBD.put(orphan.getUuid(), bookingDetail.getParserUUID());
                        }
                    }
                }
            }
        }
        return orphanToBD;
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