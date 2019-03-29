/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import gov.gtas.model.*;
import gov.gtas.parsers.pnrgov.enums.SSRDocsType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import gov.gtas.model.lookup.Airport;
import gov.gtas.model.lookup.Country;
import gov.gtas.model.lookup.FlightDirectionCode;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.util.DateUtils;
import gov.gtas.parsers.vo.AddressVo;
import gov.gtas.parsers.vo.AgencyVo;
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
import gov.gtas.parsers.vo.TicketFareVo;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.repository.LookUpRepository;
import gov.gtas.util.EntityResolverUtils;

@Service
public class LoaderUtils {
    private static final Logger logger = LoggerFactory.getLogger(LoaderUtils.class);

    private static final String LOADER_USER = "SYSTEM";

    @Autowired
    private AirportService airportService;

    @Autowired
    private CountryService countryService;

    @Autowired
    private LookUpRepository lookupRepo;

    public Passenger createNewPassenger(PassengerVo vo) throws ParseException {
        Passenger p = new Passenger();
        PassengerTripDetails passengerTripDetails = new PassengerTripDetails(p);
        PassengerDetails passengerDetails = new PassengerDetails(p);
        p.setPassengerDetails(passengerDetails);
        p.setPassengerTripDetails(passengerTripDetails);
        p.setCreatedBy(LOADER_USER);
        updatePassenger(vo, p);
        return p;
    }

    public void updatePassenger(PassengerVo vo, Passenger p) throws ParseException {
        BeanUtils.copyProperties(vo, p, getNullPropertyNames(vo));
        BeanUtils.copyProperties(vo, p.getPassengerDetails(), getNullPropertyNames(vo));
        BeanUtils.copyProperties(vo, p.getPassengerTripDetails(), getNullPropertyNames(vo));


        p.setUpdatedBy(LOADER_USER);

        if (p.getPassengerDetails().getFirstName() != null && p.getPassengerDetails().getFirstName().length() > 254) {
            p.getPassengerDetails().setFirstName(p.getPassengerDetails().getFirstName().substring(0,254));
        }
        if (p.getPassengerDetails().getLastName() != null && p.getPassengerDetails().getLastName().length() > 254) {
            p.getPassengerDetails().setLastName(p.getPassengerDetails().getLastName().substring(0,254));
        }

        if (vo.getDebarkation() != null) {
            String airportCode = vo.getDebarkation();
            p.getPassengerTripDetails().setDebarkation(airportCode);
            Airport debark = getAirport(airportCode);
            if (debark != null) {
                p.getPassengerTripDetails().setDebarkCountry(debark.getCountry());
            }
        }

        if (vo.getEmbarkation() != null) {
            String airportCode = vo.getEmbarkation();
            p.getPassengerTripDetails().setEmbarkation(airportCode);
            Airport embark = getAirport(airportCode);
            if (embark != null) {
                p.getPassengerTripDetails().setEmbarkCountry(embark.getCountry());
            }
        }

        if (vo.getCitizenshipCountry() != null) {
            p.getPassengerDetails().setCitizenshipCountry(normalizeCountryCode(vo.getCitizenshipCountry()));
        }

        if (vo.getResidencyCountry() != null) {
            p.getPassengerDetails().setResidencyCountry(normalizeCountryCode(vo.getResidencyCountry()));
        }
        p.getPassengerTripDetails().setBagNum(vo.getBagNum());
        if(vo.getTickets() != null && vo.getTickets().size() >0){
        	updateTicketDetails(vo,p);
        }
    }

