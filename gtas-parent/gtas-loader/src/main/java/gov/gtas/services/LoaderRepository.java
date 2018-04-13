/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.Address;
import gov.gtas.model.Agency;
import gov.gtas.model.ApisMessage;
import gov.gtas.model.Bag;
import gov.gtas.model.CodeShareFlight;
import gov.gtas.model.CreditCard;
import gov.gtas.model.Document;
import gov.gtas.model.Email;
import gov.gtas.model.Flight;
import gov.gtas.model.FlightLeg;
import gov.gtas.model.FrequentFlyer;
import gov.gtas.model.Message;
import gov.gtas.model.Passenger;
import gov.gtas.model.PaymentForm;
import gov.gtas.model.Phone;
import gov.gtas.model.Pnr;
import gov.gtas.model.ReportingParty;
import gov.gtas.model.Seat;
import gov.gtas.model.BookingDetail;
import gov.gtas.parsers.exception.ParseException;
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
import gov.gtas.repository.AddressRepository;
import gov.gtas.repository.AgencyRepository;
import gov.gtas.repository.BagRepository;
import gov.gtas.repository.BookingDetailRepository;
import gov.gtas.repository.CreditCardRepository;
import gov.gtas.repository.DocumentRepository;
import gov.gtas.repository.FlightRepository;
import gov.gtas.repository.FrequentFlyerRepository;
import gov.gtas.repository.MessageRepository;
import gov.gtas.repository.PassengerRepository;
import gov.gtas.repository.PaymentFormRepository;
import gov.gtas.repository.PhoneRepository;
import gov.gtas.repository.PnrRepository;
import gov.gtas.repository.ReportingPartyRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class LoaderRepository {
	private static final Logger logger = LoggerFactory
			.getLogger(LoaderRepository.class);

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
    BookingDetailRepository bookingDetailDao;
    
    @Autowired
    PnrRepository pnrDao;

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
    	logger.debug("@ processPnr");
    	long startTime = System.nanoTime();

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
        for (EmailVo evo : vo.getEmails()) {
        	Email email = utils.convertEmailVo(evo);
        	pnr.addEmail(email);
        }
        for(CodeShareVo cso : vo.getCodeshares()){
        	CodeShareFlight cs = utils.convertCodeShare(cso);
        	cs.getPnrs().add(pnr);
        	pnr.getCodeshares().add(cs);
        }
        logger.debug("processPnr time= "+(System.nanoTime()-startTime)/1000000);
    }

    @Transactional
    public void processFlightsAndPassengers(List<FlightVo> flights, List<PassengerVo> passengers, Set<Flight> messageFlights, Set<Passenger> messagePassengers, 
    		List<FlightLeg> flightLegs, String primeFlightKey, Set<BookingDetail> bookingDetails, long pnrId) throws ParseException {
        Set<PassengerVo> existingPassengers = new HashSet<>();
        Pnr currentPnr = pnrDao.findOne(pnrId); // We use this to associate booking details and pnr, otherwise we get detached entities and other nastiness.
        long startTime = System.nanoTime();
        Pnr currentPNR = null;
        // first find all existing passengers, create any missing flights
        for (int i=0; i<flights.size(); i++) {
            FlightVo fvo = flights.get(i);
            if(primeFlightKey.equalsIgnoreCase("placeholder") || utils.isPrimeFlight(fvo, primeFlightKey)){ //placeholder is temporary allowance to assist manual running of loader
	            logger.debug("@ getFlightByCriteria");
	            Flight currentFlight = null;
	           	Flight existingFlight = flightDao.getFlightByCriteria(fvo.getCarrier(), fvo.getFlightNumber(), fvo.getOrigin(), fvo.getDestination(), fvo.getFlightDate());
	           		if(existingFlight == null){
	                	logger.info("Flight Not Found: Creating Flight");
	                    currentFlight = utils.createNewFlight(fvo);
	                    flightDao.save(currentFlight);
	                    logger.info("Flight Created: "+fvo.getFlightNumber());
	                }
	                else if (existingFlight != null) {
	                    currentFlight = existingFlight;
	                    if(fvo.isCodeShareFlight() ){
	                    	currentFlight.setOperatingFlight(true);
	                    }
	                    for (PassengerVo pvo : passengers) {
	                    	logger.debug("@ findPassengerOnFlight");
	                        Passenger existingPassenger = findPassengerOnFlight(existingFlight, pvo);
	                        if (existingPassenger != null) {
	                            updatePassenger(existingPassenger, pvo);
	                            messagePassengers.add(existingPassenger);
	                            existingPassengers.add(pvo);
	                            logger.debug("@ createSeatAssignment");
	                            createSeatAssignment(pvo.getSeatAssignments(), existingPassenger, existingFlight);
	                            logger.debug("@ createBags");
	                            createBags(pvo.getBags(), existingPassenger, existingFlight);
	                        }
	                    }
	                }
	            logger.debug("processFlightsAndPassenger: check for existing flights time= "+ (System.nanoTime()-startTime)/1000000);
	            messageFlights.add(currentFlight);
	            FlightLeg leg = new FlightLeg();
	            leg.setFlight(currentFlight);
	            leg.setLegNumber(i);
	            flightLegs.add(leg);
            }//End if prime flight
            else{
            	BookingDetail bD = utils.convertFlightVoToBookingDetail(fvo);
            	//create booking details for this pnr
            	bookingDetails.add(bD);
            	bookingDetailDao.save(bD);
            }
        }
        
		// create any new passengers
        startTime = System.nanoTime();
		for (PassengerVo pvo : passengers) {

//			if (passengerDao.findExistingPassengerByAttributes(
//					pvo.getFirstName(), pvo.getLastName(), pvo.getMiddleName(),
//					pvo.getGender(), pvo.getDob(), pvo.getPassengerType())) {
//
//				continue;
//			}

			/*Passenger newPassenger=(Passenger)passengerDao.findExistingPassengerWithAttributes(pvo.getFirstName(), pvo.getLastName(), pvo.getMiddleName(),
					pvo.getGender(), pvo.getDob(), pvo.getPassengerType());*/

			Passenger newPassenger = utils.createNewPassenger(pvo);

			/*if(newPassenger != null){

//			Passenger newPassenger=(Passenger)passengerDao.findExistingPassengerWithAttributes(pvo.getFirstName(), pvo.getLastName(), pvo.getMiddleName(),
//					pvo.getGender(), pvo.getDob(), pvo.getPassengerType());

            Passenger newPassenger=null;

			if(newPassenger != null){
				utils.updatePassenger(pvo, newPassenger);
			}else{
				newPassenger = utils.createNewPassenger(pvo);
			}*/

			for (DocumentVo dvo : pvo.getDocuments()) {
				newPassenger.addDocument(utils.createNewDocument(dvo));
			}

			passengerDao.save(newPassenger);
			messagePassengers.add(newPassenger);

			for (Flight f : messageFlights) {
				createSeatAssignment(pvo.getSeatAssignments(), newPassenger, f);
				createBags(pvo.getBags(), newPassenger, f);
			}
		}
        logger.debug("processFlightAndPassenger() create new Passengers time = "+(System.nanoTime()-startTime)/1000000);
        // assoc all passengers w/ flights, update pax counts
        startTime = System.nanoTime();
        for (Flight f : messageFlights) {
            for (Passenger p : messagePassengers) {
            	utils.calculateValidVisaDays(f,p);
                f.addPassenger(p);
            }
            f.setPassengerCount(f.getPassengers().size());
        }
        
        //assoc passengers to booking details
    	for(BookingDetail bD : bookingDetails){
        	bD.getPnrs().add(currentPnr);
   			for(Passenger pax : messagePassengers){
    			bD.getPassengers().add(pax);
    			pax.getBookingDetails().add(bD);
    		}
    	}
        
        logger.debug("processFlightAndPassenger() associate pax/flights time = "+(System.nanoTime()-startTime)/1000000);
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

    public void createBags(List<String> bagIds, Passenger p, Flight f) {
        for (String bagId: bagIds) {
            Bag bag = new Bag();
            bag.setBagId(bagId);
            bag.setData_source("APIS");
            bag.setDestinationAirport(f.getDestination());
            bag.setAirline(f.getCarrier());
            bag.setFlight(f);
            bag.setPassenger(p);
            p.getBags().add(bag);
            bagDao.save(bag);
        }
    }

    public void createBagsFromPnrVo(PnrVo pvo,Pnr pnr) {

    	for(Flight f : pnr.getFlights()){
        	if(pvo == null || pvo.getBags() == null ){
        		break;
        	}
    		for(BagVo b : pvo.getBags()){

    			if(b.getDestinationAirport() != null && b.getDestinationAirport().equals(f.getDestination())){
    				for(Passenger p: f.getPassengers()){
    					if(StringUtils.equals(p.getFirstName(), b.getFirstName()) &&
    							StringUtils.equals(p.getLastName(), b.getLastName())){
    						 Bag bag = new Bag();
    	    		         bag.setBagId(b.getBagId());
    	    		         bag.setAirline(b.getAirline());
    	    		         bag.setData_source(b.getData_source());
    	    		         bag.setDestinationAirport(b.getDestinationAirport());
    	    		         bag.setHeadPool(b.isHeadPool());
    	    		         bag.setFlight(f);
    	    		         bag.setPassenger(p);
    	    		         bagDao.save(bag);
    	    		         p.getBags().add(bag);
    					}
    				}
    			}
    			if(b.getDestinationAirport() == null){
    				for(Passenger p: f.getPassengers()){
    					if(StringUtils.equals(p.getFirstName(), b.getFirstName()) &&
    							StringUtils.equals(p.getLastName(), b.getLastName())){
    						 Bag bag = new Bag();
    	    		         bag.setBagId(b.getBagId());
    	    		         bag.setAirline(f.getCarrier());
    	    		         bag.setData_source(b.getData_source());
    	    		         bag.setDestinationAirport(f.getDestination());
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
    public void createFormPfPayments(PnrVo vo,Pnr pnr){
    	Set<PaymentForm> chkList=new HashSet<>();
    	for(PaymentFormVo pvo:vo.getFormOfPayments()){
    		PaymentForm pf = new PaymentForm();
    		pf.setPaymentType(pvo.getPaymentType());
    		pf.setPaymentAmount(pvo.getPaymentAmount());
    		pf.setPnr(pnr);
    		chkList.add(pf);
    	}
    	for(PaymentForm pform : chkList){
    		paymentFormDao.save(pform);
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
    
    public void createBookingDetails(Pnr pnr){
    	
    	for(BookingDetail bD : pnr.getBookingDetails()){
    		bD.getPnrs().add(pnr);
    	}
    	bookingDetailDao.save(pnr.getBookingDetails());
    }
}
