/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.*;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.tamr.TamrConversionService;
import gov.gtas.parsers.vo.AddressVo;
import gov.gtas.parsers.vo.AgencyVo;
import gov.gtas.parsers.vo.BagVo;
import gov.gtas.parsers.vo.CodeShareVo;
import gov.gtas.parsers.vo.CreditCardVo;
import gov.gtas.parsers.vo.DocumentVo;
import gov.gtas.parsers.vo.EmailVo;
import gov.gtas.parsers.vo.FlightVo;
import gov.gtas.parsers.vo.FrequentFlyerVo;
import gov.gtas.parsers.vo.PassengerVo;
import gov.gtas.parsers.vo.PaymentFormVo;
import gov.gtas.parsers.vo.PhoneVo;
import gov.gtas.parsers.vo.PnrVo;
import gov.gtas.parsers.vo.ReportingPartyVo;
import gov.gtas.parsers.vo.SeatVo;
import gov.gtas.repository.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class GtasLoaderImpl implements GtasLoader {
    private static final Logger logger = LoggerFactory
            .getLogger(GtasLoaderImpl.class);

    @Autowired
    private ReportingPartyRepository rpDao;

    @Autowired
    private FlightRepository flightDao;

    @Autowired
    private PassengerRepository passengerDao;

    @Autowired
    private DocumentRepository docDao;

    @Autowired
    private PhoneRepository phoneDao;

    @Autowired
    private CreditCardRepository creditDao;

    @Autowired
    private AddressRepository addressDao;

    @Autowired
    private AgencyRepository agencyDao;

    @Autowired
    private MessageRepository<Message> messageDao;

    @Autowired
    private FrequentFlyerRepository ffdao;

    @Autowired
    private LoaderUtils utils;

    @Autowired
    private BagRepository bagDao;

    @Autowired
    private PaymentFormRepository paymentFormDao;

    @Autowired
    private PassengerIDTagRepository passengerIdTagDao;

    @Autowired
    BookingDetailRepository bookingDetailDao;

    @Autowired
    BookingDetailService bookingDetailService;

    @Autowired
    LoaderServices loaderServices;

    @Autowired
    PnrRepository pnrDao;

    @Autowired
    FlightService flightService;

    @Autowired
    TamrConversionService tamrService;

    @Autowired
    FlightPassengerRepository flightPassengerRepository;

    @Autowired
    FlightPassengerCountRepository flightPassengerCountRepository;


    @Autowired
    FlightPaxRepository flightPaxRepository;


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
            ReportingParty existingRp = rpDao.getReportingParty(rvo.getPartyName(), rvo.getTelephone());
            if (existingRp == null) {
                ReportingParty newRp = utils.createNewReportingParty(rvo);
                apisMessage.getReportingParties().add(newRp);
            } else {
                utils.updateReportingParty(rvo, existingRp);
                apisMessage.addReportingParty(existingRp);
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
                    logger.info("Flight Not Found: Creating Flight");
                    currentFlight = utils.createNewFlight(fvo);
                    flightDao.save(currentFlight);
                    logger.info("Flight Created: " + fvo.getFlightNumber());
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
            throw new RuntimeException("oh noes!");
        }
        return primeFlight;
    }

    @Override
    public Set<Passenger> makeNewPassengerObjects(Flight primeFlight, List<PassengerVo> passengers,
                                                  Set<Passenger> messagePassengers,
                                                  Set<BookingDetail> bookingDetails,
                                                  Message message) throws ParseException {

        Set<Passenger> newPassengers = new HashSet<>();
        for (PassengerVo pvo : passengers) {
            Passenger existingPassenger = loaderServices.findPassengerOnFlight(primeFlight, pvo);
            if (existingPassenger != null) {
                updatePassenger(existingPassenger, pvo);
                messagePassengers.add(existingPassenger);
                logger.debug("@ createSeatAssignment");
                createSeatAssignment(pvo.getSeatAssignments(), existingPassenger, primeFlight);
                logger.debug("@ createBags");
                createBags(pvo.getBags(), existingPassenger, primeFlight);
            } else {
                Passenger newPassenger = utils.createNewPassenger(pvo);
                for (DocumentVo dvo : pvo.getDocuments()) {
                    newPassenger.addDocument(utils.createNewDocument(dvo));
                }
                createSeatAssignment(pvo.getSeatAssignments(), newPassenger, primeFlight);
                createBags(pvo.getBags(), newPassenger, primeFlight);
                utils.calculateValidVisaDays(primeFlight, newPassenger);
                newPassengers.add(newPassenger);
                messagePassengers.add(newPassenger);
            }
        }
        return newPassengers;
    }

    @Override
    @Transactional()
    public int createPassengers(Set<Passenger> newPassengers, Set<Passenger> messagePassengers, Flight primeFlight, Set<BookingDetail> bookingDetails) {
        List<PassengerIDTag> passengerIDTags = new ArrayList<>();

        passengerDao.save(messagePassengers);
        for (Passenger p : newPassengers) {
            PassengerIDTag paxIdTag = utils.createPassengerIDTag(p);
            passengerIDTags.add(paxIdTag);
        }

        Set<FlightPassenger> flightPassengers = new HashSet<>();
        for (Passenger p : newPassengers) {
            FlightPassenger fp = new FlightPassenger();
            fp.setPassengerId(p.getId().toString());
            fp.setFlightId(primeFlight.getId().toString());
            flightPassengers.add(fp);
        }

        passengerIdTagDao.save(passengerIDTags);
        flightPassengerRepository.save(flightPassengers);
        return newPassengers.size();
    }

    @Transactional
    public void updateFlightPassengerCount(Flight primeFlight, int createdPassengers) {
        FlightPassengerCount flightPassengerCount = flightPassengerCountRepository.findOne(primeFlight.getId());
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
    public void createBookingDetails(Pnr pnr) {
        Set<BookingDetail> bookingDetails = pnr.getBookingDetails();
        Set<Passenger> messagePassengers = pnr.getPassengers();
        if (!bookingDetails.isEmpty()) {
            for (BookingDetail bD : bookingDetails) {
                bD.getPnrs().add(pnr);
                for (Passenger pax : messagePassengers) {
                    if (!pax.getBookingDetails().contains(bD)) {
                        bD.getPassengers().add(pax);
                    }
                }
            }
        }
        bookingDetailDao.save(bookingDetails);
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
        paymentFormDao.save(chkList);
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
