/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.*;
import gov.gtas.model.lookup.Airport;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.util.DateUtils;
import gov.gtas.parsers.vo.*;
import gov.gtas.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static gov.gtas.services.CaseDispositionServiceImpl.getNullPropertyNames;


@Service
public class GtasLoaderImpl implements GtasLoader {
    private static final Logger logger = LoggerFactory
            .getLogger(GtasLoaderImpl.class);
    public static final int ETD_DATE_WITH_TIMESTAMP = 5;
    public static final int ETD_DATE_NO_TIMESTAMP_AS_LONG = 4;
    public static final int PRIME_FLIGHT_NUMBER_STRING = 3;
    public static final int PRIME_FLIGHT_CARRIER = 2;
    public static final int PRIME_FLIGHT_DESTINATION = 1;
    public static final int PRIME_FLIGHT_ORIGIN = 0;

    private final ReportingPartyRepository rpDao;

    private final FlightRepository flightDao;

    private final PassengerRepository passengerDao;

    private final DocumentRepository docDao;

    private final PhoneRepository phoneDao;

    private final CreditCardRepository creditDao;

    private final AddressRepository addressDao;

    private final AgencyRepository agencyDao;

    private final MessageRepository<Message> messageDao;

    private final FrequentFlyerRepository ffdao;

    private final LoaderUtils utils;

    private final BagRepository bagDao;

    private final BagMeasurementsRepository bagMeasurementsRepository;

    private final PaymentFormRepository paymentFormDao;

    private final PassengerIDTagRepository passengerIdTagDao;

    private final
    BookingDetailRepository bookingDetailDao;

    private final
    LoaderServices loaderServices;

    private final
    FlightPassengerRepository flightPassengerRepository;

    private final
    FlightPassengerCountRepository flightPassengerCountRepository;

    private final
    PassengerTripRepository passengerTripRepository;

    private final
    PassengerDetailRepository passengerDetailRepository;

    private final
    MutableFlightDetailsRepository mutableFlightDetailsRepository;


    private final LoaderUtils loaderUtils;


    @Autowired
    public GtasLoaderImpl(
            PassengerRepository passengerDao,
            ReportingPartyRepository rpDao,
            LoaderServices loaderServices,
            FlightRepository flightDao,
            DocumentRepository docDao,
            PhoneRepository phoneDao,
            PaymentFormRepository paymentFormDao,
            CreditCardRepository creditDao,
            FlightPassengerCountRepository flightPassengerCountRepository,
            AddressRepository addressDao,
            AgencyRepository agencyDao,
            BagRepository bagDao,
            MessageRepository<Message> messageDao,
            PassengerIDTagRepository passengerIdTagDao,
            FlightPassengerRepository flightPassengerRepository,
            FrequentFlyerRepository ffdao,
            LoaderUtils utils,
            BookingDetailRepository bookingDetailDao,
            PassengerTripRepository passengerTripRepository,
            PassengerDetailRepository passengerDetailRepository,
            MutableFlightDetailsRepository mutableFlightDetailsRepository,
            BagMeasurementsRepository bagMeasurementsRepository,
            LoaderUtils loaderUtils) {
        this.passengerDao = passengerDao;
        this.rpDao = rpDao;
        this.loaderServices = loaderServices;
        this.flightDao = flightDao;
        this.docDao = docDao;
        this.phoneDao = phoneDao;
        this.paymentFormDao = paymentFormDao;
        this.creditDao = creditDao;
        this.flightPassengerCountRepository = flightPassengerCountRepository;
        this.addressDao = addressDao;
        this.agencyDao = agencyDao;
        this.bagDao = bagDao;
        this.messageDao = messageDao;
        this.passengerIdTagDao = passengerIdTagDao;
        this.flightPassengerRepository = flightPassengerRepository;
        this.ffdao = ffdao;
        this.utils = utils;
        this.bookingDetailDao = bookingDetailDao;
        this.passengerTripRepository = passengerTripRepository;
        this.passengerDetailRepository = passengerDetailRepository;
        this.mutableFlightDetailsRepository = mutableFlightDetailsRepository;
        this.bagMeasurementsRepository = bagMeasurementsRepository;
        this.loaderUtils = loaderUtils;
    }


    @Override
    public void checkHashCode(String hash) throws LoaderException {
        Message m = messageDao.findByHashCode(hash);
        if (m != null) {
            throw new LoaderException("duplicate message hashcode: " + hash);
        }
    }

