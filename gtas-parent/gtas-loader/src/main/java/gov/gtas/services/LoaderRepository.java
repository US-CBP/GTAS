/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import gov.gtas.model.Address;
import gov.gtas.model.Agency;
import gov.gtas.model.ApisMessage;
import gov.gtas.model.CreditCard;
import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import gov.gtas.model.FlightLeg;
import gov.gtas.model.FrequentFlyer;
import gov.gtas.model.Message;
import gov.gtas.model.Passenger;
import gov.gtas.model.Phone;
import gov.gtas.model.Pnr;
import gov.gtas.model.ReportingParty;
import gov.gtas.model.Seat;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.vo.AddressVo;
import gov.gtas.parsers.vo.AgencyVo;
import gov.gtas.parsers.vo.CreditCardVo;
import gov.gtas.parsers.vo.DocumentVo;
import gov.gtas.parsers.vo.FlightVo;
import gov.gtas.parsers.vo.FrequentFlyerVo;
import gov.gtas.parsers.vo.PassengerVo;
import gov.gtas.parsers.vo.PhoneVo;
import gov.gtas.parsers.vo.PnrVo;
import gov.gtas.parsers.vo.ReportingPartyVo;
import gov.gtas.parsers.vo.SeatVo;
import gov.gtas.repository.AddressRepository;
import gov.gtas.repository.AgencyRepository;
import gov.gtas.repository.CreditCardRepository;
import gov.gtas.repository.DocumentRepository;
import gov.gtas.repository.FlightRepository;
import gov.gtas.repository.FrequentFlyerRepository;
import gov.gtas.repository.MessageRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.repository.PhoneRepository;
import gov.gtas.repository.ReportingPartyRepository;


@Repository
public class LoaderRepository {
    @PersistenceContext 
    private EntityManager entityManager;
    
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
    
    public void checkHashCode(String hash) throws LoaderException {
        Message m = messageDao.findByHashCode(hash);
        if (m != null) {
            throw new LoaderException("duplicate message hashcode: " + hash);
        }
    }

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

    @Transactional
    public void processPnr(Pnr pnr, PnrVo vo) throws ParseException {
        for (AddressVo addressVo : vo.getAddresses()) {
            Address existingAddress = addressDao.findByLine1AndCityAndStateAndPostalCodeAndCountry(
                    addressVo.getLine1(), addressVo.getCity(), addressVo.getState(), addressVo.getPostalCode(), addressVo.getCountry());
            if (existingAddress == null) {
                Address address = utils.convertAddressVo(addressVo);
                pnr.addAddress(address);
            } else {
                pnr.addAddress(existingAddress);                
            }
        }
        
        for (PhoneVo phoneVo : vo.getPhoneNumbers()) {
            Phone existingPhone = phoneDao.findByNumber(phoneVo.getNumber());
            if (existingPhone == null) {
                Phone newPhone = utils.convertPhoneVo(phoneVo);
                pnr.addPhone(newPhone);
            } else {
                pnr.addPhone(existingPhone);                
            }
        }

        for (CreditCardVo creditVo : vo.getCreditCards()) {
            CreditCard existingCard = creditDao.findByCardTypeAndNumberAndExpiration(creditVo.getCardType(), creditVo.getNumber(), creditVo.getExpiration());
            if (existingCard == null) {
                CreditCard newCard  = utils.convertCreditVo(creditVo);
                pnr.addCreditCard(newCard);
            } else {
                pnr.addCreditCard(existingCard);                
            }
        }
        
        for (FrequentFlyerVo ffvo : vo.getFrequentFlyerDetails()) {
            FrequentFlyer existingFf = ffdao.findByCarrierAndNumber(ffvo.getCarrier(), ffvo.getNumber());
            if (existingFf == null) {
                FrequentFlyer newFf = utils.convertFrequentFlyerVo(ffvo);
                pnr.addFrequentFlyer(newFf);
            } else {
                pnr.addFrequentFlyer(existingFf);
            }
        }
        
        for (AgencyVo avo : vo.getAgencies()) {
            Agency existingAgency = agencyDao.findByNameAndLocation(avo.getName(), avo.getLocation());
            if (existingAgency == null) {
                Agency newAgency = utils.convertAgencyVo(avo);
                pnr.addAgency(newAgency);
            } else {
                pnr.addAgency(existingAgency);
            }
        }
    }