    public void updateTicketDetails(PassengerVo vo, Passenger p){
    	for(TicketFareVo t : vo.getTickets()){
    		TicketFare tf = new TicketFare();
    		tf.setCurrencyCode(t.getCurrencyCode());
    		tf.setNumberOfBooklets(t.getNumberOfBooklets());
    		tf.setPaymentAmount(t.getPaymentAmount());
    		tf.setTicketless(t.isTicketless());
    		tf.setTicketNumber(t.getTicketNumber());
    		tf.setTicketType(t.getTicketType());
    		tf.setPassenger(p);
    		p.getTickets().add(tf);
    	}

    }
    public Document createNewDocument(DocumentVo vo) throws ParseException {
        Document d = new Document();
        updateDocument(vo, d);
        if((StringUtils.isNotBlank(d.getIssuanceCountry())) && d.getIssuanceCountry().length() == 2 ){
        	d.setIssuanceCountry(normalizeCountryCode(d.getIssuanceCountry()));
        }
        return d;
    }

    public void updateDocument(DocumentVo vo, Document d) {
        String docType = d.getDocumentType();
        if (docType == null || SSRDocsType.NOT_PROVIDED.toString().equalsIgnoreCase(d.getDocumentType())) {
            BeanUtils.copyProperties(vo, d, getNullPropertyNames(vo));
        } else {
            BeanUtils.copyProperties(vo, d, getNullPropertyNames(vo));
            d.setDocumentType(docType);
        }
    }

    public ReportingParty createNewReportingParty(ReportingPartyVo vo) {
        ReportingParty rp = new ReportingParty();
        updateReportingParty(vo, rp);
        return rp;
    }

    public void updateReportingParty(ReportingPartyVo vo, ReportingParty rp) {
        BeanUtils.copyProperties(vo, rp);
    }

    public Flight createNewFlight(FlightVo vo, String [] primeFlightKey) throws ParseException {
        Flight f = new Flight();
        f.setCreatedBy(LOADER_USER);
        updateFlight(vo, f);
        /*
        Flights created MUST be created to the border crossing flight information
        found in the primeFlightKey array.
        Override any data that does not match the prime flight key.
        This can happen in the case of a code share flight.
         * */
        f.setDestination(primeFlightKey[GtasLoaderImpl.PRIME_FLIGHT_DESTINATION]);
        f.setOrigin(primeFlightKey[GtasLoaderImpl.PRIME_FLIGHT_ORIGIN]);
        f.setEtdDate(new Date(Long.parseLong(primeFlightKey[GtasLoaderImpl.ETD_DATE_NO_TIMESTAMP_AS_LONG])));
        f.setCarrier(primeFlightKey[GtasLoaderImpl.PRIME_FLIGHT_CARRIER]);
        f.setFlightNumber(primeFlightKey[GtasLoaderImpl.PRIME_FLIGHT_NUMBER_STRING]);
        return f;
    }

    public void calculateValidVisaDays(Flight f,Passenger p){
    	Date etd = f.getEtdDate();
    	Date docExpDate=null;
    	int validdays=0;
    	for(Document d : p.getDocuments()){
    		if(d.getExpirationDate() != null && etd != null ){
    			docExpDate=d.getExpirationDate();
         		validdays=DateUtils.calculateValidVisaPeriod(etd, docExpDate);
         		if(d.getExpirationDate().after(etd)){
         			p.getPassengerTripDetails().setNumberOfDaysVisaValid(validdays);
         		}
         		d.setNumberOfDaysValid(validdays);
    		}
    	}
    }
 
    
    public void updateFlight(FlightVo vo, Flight f) throws ParseException {
        f.setUpdatedBy(LOADER_USER);
        String homeCountry = lookupRepo.getAppConfigOption(AppConfigurationRepository.HOME_COUNTRY);

        BeanUtils.copyProperties(vo, f, getNullPropertyNames(vo));

        f.setFullFlightNumber(String.format("%s%s", vo.getCarrier(), vo.getFlightNumber()));

        Airport dest = getAirport(f.getDestination());
        String destCountry = null;
        if (dest != null) {
            destCountry = dest.getCountry();
            f.setDestinationCountry(destCountry);
        }

        Airport origin = getAirport(f.getOrigin());
        String originCountry = null;
        if (origin != null) {
            originCountry = origin.getCountry();
            f.setOriginCountry(originCountry);
        }

        if (homeCountry.equals(originCountry) && homeCountry.equals(destCountry)) {
            f.setDirection(FlightDirectionCode.C.name());
        } else if (homeCountry.equals(originCountry)) {
            f.setDirection(FlightDirectionCode.O.name());
        } else if (homeCountry.equals(destCountry)) {
            f.setDirection(FlightDirectionCode.I.name());
        } else {
            f.setDirection(FlightDirectionCode.A.name());
        }
    }