    @Override
    public void processReportingParties(ApisMessage apisMessage, List<ReportingPartyVo> parties) {
        for (ReportingPartyVo rvo : parties) {
            List<ReportingParty> existingRp = rpDao.getReportingParty(rvo.getPartyName(), rvo.getTelephone());
            if (existingRp.isEmpty()) {
                ReportingParty newRp = utils.createNewReportingParty(rvo);
                apisMessage.getReportingParties().add(newRp);
            } else {
                utils.updateReportingParty(rvo, existingRp.get(0));
                apisMessage.addReportingParty(existingRp.get(0));
            }
        }
    }

    @Override
    public void processPnr(Pnr pnr, PnrVo vo) throws ParseException {
        logger.debug("@ processPnr");
        long startTime = System.nanoTime();
        Long flightId = pnr.getFlights().iterator().next().getId();
        for (AddressVo addressVo : vo.getAddresses()) {
            List<Address> existingAddress = addressDao.findByLine1AndCityAndStateAndPostalCodeAndCountryAndFlightId(
                    addressVo.getLine1(), addressVo.getCity(), addressVo.getState(), addressVo.getPostalCode(), addressVo.getCountry(), flightId);
            if (existingAddress.isEmpty()) {
                Address address = utils.convertAddressVo(addressVo);
                address.setFlightId(flightId);
                pnr.addAddress(address);
            } else {
                pnr.addAddress(existingAddress.get(0));
            }
        }

        for (PhoneVo phoneVo : vo.getPhoneNumbers()) {
            List<Phone> existingPhone = phoneDao.findByNumberAndFlightId(phoneVo.getNumber(), flightId);
            if (existingPhone.isEmpty()) {
                Phone newPhone = utils.convertPhoneVo(phoneVo);
                newPhone.setFlightId(flightId);
                pnr.addPhone(newPhone);
            } else {
                pnr.addPhone(existingPhone.get(0));
            }
        }

        for (CreditCardVo creditVo : vo.getCreditCards()) {
            List<CreditCard> existingCard = creditDao.findByCardTypeAndNumberAndExpirationAndFlightId(creditVo.getCardType(), creditVo.getNumber(), creditVo.getExpiration(), flightId);
            if (existingCard.isEmpty()) {
                CreditCard newCard = utils.convertCreditVo(creditVo);
                newCard.setFlightId(flightId);
                pnr.addCreditCard(newCard);
            } else {
                pnr.addCreditCard(existingCard.get(0));
            }
        }

        for (FrequentFlyerVo ffvo : vo.getFrequentFlyerDetails()) {
            List<FrequentFlyer> existingFf = ffdao.findByCarrierAndNumber(ffvo.getCarrier(), ffvo.getNumber());
            if (existingFf.isEmpty()) {
                FrequentFlyer newFf = utils.convertFrequentFlyerVo(ffvo);
                pnr.addFrequentFlyer(newFf);
            } else {
                pnr.addFrequentFlyer(existingFf.get(0));
            }
        }

        for (AgencyVo avo : vo.getAgencies()) {
            List<Agency> existingAgency = agencyDao.findByNameAndLocation(avo.getName(), avo.getLocation());
            if (existingAgency.isEmpty()) {
                Agency newAgency = utils.convertAgencyVo(avo);
                newAgency.setCity(newAgency.getCity() != null ? newAgency.getCity().toUpperCase() : newAgency.getCity());
                pnr.addAgency(newAgency);
            } else {
                pnr.addAgency(existingAgency.get(0));
            }
        }
        for (EmailVo evo : vo.getEmails()) {
            Email email = utils.convertEmailVo(evo);
            pnr.addEmail(email);
        }
        for (CodeShareVo cso : vo.getCodeshares()) {
            CodeShareFlight cs = utils.convertCodeShare(cso);
            cs.getPnrs().add(pnr);
            pnr.getCodeshares().add(cs);
        }
        logger.debug("processPnr time= " + (System.nanoTime() - startTime) / 1000000);
    }

