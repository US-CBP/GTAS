/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.PaxProcessingDto;
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
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


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
    private PassengerIDTagRepository passengerIdTagDao;
    
    @Autowired 
    BookingDetailRepository bookingDetailDao;
    
    @Autowired
    PnrRepository pnrDao;
    
    @Autowired
    FlightService flightService;
    
    @Autowired
    TamrConversionService tamrService;

    @Autowired
    FlightPassengerRepository flightPassengerRepository;


    void checkHashCode(String hash) throws LoaderException {
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
                newAgency.setCity(newAgency.getCity()!=null?newAgency.getCity().toUpperCase():newAgency.getCity());
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
    public PaxProcessingDto processFlightsAndPassengers(List<FlightVo> flights, Set<Flight> messageFlights, List<FlightLeg> flightLegs,
                                                        String[] primeFlightKey, Set<BookingDetail> bookingDetails) throws ParseException {

        long startTime = System.nanoTime();
        // save flight and booking details
        // return flight and booking details
        // first find all existing passengers, create any missing flights
    	utils.sortFlightsByDate(flights);
    	Flight primeFlight = null;
    	boolean isPrimeFlightNew = false;
        for (int i=0; i<flights.size(); i++) {
            FlightVo fvo = flights.get(i);
            if(primeFlightKey[0].equalsIgnoreCase("placeholder") || utils.isPrimeFlight(fvo, primeFlightKey)){ //placeholder is temporary allowance to assist manual running of loader
	            logger.debug("@ getFlightByCriteria");
	            Flight currentFlight = null;
	           	Flight existingFlight = flightDao.getFlightByCriteria(fvo.getCarrier(), fvo.getFlightNumber(), fvo.getOrigin(), fvo.getDestination(), fvo.getFlightDate());
	           		if(existingFlight == null){
                        isPrimeFlightNew = true;
	                	logger.info("Flight Not Found: Creating Flight");
	                    currentFlight = utils.createNewFlight(fvo);
	                    flightDao.save(currentFlight);
	                    logger.info("Flight Created: "+fvo.getFlightNumber());
	                }
	                else {
	                    currentFlight = existingFlight;
	                    if(fvo.isCodeShareFlight() ){
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
            else{
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
        PaxProcessingDto paxProcessingDto = new PaxProcessingDto();
        paxProcessingDto.setBookingDetails(bookingDetails);
        paxProcessingDto.setPrimeFlight(primeFlight);
        paxProcessingDto.setPrimeFlightNew(isPrimeFlightNew);
        return paxProcessingDto;
    }


    void makeNewPassengers(PaxProcessingDto paxProcessingDto, List<PassengerVo> passengers, Set<Passenger> messagePassengers, Message message) throws ParseException {

        Flight primeFlight = paxProcessingDto.getPrimeFlight();
        Set<BookingDetail> bookingDetails = paxProcessingDto.getBookingDetails();
        Set<PassengerVo> existingPassengers = new HashSet<>();
        for (PassengerVo pvo : passengers) {
            logger.debug("@ findPassengerOnFlight");
            Passenger existingPassenger = findPassengerOnFlight(primeFlight, pvo);
            if (existingPassenger != null) {
                updatePassenger(existingPassenger, pvo);
                messagePassengers.add(existingPassenger);
                existingPassengers.add(pvo);
                logger.debug("@ createSeatAssignment");
                createSeatAssignment(pvo.getSeatAssignments(), existingPassenger, primeFlight);
                logger.debug("@ createBags");
                createBags(pvo.getBags(), existingPassenger, primeFlight);
            }
        }

        Set<Passenger> newPassengers = new HashSet<>();
        for (PassengerVo pvo : passengers) {
            if (!existingPassengers.contains(pvo)) {
                Passenger newPassenger = utils.createNewPassenger(pvo);
                for (DocumentVo dvo : pvo.getDocuments()) {
                    newPassenger.addDocument(utils.createNewDocument(dvo));
                }
                createSeatAssignment(pvo.getSeatAssignments(), newPassenger, primeFlight); // revisit creation equality.
                createBags(pvo.getBags(), newPassenger, primeFlight);
                utils.calculateValidVisaDays(primeFlight,newPassenger);
                newPassengers.add(newPassenger);
            }
        }

        saveAndLinkNewPassengers(messagePassengers, primeFlight, bookingDetails, newPassengers, message);
    }


    @Transactional
    protected void saveAndLinkNewPassengers(Set<Passenger> messagePassengers, Flight primeFlight, Set<BookingDetail> bookingDetails, Set<Passenger> newPassengers, Message message) {
        Iterable<Passenger> passengerIterable = passengerDao.save(messagePassengers);
        Iterable<Passenger> newPassengerIterable = passengerDao.save(newPassengers);
        Set<FlightPassenger> flightPassengers = new HashSet<>();
        List<PassengerIDTag> passengerIDTags = new ArrayList<>();
        for (Passenger p : newPassengers) {
            PassengerIDTag paxIdTag = utils.createPassengerIDTag(p);
            passengerIDTags.add(paxIdTag);
        }
        for (Passenger p : newPassengerIterable) {
            FlightPassenger fp = new FlightPassenger();
            fp.setPassengerId(p.getId().toString());
            fp.setFlightId(primeFlight.getId().toString());
            flightPassengers.add(fp);
    //        primeFlight.setPassengerCount(primeFlight.getPassengerCount()+1); TODO: FIX THIS BEFORE COMMITTING.
            }

        Set<Passenger> allPassengersNewAndUpdated = new HashSet<>();
        //add existing passengers
        passengerIterable.forEach(allPassengersNewAndUpdated::add);
        //add new passengers
        newPassengerIterable.forEach(allPassengersNewAndUpdated::add);

        for(BookingDetail bD : bookingDetails){
            for(Passenger pax : allPassengersNewAndUpdated){
                bD.getPassengers().add(pax);
                pax.getBookingDetails().add(bD);
            }
        }
        passengerIdTagDao.save(passengerIDTags);
        flightPassengerRepository.save(flightPassengers);
        Iterable<Passenger> savedPassengerIterable =  passengerDao.save(allPassengersNewAndUpdated);
        Set<Passenger> savedPassengersSet = new HashSet<>();
        savedPassengerIterable.forEach(savedPassengersSet::add);

        Set<BookingDetail> savedBookingDetailsSet = new HashSet<>();
        Iterable<BookingDetail> bookingDetailIterable = bookingDetailDao.save(bookingDetails);
        bookingDetailIterable.forEach(savedBookingDetailsSet::add);

        if (message instanceof Pnr) {
            Pnr pnr = (Pnr) message;
            pnr.setPassengers(savedPassengersSet);
            pnr.setBookingDetails(savedBookingDetailsSet);
        } else if (message instanceof ApisMessage) {
            ApisMessage apisMessage = (ApisMessage) message;
            apisMessage.setPassengers(savedPassengersSet);
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
                Boolean alreadyExistsSeat = Boolean.FALSE;
                for(Seat s2 : p.getSeatAssignments()){      	
                	if(s.equals(s2)){
                		alreadyExistsSeat = Boolean.TRUE;
                	}
                }
                if(!alreadyExistsSeat){
                	p.getSeatAssignments().add(s);
                }
                return;
            }
        }
    }

    public void createBags(List<String> bagIds, Passenger p, Flight f) {
        for (String bagId: bagIds) {
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

    public void createBagsFromPnrVo(PnrVo pvo,Pnr pnr) {
    	for(Flight f : pnr.getFlights()){
        	if(pvo == null || pvo.getBags() == null ){
        		break;
        	}
    		for(BagVo b : pvo.getBags()){
    			String destination=f.getDestination();
    			//flight_pax | bag info not making table #783 code fix
    			if(b.getDestinationAirport() != null && b.getDestinationAirport().equals(f.getDestination())){
    			if(org.apache.commons.lang3.StringUtils.isNotBlank(b.getDestinationAirport())){
    				destination=b.getDestinationAirport();
    			}
    			for(Passenger p: pnr.getPassengers()){
    					if(StringUtils.equals(p.getFirstName(), b.getFirstName()) &&
    							StringUtils.equals(p.getLastName(), b.getLastName())){
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