    public void convertPnrVo(Pnr pnr, PnrVo vo) throws ParseException {
        BeanUtils.copyProperties(vo, pnr);
        Airport origin = getAirport(vo.getOrigin());
        String originCountry = null;
        if (origin != null) {
            originCountry = origin.getCountry();
            pnr.setOriginCountry(originCountry);
        }

        pnr.setPassengerCount(vo.getPassengers().size());
        if (vo.getDateBooked() != null && vo.getDepartureDate() != null) {
            // NB: won't work for leap years
            long diff = vo.getDepartureDate().getTime() - vo.getDateBooked().getTime();
            int days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            pnr.setDaysBookedBeforeTravel(days);
        }
    }

    public Address convertAddressVo(AddressVo vo) throws ParseException {
        Address addr = new Address();
        addr.setCreatedBy(LOADER_USER);
        BeanUtils.copyProperties(vo, addr);
        addr.setCountry(normalizeCountryCode(vo.getCountry()));
        return addr;
    }

    public Phone convertPhoneVo(PhoneVo vo) {
        Phone p = new Phone();
        p.setCreatedBy(LOADER_USER);
        BeanUtils.copyProperties(vo, p);
        return p;
    }

    public PaymentForm convertPaymentFormVo(PaymentFormVo vo) {
    	PaymentForm pform=new PaymentForm();
    	pform.setPaymentType(vo.getPaymentType());
    	pform.setPaymentAmount(vo.getPaymentAmount());
        return pform;
    }

    public CreditCard convertCreditVo(CreditCardVo vo) {
        CreditCard cc = new CreditCard();
        cc.setCreatedBy(LOADER_USER);
        BeanUtils.copyProperties(vo, cc);
        return cc;
    }
    
    public FrequentFlyer convertFrequentFlyerVo(FrequentFlyerVo vo) {
        FrequentFlyer ff = new FrequentFlyer();
        ff.setCreatedBy(LOADER_USER);
        BeanUtils.copyProperties(vo, ff);
        return ff;
    }

    public Email convertEmailVo(EmailVo vo) {
        Email e = new Email();
        e.setCreatedBy(LOADER_USER);
        e.setAddress(vo.getAddress());
        e.setDomain(vo.getDomain());
        return e;
    }
    public CodeShareFlight convertCodeShare(CodeShareVo vo){
    	CodeShareFlight csf = new CodeShareFlight();
    	csf.setMarketingFlightNumber(vo.getFullMarketingFlightNumber());
    	csf.setOperatingFlightNumber(vo.getFullOperatingFlightNumber());
		return csf;
    	
    }    
    public Agency convertAgencyVo(AgencyVo vo) {
        Agency a = new Agency();
        a.setCreatedBy(LOADER_USER);
        BeanUtils.copyProperties(vo, a);
        if (StringUtils.isNotBlank(vo.getCity())) {
            
            Airport aPort = airportService.getAirportByThreeLetterCode(vo.getCity());
            if (aPort != null && StringUtils.isNotBlank(aPort.getCity())) {
            	a.setCity(aPort.getCity());
            	a.setCountry(aPort.getCountry());
            }
           
        }
        if(StringUtils.isBlank(vo.getCity()) && StringUtils.isNotBlank(vo.getLocation())){
        	Airport aPort = airportService.getAirportByThreeLetterCode(vo.getLocation());
        	if (aPort != null && StringUtils.isNotBlank(aPort.getCity())) {
                a.setCity(aPort.getCity());
                a.setCountry(aPort.getCountry());
            }
        }
        return a;
    }