    @Override
    public Flight processFlightsAndBookingDetails(List<FlightVo> flights, Set<Flight> messageFlights, List<FlightLeg> flightLegs,
                                                  String[] primeFlightKey, Set<BookingDetail> bookingDetails) throws ParseException {

        long startTime = System.nanoTime();
        /*
         * If pnrVo flights on tvl level deos not 5 exist then make
         * the tvl level 0 the prime flight.
         *
         * */
        if (flights.isEmpty()) {
            FlightVo flightVo = new FlightVo();
            Date primeFlightDate = new Date(Long.parseLong(primeFlightKey[ETD_DATE_NO_TIMESTAMP_AS_LONG]));
            flightVo.setEta(primeFlightDate);
            flightVo.setCarrier(primeFlightKey[PRIME_FLIGHT_CARRIER]);
            flightVo.setFlightNumber(primeFlightKey[PRIME_FLIGHT_NUMBER_STRING]);
            flightVo.setOrigin(primeFlightKey[PRIME_FLIGHT_ORIGIN]);
            flightVo.setDestination(primeFlightKey[PRIME_FLIGHT_DESTINATION]);
            flights.add(flightVo);
        }

        // save flight and booking details
        // return flight and booking details
        // first find all existing passengers, create any missing flights
        utils.sortFlightsByDate(flights);
        Flight primeFlight = null;
        for (int i = 0; i < flights.size(); i++) {
            FlightVo fvo = flights.get(i);
            /*
            * A prime flight is determined by the level 0 TVL of a PNR or a combination
            * of fields in an APIS.
            * The isPrimeFlight will check to see if the flightVo being processed matches the prime flight
            * and set the flight accordingly.
            * */
            if (utils.isPrimeFlight(fvo, primeFlightKey)) {
                Date primeFlightDate = new Date(Long.parseLong(primeFlightKey[ETD_DATE_NO_TIMESTAMP_AS_LONG]));
                Flight currentFlight = flightDao.getFlightByCriteria(primeFlightKey[PRIME_FLIGHT_CARRIER],
                                                                     primeFlightKey[PRIME_FLIGHT_NUMBER_STRING],
                                                                     primeFlightKey[PRIME_FLIGHT_ORIGIN],
                                                                     primeFlightKey[PRIME_FLIGHT_DESTINATION],
                                                                     primeFlightDate);
                if (currentFlight == null) {
                    logger.debug("Flight Not Found: Creating Flight");
                    currentFlight = utils.createNewFlight(fvo, primeFlightKey);
                    currentFlight = flightDao.save(currentFlight);
                    logger.info("Flight Created: Flight Number:" + fvo.getFlightNumber() + " with ID " + currentFlight.getId());
                }
                /*
                * Update the information on a flight that can change. Always save the most recent one as it will contain the
                * most up to date information.
                * */
                MutableFlightDetails mfd = mutableFlightDetailsRepository.findById(currentFlight.getId()).orElse(new MutableFlightDetails(currentFlight.getId()));
                BeanUtils.copyProperties(fvo, mfd, getNullPropertyNames(fvo));
                mfd.setEtaDate(DateUtils.stripTime(mfd.getEta()));
                if (mfd.getEtd() == null) {
                    //Special case where flight doesnt have a timestamp - use the prime flight's timestamp if this is the case.
                    Date primeFlightTimeStamp = new Date(Long.parseLong(primeFlightKey[ETD_DATE_WITH_TIMESTAMP]));
                    mfd.setEtd(primeFlightTimeStamp);
                }
                mfd = mutableFlightDetailsRepository.save(mfd);
                currentFlight.setMutableFlightDetails(mfd);
                primeFlight = currentFlight;
                primeFlight.setParserUUID(fvo.getUuid());
                logger.debug("processFlightsAndPassenger: check for existing flights time= " + (System.nanoTime() - startTime) / 1000000);
                messageFlights.add(currentFlight);
                FlightLeg leg = new FlightLeg();
                leg.setFlight(currentFlight);
                leg.setLegNumber(i);
                flightLegs.add(leg);
            } else {
                // All flights that are not prime flights are considered booking details.
                BookingDetail bD = utils.convertFlightVoToBookingDetail(fvo);
                bD = bookingDetailDao.save(bD);
                bD.setParserUUID(fvo.getUuid());
                bookingDetails.add(bD);
                FlightLeg leg = new FlightLeg();
                leg.setBookingDetail(bD);
                leg.setLegNumber(i);
                flightLegs.add(leg);
            }
        }
        if (primeFlight == null) {
            throw new RuntimeException("No prime flight. ERROR!!!!!");
        }
        return primeFlight;
    }


