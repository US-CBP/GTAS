/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.enumtype.MessageType;
import gov.gtas.model.*;
import gov.gtas.model.lookup.Airport;
import gov.gtas.model.lookup.FlightDirectionCode;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.pnrgov.enums.SSRDocsType;
import gov.gtas.parsers.util.DateUtils;
import gov.gtas.parsers.vo.*;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.repository.LookUpRepository;
import gov.gtas.util.EntityResolverUtils;
import gov.gtas.vo.lookup.AirportVo;
import gov.gtas.vo.lookup.CountryVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

	@Value("${message.dir.processed}")
	private String processedDir;

	public String getUpdatedPath(String workingPath) {
		String fileName = Paths.get(workingPath).toFile().getName();

		return (processedDir) + File.separator + fileName;
	}

	@Autowired
	private GtasLocalToUTCService gtasLocalToUTCService;

	public Passenger createNewPassenger(PassengerVo vo, Message message) throws ParseException {
		Passenger p = new Passenger();
		PassengerDetailFromMessage passengerDetailFromMessage = fromVoAndMessage(vo, message, p);
		MessageType messageType = getMessageType(message);
		passengerDetailFromMessage.setMessageType(messageType);
		PassengerTripDetails passengerTripDetails = new PassengerTripDetails(p);
		PassengerDetails passengerDetails = new PassengerDetails(p);
		p.setPassengerDetails(passengerDetails);
		p.setPassengerTripDetails(passengerTripDetails);
		p.getPassengerDetailFromMessages().add(passengerDetailFromMessage);
		p.setCreatedBy(LOADER_USER);
		updatePassenger(vo, p);
		return p;
	}




	public MessageType getMessageType(Message message) {
		MessageType messageType = MessageType.NO_TYPE;
		if (message instanceof Pnr) {
			messageType = MessageType.PNR;
		} else if (message instanceof ApisMessage) {
			messageType = MessageType.APIS;
		}
		return messageType;
	}

	public static PassengerDetailFromMessage fromVoAndMessage(PassengerVo passengerVo, Message message, Passenger p) {
		PassengerDetailFromMessage passengerDetailFromMessage = new PassengerDetailFromMessage(p);
		BeanUtils.copyProperties(passengerVo, passengerDetailFromMessage, getNullPropertyNames(passengerVo));
		if (message instanceof Pnr) {
			passengerDetailFromMessage.setMessageType(MessageType.PNR);

		} else if (message instanceof ApisMessage) {
			passengerDetailFromMessage.setMessageType(MessageType.APIS);
		}
		passengerDetailFromMessage.setMessage(message);
		return passengerDetailFromMessage;
	}

	public void updatePassenger(PassengerVo vo, Passenger p) throws ParseException {
		BeanUtils.copyProperties(vo, p, getNullPropertyNames(vo));
		BeanUtils.copyProperties(vo, p.getPassengerDetails(), getNullPropertyNames(vo));
		BeanUtils.copyProperties(vo, p.getPassengerTripDetails(), getNullPropertyNames(vo));

		if (p.getPassengerDetails().getFirstName() != null && p.getPassengerDetails().getFirstName().length() > 254) {
			p.getPassengerDetails().setFirstName(p.getPassengerDetails().getFirstName().substring(0, 254));
		}
		if (p.getPassengerDetails().getLastName() != null && p.getPassengerDetails().getLastName().length() > 254) {
			p.getPassengerDetails().setLastName(p.getPassengerDetails().getLastName().substring(0, 254));
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

		if (vo.getNationality() != null) {
			p.getPassengerDetails().setNationality(normalizeCountryCode(vo.getNationality()));
		}

		if (vo.getResidencyCountry() != null) {
			p.getPassengerDetails().setResidencyCountry(normalizeCountryCode(vo.getResidencyCountry()));
		}
		p.getPassengerTripDetails().setBagNum(vo.getBagNum());
		if (vo.getTickets() != null && vo.getTickets().size() > 0) {
			updateTicketDetails(vo, p);
		}
	}

	public void updateTicketDetails(PassengerVo vo, Passenger p) {
		for (TicketFareVo t : vo.getTickets()) {
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

	public Document createNewDocument(DocumentVo vo, Message message) {
		Document d = new Document();
		MessageType messageType = getMessageType(message);
		d.setMessageType(messageType);
		updateDocument(vo, d);
		if ((StringUtils.isNotBlank(d.getIssuanceCountry())) && d.getIssuanceCountry().length() == 2) {
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

	public Flight createNewFlight(FlightVo vo, String dest, String origin, Date etd, String carrier, String number)
			throws ParseException {
		Flight f = new Flight();
		f.setCreatedBy(LOADER_USER);
		updateFlight(vo, f);
		/*
		 * Flights created MUST be created to the border crossing flight information
		 * found in the primeFlightKey array. Override any data that does not match the
		 * prime flight key. This can happen in the case of a code share flight.
		 */

		f.setDestination(dest);
		f.setOrigin(origin);
		f.setEtdDate(etd);
		f.setCarrier(carrier);
		f.setFlightNumber(number);
		f.setIdTag(getStringHash(dest, origin, etd.toString(), carrier, number));
		return f;
	}

	public static void calculateValidVisaDays(Flight f, Passenger p) {
		Date etd = f.getEtdDate();
		Date docExpDate = null;
		int validdays = 0;
		for (Document d : p.getDocuments()) {
			if (d.getExpirationDate() != null && etd != null) {
				docExpDate = d.getExpirationDate();
				validdays = DateUtils.calculateValidVisaPeriod(etd, docExpDate);
				if (d.getExpirationDate().after(etd)) {
					p.getPassengerTripDetails().setNumberOfDaysVisaValid(validdays);
				}
				d.setNumberOfDaysValid(validdays);
			}
		}
	}

	/**
	 * Generic hash generator for a variable number of string args. May need an
	 * entity specific versions later to better control the fields and field order,
	 * etc.
	 *
	 * @param values
	 * @return String hash
	 */
	public String getStringHash(String... values) {
		String keys = "";
		String hash = "";

		for (String value : values) {
			keys += value.toUpperCase();
		}

		try {
			hash = makeSHA1Hash(keys);
		} catch (Exception ex) {
			logger.warn("Could not generate flight hash for: " + String.join(" ", values));
		}
		return hash;
	}

	public void updateFlight(FlightVo vo, Flight f) throws ParseException {
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

	public Address convertAddressVo(AddressVo vo) {
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
		PaymentForm pform = new PaymentForm();
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

	public CodeShareFlight convertCodeShare(CodeShareVo vo) {
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

			AirportVo aPort = airportService.getAirportByThreeLetterCode(vo.getCity());
			if (aPort != null && StringUtils.isNotBlank(aPort.getCity())) {
				a.setCity(aPort.getCity());
				a.setCountry(aPort.getCountry());
			}

		}
		if (StringUtils.isBlank(vo.getCity()) && StringUtils.isNotBlank(vo.getLocation())) {
			AirportVo aPort = airportService.getAirportByThreeLetterCode(vo.getLocation());
			if (aPort != null && StringUtils.isNotBlank(aPort.getCity())) {
				a.setCity(aPort.getCity());
				a.setCountry(aPort.getCountry());
			}
		}
		return a;
	}

	public Airport getAirport(String code) {
		if (StringUtils.isBlank(code)) {
			return null;
		}

		if (code.length() == 3) {
			AirportVo airportVo = airportService.getAirportByThreeLetterCode(code);
			if (airportVo != null) {
				return AirportServiceImpl.buildAirport(airportVo);
			}
		} else if (code.length() == 4) {
			AirportVo airportVo = airportService.getAirportByThreeLetterCode(code);
			if (airportVo != null) {
				return AirportServiceImpl.buildAirport(airportVo);
			}
		}

		logger.warn("Unknown airport code: " + code);
		return null;
	}

	boolean isPrimeFlight(FlightVo fvo, String origin, String dest, String etd) {
		String otherDate = Long.toString(DateUtils.stripTime(fvo.getLocalEtdDate()).getTime());
		return (fvo.getOrigin().equals(origin) && fvo.getDestination().equals(dest) && etd.equals(otherDate))
				|| isTestData(origin);
	}

	private boolean isTestData(String s) {
		return s.equalsIgnoreCase("placeholder");
	}

	@SuppressWarnings("DuplicatedCode")
	BookingDetail convertFlightVoToBookingDetail(FlightVo fvo) throws ParseException {
		BookingDetail bD = new BookingDetail();
		BeanUtils.copyProperties(fvo, bD);
		String originAirport = bD.getOrigin();
		String destinationAirport = bD.getDestination();
		Date utcETDDate = gtasLocalToUTCService.convertFromAirportCode(originAirport, fvo.getLocalEtdDate());
		Date utcETADate = gtasLocalToUTCService.convertFromAirportCode(destinationAirport, fvo.getLocalEtaDate());
		bD.setEta(utcETADate);
		bD.setEtd(utcETDDate);

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
		bD.setEtdDate(fvo.getLocalEtdDate());
		bD.setEtaDate(fvo.getLocalEtaDate());
		bD.setCreatedAt(null);
		bD.setCreatedBy(LOADER_USER);
		return bD;
	}

	// These 4 overloaded methods represent the 4 combinations that legs can be
	// compared together
	public void setDwellTime(Flight firstFlight, Flight secondFlight, Pnr pnr) {
		if (firstFlight != null && secondFlight != null
				&& firstFlight.getDestination().equalsIgnoreCase(secondFlight.getOrigin())
				&& (firstFlight.getMutableFlightDetails().getEta() != null
						&& secondFlight.getMutableFlightDetails().getEtd() != null)) {

			DwellTime d = new DwellTime(firstFlight.getMutableFlightDetails().getEta(),
					secondFlight.getMutableFlightDetails().getEtd(), secondFlight.getOrigin(), pnr);
			d.setFlyingFrom(firstFlight.getOrigin());
			d.setFlyingTo(secondFlight.getDestination());
			pnr.addDwellTime(d);
		}
	}

	public void setDwellTime(BookingDetail firstBooking, BookingDetail secondBooking, Pnr pnr) {
		if (firstBooking != null && secondBooking != null
				&& firstBooking.getDestination().equalsIgnoreCase(secondBooking.getOrigin())
				&& (firstBooking.getEta() != null && secondBooking.getEtd() != null)) {

			DwellTime d = new DwellTime(firstBooking.getEta(), secondBooking.getEtd(), secondBooking.getOrigin(), pnr);
			d.setFlyingFrom(firstBooking.getOrigin());
			d.setFlyingTo(secondBooking.getDestination());
			pnr.addDwellTime(d);
		}
	}

	public void setDwellTime(Flight firstFlight, BookingDetail secondBooking, Pnr pnr) {
		if (firstFlight != null && secondBooking != null
				&& firstFlight.getDestination().equalsIgnoreCase(secondBooking.getOrigin())
				&& (firstFlight.getMutableFlightDetails().getEta() != null && secondBooking.getEtd() != null)) {

			DwellTime d = new DwellTime(firstFlight.getMutableFlightDetails().getEta(), secondBooking.getEtd(),
					secondBooking.getOrigin(), pnr);
			d.setFlyingFrom(firstFlight.getOrigin());
			d.setFlyingTo(secondBooking.getDestination());
			pnr.addDwellTime(d);
		}
	}

	public void setDwellTime(BookingDetail firstBooking, Flight secondFlight, Pnr pnr) {
		if (firstBooking != null && secondFlight != null
				&& firstBooking.getDestination().equalsIgnoreCase(secondFlight.getOrigin())
				&& (firstBooking.getEta() != null && secondFlight.getMutableFlightDetails().getEtd() != null)) {

			DwellTime d = new DwellTime(firstBooking.getEta(), secondFlight.getMutableFlightDetails().getEtd(),
					secondFlight.getOrigin(), pnr);
			d.setFlyingFrom(firstBooking.getOrigin());
			d.setFlyingTo(secondFlight.getDestination());
			pnr.addDwellTime(d);
		}
	}

	// The parsed message did not have the flights in proper order for flight leg
	// generation (needed for dwell time and appropriate display)
	void sortFlightsByDate(List<FlightVo> flights) {
		flights.sort(Comparator.comparing(FlightVo::getLocalEtdDate));
	}

	/**
	 * Util method takes a Passenger object and return a hash for the top 5
	 * attributes
	 *
	 * @param pax
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	private String getHashForPassenger(Passenger pax) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		return EntityResolverUtils.makeHashForPassenger(pax);
	}

	private String getDocIdHashForPassenger(Passenger passenger)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String hashString = EntityResolverUtils.makeDocIdHashForPassenger(passenger);
		return hashString;
	}

	/**
	 * Util method to hash passenger attributes
	 *
	 * @param input
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	private String makeSHA1Hash(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
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
			CountryVo c = countryService.getCountryByTwoLetterCode(code);
			if (c != null) {
				return c.getIso3();
			}
		} else if (code.length() == 3) {
			CountryVo c = countryService.getCountryByThreeLetterCode(code);
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
