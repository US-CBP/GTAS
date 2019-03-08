/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.*;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.vo.*;
import gov.gtas.repository.*;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
public class GtasLoaderImpl implements GtasLoader {
    private static final Logger logger = LoggerFactory
            .getLogger(GtasLoaderImpl.class);

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
                          BookingDetailRepository bookingDetailDao) {
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
    }


    @Override
    public void checkHashCode(String hash) throws LoaderException {
        Message m = messageDao.findByHashCode(hash);
        if (m != null) {
            throw new LoaderException("duplicate message hashcode: " + hash);
        }
    }

    @Override
    @Transactional
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
    @Transactional
    public void processPnr(Pnr pnr, PnrVo vo) throws ParseException {
        logger.debug("@ processPnr");
        long startTime = System.nanoTime();

        for (AddressVo addressVo : vo.getAddresses()) {
            List<Address> existingAddress = addressDao.findByLine1AndCityAndStateAndPostalCodeAndCountry(
                    addressVo.getLine1(), addressVo.getCity(), addressVo.getState(), addressVo.getPostalCode(), addressVo.getCountry());
            if (existingAddress.isEmpty()) {
                Address address = utils.convertAddressVo(addressVo);
                pnr.addAddress(address);
            } else {
                pnr.addAddress(existingAddress.get(0));
            }
        }

        for (PhoneVo phoneVo : vo.getPhoneNumbers()) {
            List<Phone> existingPhone = phoneDao.findByNumber(phoneVo.getNumber());
            if (existingPhone.isEmpty()) {
                Phone newPhone = utils.convertPhoneVo(phoneVo);
                pnr.addPhone(newPhone);
            } else {
                pnr.addPhone(existingPhone.get(0));
            }
        }

        for (CreditCardVo creditVo : vo.getCreditCards()) {
            List<CreditCard> existingCard = creditDao.findByCardTypeAndNumberAndExpiration(creditVo.getCardType(), creditVo.getNumber(), creditVo.getExpiration());
            if (existingCard.isEmpty()) {
                CreditCard newCard = utils.convertCreditVo(creditVo);
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
    @Transactional
    public Flight processFlightsAndBookingDetails(List<FlightVo> flights, Set<Flight> messageFlights, List<FlightLeg> flightLegs,
                                                  String[] primeFlightKey, Set<BookingDetail> bookingDetails) throws ParseException {

        long startTime = System.nanoTime();
        // save flight and booking details
        // return flight and booking details
        // first find all existing passengers, create any missing flights
        utils.sortFlightsByDate(flights);
        Flight primeFlight = null;
        for (int i = 0; i < flights.size(); i++) {
            FlightVo fvo = flights.get(i);
            if (primeFlightKey[0].equalsIgnoreCase("placeholder") || utils.isPrimeFlight(fvo, primeFlightKey)) { //placeholder is temporary allowance to assist manual running of loader
                logger.debug("@ getFlightByCriteria");
                Flight currentFlight;
                Flight existingFlight = flightDao.getFlightByCriteria(fvo.getCarrier(), fvo.getFlightNumber(), fvo.getOrigin(), fvo.getDestination(), fvo.getFlightDate());
                if (existingFlight == null) {
                    logger.debug("Flight Not Found: Creating Flight");
                    currentFlight = utils.createNewFlight(fvo);
                    flightDao.save(currentFlight);
                    logger.info("Flight Created: Flight Number:" + fvo.getFlightNumber() + " with ID " + currentFlight.getId());
                } else {
                    currentFlight = existingFlight;
                    if (fvo.isCodeShareFlight()) {
                        currentFlight.setOperatingFlight(true);
                    }
                }
                primeFlight = currentFlight;
                logger.debug("processFlightsAndPassenger: check for existing flights time= " + (System.nanoTime() - startTime) / 1000000);
                messageFlights.add(currentFlight);
                FlightLeg leg = new FlightLeg();
                leg.setFlight(currentFlight);
                leg.setLegNumber(i);
                flightLegs.add(leg);
            }//End if prime flight
            else {
                BookingDetail bD = utils.convertFlightVoToBookingDetail(fvo);
                //create booking details for this pnr
                bD = bookingDetailDao.save(bD);
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
    public CreatedAndOldPassengerInformation makeNewPassengerObjects(Flight primeFlight, List<PassengerVo> passengers,
                                                  Set<Passenger> messagePassengers,
                                                  Set<BookingDetail> bookingDetails,
                                                  Message message) throws ParseException {

        Set<Passenger> newPassengers = new HashSet<>();
        Set<Long> oldPassengersId = new HashSet<>();
        Map<Long, Set<BookingDetail>> bookingDetailsAPassengerOwns = new HashMap<>();
        for (PassengerVo pvo : passengers) {
            Passenger existingPassenger = loaderServices.findPassengerOnFlight(primeFlight, pvo);
            if (existingPassenger == null ) {
                Passenger newPassenger = utils.createNewPassenger(pvo);
                for (DocumentVo dvo : pvo.getDocuments()) {
                    newPassenger.addDocument(utils.createNewDocument(dvo));
                }
                createSeatAssignment(pvo.getSeatAssignments(), newPassenger, primeFlight);
                createBags(pvo.getBags(), newPassenger, primeFlight);
                utils.calculateValidVisaDays(primeFlight, newPassenger);
                newPassengers.add(newPassenger);
                messagePassengers.add(newPassenger);
            } else if (!oldPassengersId.contains(existingPassenger.getId())) {
                bookingDetailsAPassengerOwns.put(existingPassenger.getId(), existingPassenger.getBookingDetails());
                oldPassengersId.add(existingPassenger.getId());
                updatePassenger(existingPassenger, pvo);
                messagePassengers.add(existingPassenger);
                logger.debug("@ createSeatAssignment");
                createSeatAssignment(pvo.getSeatAssignments(), existingPassenger, primeFlight);
                logger.debug("@ createBags");
                createBags(pvo.getBags(), existingPassenger, primeFlight);

            }
        }
        CreatedAndOldPassengerInformation createdAndOldPassengerInformation = new CreatedAndOldPassengerInformation();
        createdAndOldPassengerInformation.setBdSet(bookingDetailsAPassengerOwns);
        createdAndOldPassengerInformation.setNewPax(newPassengers);
        return createdAndOldPassengerInformation ;
    }

    @Override
    @Transactional()
    public int createPassengers(Set<Passenger> newPassengers, Set<Passenger> messagePassengers, Flight primeFlight, Set<BookingDetail> bookingDetails) {
        List<PassengerIDTag> passengerIDTags = new ArrayList<>();

        passengerDao.saveAll(messagePassengers);
        for (Passenger p : newPassengers) {
            try {
                PassengerIDTag paxIdTag = utils.createPassengerIDTag(p);
                passengerIDTags.add(paxIdTag);
            } catch (Exception ignored) {
                logger.error("Failed to make a pax id - passenger lacks fname, lname, gender, or dob. ");
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

    @Transactional
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
    @Transactional()
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
    @Transactional
    public void createBagsFromPnrVo(PnrVo pvo, Pnr pnr) {

        for (Flight f : pnr.getFlights()) {
            if (pvo == null || pvo.getBags() == null) {
                break;
            }
            for (BagVo b : pvo.getBags()) {
                String destination = f.getDestination();
                //flight_pax | bag info not making table #783 code fix
                if (b.getDestinationAirport() != null && b.getDestinationAirport().equals(f.getDestination())) {
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(b.getDestinationAirport())) {
                        destination = b.getDestinationAirport();
                    }
                    for (Passenger p : pnr.getPassengers()) {
                        if (StringUtils.equals(p.getFirstName(), b.getFirstName()) &&
                                StringUtils.equals(p.getLastName(), b.getLastName())) {
                            Bag bag = new Bag();
                            bag.setBagId(b.getBagId());
                            bag.setAirline(b.getAirline());
                            bag.setData_source(b.getData_source());
                            bag.setDestinationAirport(destination);
                            bag.setHeadPool(b.isHeadPool());
                            bag.setFlight(f);
                            bag.setPassenger(p);
                            bagDao.save(bag);
                            p.getBags().add(bag);
                        }
                    }
                }
            }
        }
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
            Document existingDoc = docDao.findByDocumentNumberAndPassenger(dvo.getDocumentNumber(), existingPassenger);
            if (existingDoc == null) {
                existingPassenger.addDocument(utils.createNewDocument(dvo));
            } else {
                utils.updateDocument(dvo, existingDoc);
            }
        }
    }
}