    @Override
    public PassengerInformationDTO makeNewPassengerObjects(Flight primeFlight, List<PassengerVo> passengers,
                                                           Set<Passenger> messagePassengers,
                                                           Set<BookingDetail> bookingDetails,
                                                           Message message) throws ParseException {

        Set<Passenger> newPassengers = new HashSet<>();
        Set<Passenger> oldPassengers = new HashSet<>();
        Set<Long> oldPassengersId = new HashSet<>();
        List<PassengerDetails> passengerDetailsList = new ArrayList<>();
        List<PassengerTripDetails> passengerTripDetails = new ArrayList<>();
        Map<Long, Set<BookingDetail>> bookingDetailsAPassengerOwns = new HashMap<>();
        for (PassengerVo pvo : passengers) {
            Passenger existingPassenger =  loaderServices.findPassengerOnFlight(primeFlight, pvo);
            if (existingPassenger == null ) {
                Passenger newPassenger = utils.createNewPassenger(pvo);
                newPassenger.setParserUUID(pvo.getUuid());
                for (DocumentVo dvo : pvo.getDocuments()) {
                    newPassenger.addDocument(utils.createNewDocument(dvo));
                }
                createSeatAssignment(pvo.getSeatAssignments(), newPassenger, primeFlight);
                createBags(pvo.getBags(), newPassenger, primeFlight);
                utils.calculateValidVisaDays(primeFlight, newPassenger);
                newPassengers.add(newPassenger);
            } else if (!oldPassengersId.contains(existingPassenger.getId())) {
                existingPassenger.setParserUUID(pvo.getUuid());
                bookingDetailsAPassengerOwns.put(existingPassenger.getId(), existingPassenger.getBookingDetails());
                oldPassengersId.add(existingPassenger.getId());
                updatePassenger(existingPassenger, pvo);
                messagePassengers.add(existingPassenger);
                logger.debug("@ createSeatAssignment");
                createSeatAssignment(pvo.getSeatAssignments(), existingPassenger, primeFlight);
                logger.debug("@ createBags");
                createBags(pvo.getBags(), existingPassenger, primeFlight);
                oldPassengers.add(existingPassenger);
                passengerDetailsList.add(existingPassenger.getPassengerDetails());
                passengerTripDetails.add(existingPassenger.getPassengerTripDetails());
            }
        }

        passengerTripRepository.saveAll(passengerTripDetails);
        passengerDetailRepository.saveAll(passengerDetailsList);
        messagePassengers.addAll(oldPassengers);
        PassengerInformationDTO passengerInformationDTO = new PassengerInformationDTO();
        passengerInformationDTO.setBdSet(bookingDetailsAPassengerOwns);
        passengerInformationDTO.setNewPax(newPassengers);
        return passengerInformationDTO;
    }

    @Override
    public int createPassengers(Set<Passenger> newPassengers, Set<Passenger> oldSet, Set<Passenger> messagePassengers, Flight primeFlight, Set<BookingDetail> bookingDetails) {
        List<PassengerIDTag> passengerIDTags = new ArrayList<>();

        passengerDao.saveAll(newPassengers);
        messagePassengers.addAll(newPassengers);
        for (Passenger p : newPassengers) {
            try {
                PassengerIDTag paxIdTag = utils.createPassengerIDTag(p);
                passengerIDTags.add(paxIdTag);
            } catch (Exception ignored) {
                logger.error("Failed to make a pax hash id from pax with id " + p.getId() + ". Passenger lacks fname, lname, gender, or dob. ");
            }
        }

        Set<FlightPassenger> flightPassengers = new HashSet<>();
        for (Passenger p : newPassengers) {
            FlightPassenger fp = new FlightPassenger();
            fp.setPassengerId(p.getId());
            fp.setFlightId(primeFlight.getId());
            flightPassengers.add(fp);
        }

        passengerIdTagDao.saveAll(passengerIDTags);
        flightPassengerRepository.saveAll(flightPassengers);
        return newPassengers.size();
    }

    public void updateFlightPassengerCount(Flight primeFlight, int createdPassengers) {
        FlightPassengerCount flightPassengerCount = flightPassengerCountRepository.findById(primeFlight.getId())
                .orElse(null);
        if (flightPassengerCount == null) {
            flightPassengerCount = new FlightPassengerCount(primeFlight.getId(), createdPassengers);
        } else {
            int currentPassengers = flightPassengerCount.getPassengerCount();
            flightPassengerCount.setPassengerCount(currentPassengers + createdPassengers);
        }
        flightPassengerCountRepository.save(flightPassengerCount);
    }