    @Transactional
    public void processFlightsAndPassengers(List<FlightVo> flights, List<PassengerVo> passengers, Set<Flight> messageFlights, Set<Passenger> messagePassengers, List<FlightLeg> flightLegs) throws ParseException {
        Set<PassengerVo> existingPassengers = new HashSet<>();
        
        // first find all existing passengers, create any missing flights
        for (int i=0; i<flights.size(); i++) {
            FlightVo fvo = flights.get(i);
            Flight existingFlight = flightDao.getFlightByCriteria(fvo.getCarrier(), fvo.getFlightNumber(), fvo.getOrigin(), fvo.getDestination(), fvo.getFlightDate());
            Flight currentFlight = null;
            if (existingFlight != null) {
                currentFlight = existingFlight;
                for (PassengerVo pvo : passengers) {
                    Passenger existingPassenger = findPassengerOnFlight(existingFlight, pvo);
                    if (existingPassenger != null) {
                        updatePassenger(existingPassenger, pvo);
                        messagePassengers.add(existingPassenger);
                        existingPassengers.add(pvo);
                        createSeatAssignment(pvo.getSeatAssignments(), existingPassenger, existingFlight);
                    }
                }
                
            } else {
                currentFlight = utils.createNewFlight(fvo);
                flightDao.save(currentFlight);
            }
            
            messageFlights.add(currentFlight);
            FlightLeg leg = new FlightLeg();
            leg.setFlight(currentFlight);
            leg.setLegNumber(i);
            flightLegs.add(leg);
        }
               
        // create any new passengers
        for (PassengerVo pvo : passengers) {
            if (existingPassengers.contains(pvo)) {
                continue;
            }
            
            Passenger newPassenger = utils.createNewPassenger(pvo);
            for (DocumentVo dvo : pvo.getDocuments()) {
                newPassenger.addDocument(utils.createNewDocument(dvo));
            }
            passengerDao.save(newPassenger);
            messagePassengers.add(newPassenger);
            
            for (Flight f : messageFlights) {
                createSeatAssignment(pvo.getSeatAssignments(), newPassenger, f);
            }
        }
        
        // assoc all passengers w/ flights, update pax counts
        for (Flight f : messageFlights) {
            for (Passenger p : messagePassengers) {
                f.addPassenger(p);
            }
            
            f.setPassengerCount(f.getPassengers().size());
        }
    }
    
    /**
     * Create a single seat assignment for the given passenger, flight
     * combination. TODO: Inefficient to have to pass in the entire list of seat
     * assignments from the paxVo.
     * 
     * @param seatAssignments
     * @param p
     * @param f
     */
    public void createSeatAssignment(List<SeatVo> seatAssignments, Passenger p, Flight f) {
        for (SeatVo seat : seatAssignments) {
            if (seat.getDestination().equals(f.getDestination())
                && seat.getOrigin().equals(f.getOrigin())) {
                Seat s = new Seat();
                s.setPassenger(p);
                s.setFlight(f);
                s.setNumber(seat.getNumber());
                s.setApis(seat.getApis());
                p.getSeatAssignments().add(s);
                return;
            }
        }
    }

    private void updatePassenger(Passenger existingPassenger, PassengerVo pvo) throws ParseException {
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
    
    /**
     * TODO: update how we find passengers here, use document ,etc
     */
    private Passenger findPassengerOnFlight(Flight f, PassengerVo pvo) {
        if (f.getId() == null) {
            return null;
        }
        
        List<Passenger> pax = passengerDao.getPassengersByFlightIdAndName(f.getId(), pvo.getFirstName(), pvo.getLastName());
        if (pax != null && pax.size() >= 1) {
            return pax.get(0);
        } else {
            return null;
        }
    }
}