    public Airport getAirport(String code) throws ParseException {
        if (StringUtils.isBlank(code)) {
            return null;
        }

        if (code.length() == 3) {
            return airportService.getAirportByThreeLetterCode(code);
        } else if (code.length() == 4) {
            return airportService.getAirportByFourLetterCode(code);
        }

        logger.warn("Unknown airport code: " + code);
        return null;
    }
    
    public boolean isPrimeFlight(FlightVo fvo, String[] primeFlightKey) {
        String primeFlightOrigin = primeFlightKey[GtasLoaderImpl.PRIME_FLIGHT_ORIGIN];
        String primeFlightDestination = primeFlightKey[GtasLoaderImpl.PRIME_FLIGHT_DESTINATION];
        return (fvo.getOrigin().equals(primeFlightOrigin) &&
                fvo.getDestination().equals(primeFlightDestination))
                ||  isTestData(primeFlightKey[GtasLoaderImpl.PRIME_FLIGHT_ORIGIN]);
    }

    private boolean isTestData(String s) {
        return s.equalsIgnoreCase("placeholder");
    }

    public BookingDetail convertFlightVoToBookingDetail(FlightVo fvo) throws ParseException{
    	BookingDetail bD = new BookingDetail();
    	BeanUtils.copyProperties(fvo, bD);
    	
    	 Airport dest = getAirport(fvo.getDestination());
         String destCountry = null;
         if (dest != null) {
             destCountry = dest.getCountry();
             bD.setDestinationCountry(destCountry);
         }

         Airport origin = getAirport(fvo.getOrigin());
         String originCountry = null;
         if (origin != null) {
             originCountry = origin.getCountry();
             bD.setOriginCountry(originCountry);
         }
    	
    	bD.setFullFlightNumber(fvo.getCarrier() + fvo.getFlightNumber());
    	bD.setEtdDate(fvo.getEtd());
    	bD.setEtaDate(fvo.getEta());
    	bD.setCreatedAt(null);
    	bD.setCreatedBy(LOADER_USER);
    	return bD;
    }
    
    //These 4 overloaded methods represent the 4 combinations that legs can be compared together
    public void setDwellTime(Flight firstFlight,Flight secondFlight, Pnr pnr){
    	if(firstFlight != null && secondFlight != null
    			&& firstFlight.getDestination().equalsIgnoreCase(secondFlight.getOrigin())
    			&& (firstFlight.getMutableFlightDetails().getEta()!=null && secondFlight.getMutableFlightDetails().getEtd() != null)){

    	   	DwellTime d =new DwellTime(firstFlight.getMutableFlightDetails().getEta(),secondFlight.getMutableFlightDetails().getEtd(),secondFlight.getOrigin(),pnr);
    		d.setFlyingFrom(firstFlight.getOrigin());
    		d.setFlyingTo(secondFlight.getDestination());
    		pnr.addDwellTime(d);
    	}
    }
    public void setDwellTime(BookingDetail firstBooking, BookingDetail secondBooking, Pnr pnr){
    	if(firstBooking != null && secondBooking != null 
    			&& firstBooking.getDestination().equalsIgnoreCase(secondBooking.getOrigin())
    			&& (firstBooking.getEta()!=null && secondBooking.getEtd() != null)){
    		
    	   	DwellTime d =new DwellTime(firstBooking.getEta(),secondBooking.getEtd(),secondBooking.getOrigin(),pnr);
    		d.setFlyingFrom(firstBooking.getOrigin());
    		d.setFlyingTo(secondBooking.getDestination());
    		pnr.addDwellTime(d);
    	}
    }
    public void setDwellTime(Flight firstFlight, BookingDetail secondBooking, Pnr pnr){
    	if(firstFlight != null && secondBooking != null
    			&& firstFlight.getDestination().equalsIgnoreCase(secondBooking.getOrigin())
    			&& (firstFlight.getMutableFlightDetails().getEta()!=null && secondBooking.getEtd() != null)){

    	   	DwellTime d =new DwellTime(firstFlight.getMutableFlightDetails().getEta(),secondBooking.getEtd(),secondBooking.getOrigin(),pnr);
    		d.setFlyingFrom(firstFlight.getOrigin());
    		d.setFlyingTo(secondBooking.getDestination());
    		pnr.addDwellTime(d);
    	}
    }
    public void setDwellTime(BookingDetail firstBooking, Flight secondFlight, Pnr pnr){
    	if(firstBooking != null && secondFlight != null 
    			&& firstBooking.getDestination().equalsIgnoreCase(secondFlight.getOrigin())
    			&& (firstBooking.getEta()!=null && secondFlight.getMutableFlightDetails().getEtd() != null)){
    		
    	   	DwellTime d =new DwellTime(firstBooking.getEta(),secondFlight.getMutableFlightDetails().getEtd(),secondFlight.getOrigin(),pnr);
    		d.setFlyingFrom(firstBooking.getOrigin());
    		d.setFlyingTo(secondFlight.getDestination());
    		pnr.addDwellTime(d);
    	}
    }
    //The parsed message did not have the flights in proper order for flight leg generation (needed for dwell time and appropriate display)
    void sortFlightsByDate(List<FlightVo> flights){
    	flights.sort(Comparator.comparing(FlightVo::getEtd));
    }