    @Override
    public void createBookingDetails(Pnr pnr, Map<Long, Set<BookingDetail>> paxBookingDetailsMap) {
        Set<BookingDetail> bookingDetails = pnr.getBookingDetails();
        Set<Passenger> messagePassengers = pnr.getPassengers();
        if (!bookingDetails.isEmpty()) {
            for (BookingDetail bD : bookingDetails) {
                bD.getPnrs().add(pnr);
                for (Passenger pax : messagePassengers) {
                    Set<BookingDetail> paxBdSet = paxBookingDetailsMap.get(pax.getId());
                    if (paxBdSet == null || !paxBookingDetailsMap.get(pax.getId()).contains(bD)) {
                        bD.getPassengers().add(pax);
                    }
                }
            }
        }
        bookingDetailDao.saveAll(bookingDetails);
    }


    /**
     * Create a single seat assignment for the given passenger, flight
     * combination. TODO: Inefficient to have to pass in the entire list of seat
     * assignments from the paxVo.
     *
     * @param seatAssignments seatAssignment Value Object
     * @param p               Passenger to add seat to
     * @param f               Flight passenger is on.
     */
    private void createSeatAssignment(List<SeatVo> seatAssignments, Passenger p, Flight f) {
        for (SeatVo seat : seatAssignments) {
            if (seat.getDestination().equals(f.getDestination())
                    && seat.getOrigin().equals(f.getOrigin())) {
                Seat s = new Seat();
                s.setPassenger(p);
                s.setFlight(f);
                s.setNumber(seat.getNumber());
                s.setPaxId(p.getId());
                s.setApis(seat.getApis());
                Boolean alreadyExistsSeat = Boolean.FALSE;
                for (Seat s2 : p.getSeatAssignments()) {
                    if (s.equals(s2)) {
                        alreadyExistsSeat = Boolean.TRUE;
                    }
                }
                if (!alreadyExistsSeat) {
                    p.getSeatAssignments().add(s);
                }
                return;
            }
        }
    }

    private void createBags(List<String> bagIds, Passenger p, Flight f) {
        for (String bagId : bagIds) {
            Bag bag = new Bag();
            bag.setBagId(bagId);
            bag.setData_source("APIS");
            //APIS Tab | Remove Baggage destination #657 code fix
            //bag.setDestinationAirport(f.getDestination());
            bag.setAirline(f.getCarrier());
            bag.setFlight(f);
            bag.setPassenger(p);
            p.getBags().add(bag);
        }
    }