    /**
     * Util method takes a Passenger object and return a hash for the top 5 attributes
     * @param pax
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private String getHashForPassenger(Passenger pax) throws NoSuchAlgorithmException, UnsupportedEncodingException{
//        String hash = makeSHA1Hash(String.join("", Arrays.asList(pax.getFirstName().toUpperCase(), pax.getLastName().toUpperCase(),
//                pax.getGender().toUpperCase(), new SimpleDateFormat("MM/dd/yyyy").format(pax.getDob()))));
//            	
//           return hash;
    	return EntityResolverUtils.makeHashForPassenger(pax);
    }
    
    private String getDocIdHashForPassenger(Passenger passenger)  throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        String hashString = EntityResolverUtils.makeDocIdHashForPassenger(passenger);
        return hashString;
    }

    /**
     * Util method to hash passenger attributes
     * @param input
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private String makeSHA1Hash(String input)
            throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
//        MessageDigest md = MessageDigest.getInstance("SHA1");
//        md.reset();
//        byte[] buffer = input.getBytes("UTF-8");
//        md.update(buffer);
//        byte[] digest = md.digest();
//
//        String hexStr = "";
//        for (int i = 0; i < digest.length; i++) {
//            hexStr +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
//        }
//        return hexStr;
    	return EntityResolverUtils.makeSHA1Hash(input);
    }
    
    /**
     * try returning ISO_3 code
     */
    private String normalizeCountryCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }

        if (code.length() == 2) {
            Country c = countryService.getCountryByTwoLetterCode(code);
            if (c != null) {
                return c.getIso3();
            }
        } else if (code.length() == 3) {
            Country c = countryService.getCountryByThreeLetterCode(code);
            if (c != null) {
                return code;
            }
        }

        logger.warn("Unknown country code: " + code);
        return code;
    }

    static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null)
                emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
    
	public PassengerIDTag createPassengerIDTag(Passenger p) {
    	PassengerIDTag paxIdTag = new PassengerIDTag(); 
    	paxIdTag.setPax_id(p.getId());
    	paxIdTag.setCreatedAt(new Date());
    	paxIdTag.setCreatedBy(LOADER_USER);
    	try {
			paxIdTag.setIdTag(getHashForPassenger(p));
			paxIdTag.setDocHashId(getDocIdHashForPassenger(p));
                        
		} catch (NoSuchAlgorithmException e) {
			logger.error("error creating passenger id tag:", e);
		} catch (UnsupportedEncodingException e) {
			logger.error("error creating passenger id tag.", e);
		}
    	return paxIdTag;
	}
}