    @Override
    public void createBagInformation(PnrVo pvo, Pnr pnr, Flight primeFlight) {

        List<BagVo> bagVoList = handleDuplicateBags(pvo);
        List<BagMeasurementsVo> bagMeasurementsToSave = bagVoList.stream().map(BagVo::getBagMeasurementsVo).collect(Collectors.toList());

        Map<UUID, UUID> orphanToBD = getOrphanMap(pvo, pnr);
        Map<UUID, BagMeasurements> uuidBagMeasurementsMap = saveBagMeasurements(bagMeasurementsToSave);
        Map<UUID, BookingDetail> uuidBookingDetailMap = pnr.getBookingDetails()
                .stream()
                .collect(Collectors.toMap(BookingDetail::getParserUUID, bd -> bd));

        for (BagVo b : bagVoList) {
            for (Passenger p : pnr.getPassengers()) {
                if (p.getParserUUID().equals(b.getPassengerId()) && b.getBagId() != null) {
                    Bag bag = new Bag();
                    bag.setBagId(b.getBagId());
                    bag.setAirline(b.getAirline());
                    bag.setData_source(b.getData_source());
                    bag.setDestinationAirport(b.getDestinationAirport());
                    Airport airport = loaderUtils.getAirport(b.getDestinationAirport());
                    if (airport != null) {
                        bag.setCountry(airport.getCountry());
                    }
                    bag.setHeadPool(b.isHeadPool());
                    bag.setBagSerialCount(b.getConsecutiveTagNumber());
                    bag.setFlight(primeFlight);
                    bag.setPassenger(p);
                    bag.setBagMeasurements(uuidBagMeasurementsMap.get(b.getBagMeasurementUUID()));

                    for (UUID flightUUID : b.getFlightVoId()) {
                        if (uuidBookingDetailMap.containsKey(flightUUID)) {
                            bag.getBookingDetail().add(uuidBookingDetailMap.get(flightUUID));
                        } else if (flightUUID.equals(primeFlight.getParserUUID())) {
                            bag.setPrimeFlight(true);
                        } else if (orphanToBD.containsKey(flightUUID)) {
                            UUID bookingDetailUUID = orphanToBD.get(flightUUID);
                            bag.getBookingDetail().add(uuidBookingDetailMap.get(bookingDetailUUID));
                        } else {
                            logger.warn("Could not find a place to put the bag!");
                        }
                    }
                    bagDao.save(bag);
                }
            }
        }
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

    private Map<UUID, BagMeasurements> saveBagMeasurements(List<BagMeasurementsVo> bagMeasurementsToSave) {
        Map<UUID, BagMeasurements> uuidBagMeasurementsMap = new HashMap<>();
        for (BagMeasurementsVo bagMeasurementsVo : bagMeasurementsToSave) {
            BagMeasurements bagMeasurements = new BagMeasurements();
            bagMeasurements.setBagCount(bagMeasurementsVo.getQuantity());
            bagMeasurements.setWeight(bagMeasurementsVo.getWeight());
            bagMeasurements.setParserUUID(bagMeasurementsVo.getUuid());
            uuidBagMeasurementsMap.put(bagMeasurements.getParserUUID(), bagMeasurements);
            bagMeasurementsRepository.save(bagMeasurements);
        }
        return uuidBagMeasurementsMap;
    }

    private List<BagVo> handleDuplicateBags(PnrVo pvo) {
        List<BagVo> bagVoList = pvo.getBags();
        //Prime flight bags take priority and get merged into.
        bagVoList.sort(Comparator.comparing(BagVo::isPrimeFlight).reversed());
        for (BagVo bagvo : new ArrayList<>(bagVoList)) {
            for (BagVo secondBag : new ArrayList<>(bagVoList)) {
                if (bagvo != secondBag && isSameBag(bagvo, secondBag)) {
                    bagvo.getFlightVoId().addAll(secondBag.getFlightVoId());
                    secondBag.setFlightVoId(new HashSet<>());
                    secondBag.setBagMeasurementsVo(new BagMeasurementsVo());
                    secondBag.setConsecutiveTagNumber("");
                    secondBag.setBagTagIssuerCode("");
                    secondBag.setPassengerId(null);
                    bagVoList.remove(secondBag);
                }
            }
        }
        return bagVoList;
    }

    private boolean isSameBag(BagVo bagvo, BagVo secondBag) {
        return
           ((StringUtils.isBlank(bagvo.getConsecutiveTagNumber()) && StringUtils.isBlank(secondBag.getConsecutiveTagNumber()))
                   || (bagvo.getConsecutiveTagNumber() != null && bagvo.getConsecutiveTagNumber().equals(secondBag.getConsecutiveTagNumber())))
           &&(StringUtils.isNotBlank(bagvo.getBagId()) && bagvo.getBagId().equals(secondBag.getBagId()))
           && ((bagvo.getBagMeasurementsVo() == null && secondBag.getBagMeasurementsVo() == null)
                || (bagvo.getBagMeasurementsVo().getWeight() == secondBag.getBagMeasurementsVo().getWeight()
                    && bagvo.getBagMeasurementsVo().getQuantity() == secondBag.getBagMeasurementsVo().getQuantity()))
           && bagvo.getPassengerId() == secondBag.getPassengerId();
    }


    @Override
    public void createFormPfPayments(PnrVo vo, Pnr pnr) {
        Set<PaymentForm> chkList = new HashSet<>();
        for (PaymentFormVo pvo : vo.getFormOfPayments()) {
            PaymentForm pf = new PaymentForm();
            pf.setPaymentType(pvo.getPaymentType());
            pf.setPaymentAmount(pvo.getPaymentAmount());
            pf.setPnr(pnr);
            chkList.add(pf);
        }
        paymentFormDao.saveAll(chkList);
    }

    @Override
    public void updatePassenger(Passenger existingPassenger, PassengerVo pvo) throws ParseException {
        utils.updatePassenger(pvo, existingPassenger);
        for (DocumentVo dvo : pvo.getDocuments()) {
            Document existingDoc = null;
            if (dvo.getDocumentNumber() != null) {
                existingDoc = docDao.findByDocumentNumberAndPassenger(dvo.getDocumentNumber(), existingPassenger);
            }
            if (existingDoc == null) {
                existingPassenger.addDocument(utils.createNewDocument(dvo));
            } else {
                utils.updateDocument(dvo, existingDoc);
            }
        }
    }
}
