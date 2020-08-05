/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.enumtype.MessageType;
import gov.gtas.enumtype.TripTypeEnum;
import gov.gtas.error.ErrorUtils;
import gov.gtas.model.*;
import gov.gtas.parsers.exception.ParseException;
import gov.gtas.parsers.util.DateUtils;
import gov.gtas.parsers.vo.*;
import gov.gtas.repository.*;
import gov.gtas.summary.EventIdentifier;
import gov.gtas.summary.MessageAction;
import gov.gtas.summary.MessageSummary;
import gov.gtas.summary.MessageTravelInformation;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static gov.gtas.services.LoaderUtils.fromVoAndMessage;
import static gov.gtas.services.LoaderUtils.getNullPropertyNames;

@Service
public class GtasLoaderImpl implements GtasLoader {

	private static final Logger logger = LoggerFactory.getLogger(GtasLoaderImpl.class);
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

	private final PnrRepository msgDao;

	private final ApisMessageRepository apisMessageRepository;

	private final CreditCardRepository creditDao;

	private final AddressRepository addressDao;

	private final AgencyRepository agencyDao;

	private final MessageRepository<Message> messageDao;

	private final FrequentFlyerRepository ffdao;

	private final LoaderUtils utils;

	private final BagMeasurementsRepository bagMeasurementsRepository;

	private final PaymentFormRepository paymentFormDao;

	private final PassengerIDTagRepository passengerIdTagDao;

	private final BookingDetailRepository bookingDetailDao;

	private final LoaderServices loaderServices;

	private final FlightPassengerRepository flightPassengerRepository;

	private final FlightPassengerCountRepository flightPassengerCountRepository;

	private final MutableFlightDetailsRepository mutableFlightDetailsRepository;

	private final GtasLocalToUTCService gtasLocalToUTCService;

	private final EmailRepository emailRepository;

	private final CodeShareRepository codeShareRepository;

	@Autowired
	public GtasLoaderImpl(PassengerRepository passengerDao, ReportingPartyRepository rpDao,
						  LoaderServices loaderServices,
						  FlightRepository flightDao,
						  DocumentRepository docDao,
						  PhoneRepository phoneDao,
						  PnrRepository msgDao, ApisMessageRepository apisMessageRepository, PaymentFormRepository paymentFormDao,
						  CreditCardRepository creditDao,
						  FlightPassengerCountRepository flightPassengerCountRepository,
						  AddressRepository addressDao,
						  AgencyRepository agencyDao,
						  MessageRepository<Message> messageDao,
						  PassengerIDTagRepository passengerIdTagDao,
						  FlightPassengerRepository flightPassengerRepository,
						  FrequentFlyerRepository ffdao, LoaderUtils utils,
						  BookingDetailRepository bookingDetailDao,
						  MutableFlightDetailsRepository mutableFlightDetailsRepository,
						  BagMeasurementsRepository bagMeasurementsRepository,
						  GtasLocalToUTCService gtasLocalToUTCService,
						  EmailRepository emailRepository,
						  CodeShareRepository codeShareRepository) {
		this.passengerDao = passengerDao;
		this.rpDao = rpDao;
		this.loaderServices = loaderServices;
		this.flightDao = flightDao;
		this.docDao = docDao;
		this.phoneDao = phoneDao;
		this.msgDao = msgDao;
		this.apisMessageRepository = apisMessageRepository;
		this.paymentFormDao = paymentFormDao;
		this.creditDao = creditDao;
		this.flightPassengerCountRepository = flightPassengerCountRepository;
		this.addressDao = addressDao;
		this.agencyDao = agencyDao;
		this.messageDao = messageDao;
		this.passengerIdTagDao = passengerIdTagDao;
		this.flightPassengerRepository = flightPassengerRepository;
		this.ffdao = ffdao;
		this.utils = utils;
		this.bookingDetailDao = bookingDetailDao;
		this.mutableFlightDetailsRepository = mutableFlightDetailsRepository;
		this.bagMeasurementsRepository = bagMeasurementsRepository;
		this.gtasLocalToUTCService = gtasLocalToUTCService;
		this.emailRepository = emailRepository;
		this.codeShareRepository = codeShareRepository;
	}

	@Override
	public void checkHashCode(String hash) throws LoaderException {
		Message m = messageDao.findByHashCode(hash);
		if (m != null) {
			throw new DuplicateHashCodeException("Duplicate message, message ignored. hashcode is: " + hash);
		}
	}

	@Override
	public boolean createMessage(ApisMessage m) {
		boolean ret = true;

		try {
			m.setFilePath(utils.getUpdatedPath(m.getFilePath()));

			m = apisMessageRepository.save(m);
		} catch (Exception e) {
			ret = false;
			GtasLoaderImpl.handleException(e, m);
			try {
				m.setFilePath(utils.getUpdatedPath(m.getFilePath()));
				m = apisMessageRepository.save(m);
			} catch (Exception ignored) {
			}
		}
		return ret;
	}

	@Override
	public boolean createMessage(Pnr m) {
		boolean ret = true;
		logger.debug("@createMessage");
		long startTime = System.nanoTime();
		try {
			m.setFilePath(utils.getUpdatedPath(m.getFilePath()));
			m = msgDao.save(m);
		} catch (Exception e) {
			handleException(e, m);
			ret = false;
			try {
				logger.info("ERROR FILE FILEPATH: " + m.getFilePath());
				m.setFilePath(utils.getUpdatedPath(m.getFilePath()));
				m = msgDao.save(m);
			} catch (Exception ignored) {
			}
			logger.warn("Error saving message!", e);
		} finally {
			logger.debug("createMessage time = " + (System.nanoTime() - startTime) / 1000000);
		}
		return ret;
	}
	public static void handleException(Exception e, Pnr pnr) {
		// set all the collections to null so we can save the message itself
		pnr.setFlights(null);
		pnr.setHashCode(null);
		pnr.setPassengers(null);
		pnr.setCreditCards(null);
		pnr.setAddresses(null);
		pnr.setAgencies(null);
		pnr.setEmails(null);
		pnr.setHashCode(null);
		pnr.setFrequentFlyers(null);
		pnr.setPhones(null);
		pnr.setDwellTimes(null);
		pnr.setPaymentForms(null);
		String stacktrace = ErrorUtils.getStacktrace(e);
		pnr.setError(stacktrace);
		if (e instanceof DuplicateHashCodeException) {
			logger.info(e.getMessage());
			pnr.getStatus().setMessageStatusEnum(MessageStatusEnum.DUPLICATE_MESSAGE);
		} else {
			pnr.getStatus().setMessageStatusEnum(MessageStatusEnum.FAILED_LOADING);
			logger.error(stacktrace);
		}
	}


	public static void handleException(Exception e, ApisMessage apisMessage) {
		String stacktrace = ErrorUtils.getStacktrace(e);
		apisMessage.setError(stacktrace);
		apisMessage.setHashCode(null);
		if (e instanceof DuplicateHashCodeException) {
			logger.info(e.getMessage());
			apisMessage.getStatus().setMessageStatusEnum(MessageStatusEnum.DUPLICATE_MESSAGE);
		} else {
			apisMessage.getStatus().setMessageStatusEnum(MessageStatusEnum.FAILED_LOADING);
			logger.error(stacktrace);
		}
	}

	@Override
	public void prepareAdditionalProcessing(MessageInformation messageInformation, ApisMessage apis,
											String[] pflightKey, String rawMessage)	{

		Flight primeFlight = apis.getFlights().iterator().next();
		EventIdentifier ei = getEventIdentifier(primeFlight, pflightKey, "APIS_PASSENGER");

		String messageHash = apis.getHashCode();
		MessageSummary ms = new MessageSummary(messageHash, primeFlight.getIdTag());
		ms.setRawMessage(rawMessage);
		ms.setAction(MessageAction.PROCESSED_MESSAGE);
		ms.setTransmissionDate(apis.getEdifactMessage().getTransmissionDate());
		ms.setSourceMessageVersion("APIS");
		ms.setSourceMessageVersion(apis.getEdifactMessage().getVersion());
		ms.setTransmissionSource(apis.getEdifactMessage().getTransmissionSource());
		ms.setEventIdentifier(ei);
		ms.setRawMessage(rawMessage);
		ms.setMessageType("APIS");

		MessageTravelInformation mti = SummaryFactory.from(primeFlight, primeFlight.getIdTag());
		ms.getMessageTravelInformation().add(mti);
		for (BookingDetail bd : apis.getBookingDetails()) {
			MessageTravelInformation bdTi = SummaryFactory.from(bd, primeFlight.getIdTag());
			ms.getMessageTravelInformation().add(bdTi);
		}
		apis.getPassengers().forEach(p -> SummaryFactory.addPassengerNoHits(p, ms));
		messageInformation.setMessageSummary(ms);
	}


	@Override
	public void prepareAdditionalProcessing(MessageInformation messageInformation, Pnr pnr, String[] pflightKey, String rawMessage){

		Flight primeFlight = pnr.getFlights().iterator().next();
		EventIdentifier ei = getEventIdentifier(primeFlight, pflightKey, "PNR_PASSENGER");

		String messageHash = pnr.getHashCode();
		String flightHash = primeFlight.getIdTag();
		MessageSummary ms = new MessageSummary(messageHash, flightHash);
		ms.setPnrRefNumber(pnr.getRecordLocator());
		ms.setRawMessage(rawMessage);
		ms.setAction(MessageAction.PROCESSED_MESSAGE);
		ms.setTransmissionDate(pnr.getEdifactMessage().getTransmissionDate());
		ms.setSourceMessageVersion("PNR");
		ms.setSourceMessageVersion(pnr.getEdifactMessage().getVersion());
		ms.setTransmissionSource(pnr.getEdifactMessage().getTransmissionSource());
		ms.setEventIdentifier(ei);
		ms.setRawMessage(rawMessage);
		ms.setMessageType("PNR");

		MessageTravelInformation mti = SummaryFactory.from(primeFlight, primeFlight.getIdTag());
		ms.getMessageTravelInformation().add(mti);
		for (BookingDetail bd : pnr.getBookingDetails()) {
			MessageTravelInformation bdTi = SummaryFactory.from(bd, primeFlight.getIdTag());
			ms.getMessageTravelInformation().add(bdTi);
		}

		pnr.getPhones().forEach(p -> SummaryFactory.addPhone(p, ms));
		pnr.getAddresses().forEach(a -> SummaryFactory.addAddress(a, ms));
		pnr.getCreditCards().forEach(cc -> SummaryFactory.addCreditCard(cc, ms));
		pnr.getFrequentFlyers().forEach(ff -> SummaryFactory.addFrequentFlyer(ff, ms));
		pnr.getEmails().forEach(e -> SummaryFactory.addEmail(e, ms));
		pnr.getPassengers().forEach(p -> SummaryFactory.addPassengerNoHits(p, ms));

		messageInformation.setMessageSummary(ms);
	}


	private EventIdentifier getEventIdentifier(Flight primeFlight, String[] pflightKey, String apis_passenger) {
		EventIdentifier ei = new EventIdentifier();
		ei.setCountryDestination(primeFlight.getDestinationCountry());
		ei.setCountryOrigin(primeFlight.getOriginCountry());
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 5; i++) {
			sb.append(pflightKey[i]);
		}
		ei.setIdentifier(sb.toString());
		ei.setIdentifierArrayList(new ArrayList<>(Arrays.asList(pflightKey)));
		ei.setEventType(apis_passenger);
		return ei;
	}


	@Override
	public TripTypeEnum calculateTripType(List<FlightLeg> flightLegList, Set<DwellTime> dwellTimeSet) {

		if (flightLegList.size() == 1) {
			return TripTypeEnum.ONEWAY;
		}

		TripTypeEnum tripType = null;
		Collections.sort(flightLegList);

		FlightLeg first = flightLegList.get(0);
		FlightLeg last = flightLegList.get(flightLegList.size() - 1);

		String firstLegOrigin = getFlightLegOrigin(first);
		String lastLegDestination = getFlightLegDest(last);
		String previousDest = firstLegOrigin;
		boolean noncontig = false;
		boolean hasLongDwellTime = false;

		for (FlightLeg flightLeg : flightLegList) {
			if (!getFlightLegOrigin(flightLeg).equals(previousDest)) {
				noncontig = true;
			}
			previousDest = getFlightLegDest(flightLeg);
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

		if (noncontig) {
			tripType = TripTypeEnum.NONCONTIGUOUS;
		} else if (firstLegOrigin.equalsIgnoreCase(lastLegDestination)) {
			tripType = TripTypeEnum.ROUNDTRIP;
		} else {
			tripType = hasLongDwellTime ? TripTypeEnum.MULTICITY : TripTypeEnum.ONEWAY;
		}

		return tripType;
	}

	// TODO - REFAC as member of FlightLeg?
	private String getFlightLegOrigin(FlightLeg leg) {
		if (leg.getBookingDetail() != null) {
			return leg.getBookingDetail().getOrigin().toUpperCase();
		}
		return leg.getFlight().getOrigin().toUpperCase();
	}

	private String getFlightLegDest(FlightLeg leg) {
		if (leg.getBookingDetail() != null) {
			return leg.getBookingDetail().getDestination().toUpperCase();
		}
		return leg.getFlight().getDestination().toUpperCase();
	}


	@Override
	public void calculateDwellTimes(Pnr pnr) {
		logger.debug("@ calculateDwellTimes");
		long startTime = System.nanoTime();
		List<FlightLeg> legs = pnr.getFlightLegs();
		if (CollectionUtils.isEmpty(legs)) {
			return;
		}

		for (int i = 0; i < legs.size(); i++) {
			if (i + 1 < legs.size()) { // If the 'next' leg actually exists
				// 4 different combinations of flights and booking details n^2, where n = 2.
				// FxF, FxB, BxF, BxB. Order matters due to time calc
				if (legs.get(i).getFlight() != null) {
					if (legs.get(i + 1).getFlight() != null) { // FxF
						utils.setDwellTime(legs.get(i).getFlight(), legs.get(i + 1).getFlight(), pnr);
					} else { // next leg is a booking detail //FxB
						utils.setDwellTime(legs.get(i).getFlight(), legs.get(i + 1).getBookingDetail(), pnr);
					}
				} else if (legs.get(i + 1).getFlight() != null) { // first leg is booking detail BxF
					utils.setDwellTime(legs.get(i).getBookingDetail(), legs.get(i + 1).getFlight(), pnr);
				} else { // both legs are booking details BxB
					utils.setDwellTime(legs.get(i).getBookingDetail(), legs.get(i + 1).getBookingDetail(), pnr);
				}
			}
		}
		logger.debug("calculateDwellTime time = " + (System.nanoTime() - startTime) / 1000000);
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
	public void processPnr(Pnr pnr, PnrVo vo) {
		logger.debug("@ processPnr");
		long startTime = System.nanoTime();
		Long flightId = pnr.getFlights().iterator().next().getId();
		for (AddressVo addressVo : vo.getAddresses()) {
			List<Address> existingAddress = addressDao.findByLine1AndCityAndStateAndPostalCodeAndCountryAndFlightId(
					addressVo.getLine1(), addressVo.getCity(), addressVo.getState(), addressVo.getPostalCode(),
					addressVo.getCountry(), flightId);
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
			List<CreditCard> existingCard = creditDao.findByCardTypeAndNumberAndExpirationAndFlightId(
					creditVo.getCardType(), creditVo.getNumber(), creditVo.getExpiration(), flightId);
			if (existingCard.isEmpty()) {
				CreditCard newCard = utils.convertCreditVo(creditVo);
				newCard.setFlightId(flightId);
				pnr.addCreditCard(newCard);
			} else {
				pnr.addCreditCard(existingCard.get(0));
			}
		}

		for (FrequentFlyerVo ffvo : vo.getFrequentFlyerDetails()) {
			List<FrequentFlyer> existingFf = ffdao.findByCarrierAndNumberAndFlightId(ffvo.getCarrier(), ffvo.getNumber(), flightId);
			if (existingFf.isEmpty()) {
				FrequentFlyer newFf = utils.convertFrequentFlyerVo(ffvo);
				newFf.setFlightId(flightId);
				pnr.addFrequentFlyer(newFf);
			} else {
				pnr.addFrequentFlyer(existingFf.get(0));
			}
		}

		for (AgencyVo avo : vo.getAgencies()) {
			List<Agency> existingAgency = agencyDao.findByNameAndLocationAndFlightId(avo.getName(), avo.getLocation(), flightId);
			if (existingAgency.isEmpty()) {
				Agency newAgency = utils.convertAgencyVo(avo);
				newAgency.setFlightId(flightId);
				newAgency
						.setCity(newAgency.getCity() != null ? newAgency.getCity().toUpperCase() : newAgency.getCity());
				pnr.addAgency(newAgency);
			} else {
				pnr.addAgency(existingAgency.get(0));
			}
		}
		for (EmailVo evo : vo.getEmails()) {
			List<Email> existingEmail = emailRepository.findByAddressAndFlightId(evo.getAddress(), flightId);
			if (existingEmail.isEmpty()) {
				Email email = utils.convertEmailVo(evo);
				email.setFlightId(flightId);
				pnr.addEmail(email);
			} else {
				pnr.addEmail(existingEmail.get(0));
			}
		}

		for (CodeShareVo cso : vo.getCodeshares()) {
			List<CodeShareFlight> codeShareFlights = codeShareRepository.findByMarketingFlightNumberAndFlightId(cso.getFullMarketingFlightNumber(), flightId);
			if (codeShareFlights.isEmpty()) {
				CodeShareFlight cs = utils.convertCodeShare(cso);
				cs.getPnrs().add(pnr);
				pnr.getCodeshares().add(cs);
			} else {
				pnr.getCodeshares().add(codeShareFlights.get(0));
				codeShareFlights.get(0).getPnrs().add(pnr);
			}
		}
		logger.debug("processPnr time= " + (System.nanoTime() - startTime) / 1000000);
	}

	@Override
	public Flight processFlightsAndBookingDetails(List<FlightVo> flights, Set<Flight> messageFlights,
			List<FlightLeg> flightLegs, String[] primeFlightKey, Set<BookingDetail> bookingDetails)
			throws ParseException, NoPrimeFlightException {
		long startTime = System.nanoTime();
		String primeFlightDateString = primeFlightKey[ETD_DATE_NO_TIMESTAMP_AS_LONG];
		Date primeFlightDate = new Date(Long.parseLong(primeFlightDateString));
		String primeFlightNumber = primeFlightKey[PRIME_FLIGHT_NUMBER_STRING];
		String primeFlightCarrier = primeFlightKey[PRIME_FLIGHT_CARRIER];
		String primeFlightOrigin = primeFlightKey[PRIME_FLIGHT_ORIGIN];
		String primeFlightDest = primeFlightKey[PRIME_FLIGHT_DESTINATION];

		/*
		 * A special case exist where a pnrVo has a flight on tvl 0 but not a
		 * corresponding flight on tvl 5. This is a valid case. In this scenario we make
		 * a flightVo containing data from the TVL 0.
		 *
		 */
		if (flights.isEmpty()) {
			FlightVo flightVo = new FlightVo();
			flightVo.setLocalEtaDate(primeFlightDate);
			flightVo.setCarrier(primeFlightCarrier);
			flightVo.setFlightNumber(primeFlightNumber);
			flightVo.setOrigin(primeFlightOrigin);
			flightVo.setDestination(primeFlightDest);
			flightVo.setIdTag(utils.getStringHash(primeFlightDest, primeFlightOrigin, primeFlightDateString,
					primeFlightCarrier, primeFlightNumber));
			flights.add(flightVo);
		}

		utils.sortFlightsByDate(flights);
		Flight primeFlight = null;
		for (int i = 0; i < flights.size(); i++) {
			FlightVo fvo = flights.get(i);
			/*
			 * A prime flight is determined by the level 0 TVL of a PNR or a combination of
			 * TDT, LOC, DTM fields in an APIS representing flight with LOC code of 125/87
			 * and DTM code of 189. The isPrimeFlight will check to see if the flightVo
			 * being processed matches the prime flight and set the flight accordingly. In
			 * short: The prime flight is the reason the message was received. There is 1
			 * and only 1 prime flight per message.
			 */
			if (utils.isPrimeFlight(fvo, primeFlightOrigin, primeFlightDest, primeFlightDateString)) {
				Flight currentFlight = flightDao.getFlightByCriteria(primeFlightCarrier, primeFlightNumber,
						primeFlightOrigin, primeFlightDest, primeFlightDate);
				if (currentFlight == null) {
					//TODO: Combine with loader message reciever unique key logic
					currentFlight = utils.createNewFlight(fvo, primeFlightDest, primeFlightOrigin, primeFlightDate,
							primeFlightCarrier, primeFlightNumber);
					currentFlight = flightDao.save(currentFlight);
					logger.info("Flight Created: Flight Number:" + fvo.getFlightNumber() + " with ID "
							+ currentFlight.getId() + " and hash " + currentFlight.getIdTag());
				}
				/*
				 * Update the information on a prime flight that can change. Always save the
				 * most recent one as it will contain the most up to date information.
				 */
				MutableFlightDetails mfd = mutableFlightDetailsRepository.findById(currentFlight.getId())
						.orElse(new MutableFlightDetails(currentFlight.getId()));
				BeanUtils.copyProperties(fvo, mfd, getNullPropertyNames(fvo));
				mfd.setEtaDate(DateUtils.stripTime(mfd.getLocalEtaDate()));
				if (mfd.getLocalEtdDate() == null) {
					Date primeFlightTimeStamp = new Date(Long.parseLong(primeFlightKey[ETD_DATE_WITH_TIMESTAMP]));
					// Special case where prime flight doesnt have a timestamp
					// we use the prime TDT with the 125 or 87 LOC code \ 189 DTM code for APIS
					// or tvl level 0 etd timestamp for PNR if this is the case.
					mfd.setLocalEtdDate(primeFlightTimeStamp);
				}
				String originAirport = currentFlight.getOrigin();
				String destinationAirport = currentFlight.getDestination();
				Date utcETADate = gtasLocalToUTCService.convertFromAirportCode(destinationAirport,
						fvo.getLocalEtaDate());
				Date utcETDDate = gtasLocalToUTCService.convertFromAirportCode(originAirport, fvo.getLocalEtdDate());
				mfd.setEta(utcETADate);
				mfd.setEtd(utcETDDate);
				mfd = mutableFlightDetailsRepository.save(mfd);
				currentFlight.setMutableFlightDetails(mfd);
				primeFlight = currentFlight;
				primeFlight.setParserUUID(fvo.getUuid());
				logger.debug("processFlightsAndPassenger: check for existing flights time= "
						+ (System.nanoTime() - startTime) / 1000000);
				messageFlights.add(currentFlight);
				FlightLeg leg = new FlightLeg();
				leg.setFlight(currentFlight);
				leg.setLegNumber(i);
				flightLegs.add(leg);
			}
			if (primeFlight != null) { // break when prime flight is found.
				break;
			}
		}

		if (primeFlight == null) {
			throw new NoPrimeFlightException("No prime flight. ERROR!!!!!");
		}
		// Now that we have a prime flight populate the various other flights on the
		// trip.
		// All flights that are not prime flights are considered booking details.
		// Note there is only one "prime" flight per message. The prime flight is the
		// reason the message was received.
		// The "booking details" below are all flights received along with the prime
		// flight on a message.
		for (int i = 0; i < flights.size(); i++) {
			FlightVo fvo = flights.get(i);
			if (!utils.isPrimeFlight(fvo, primeFlightOrigin, primeFlightDest, primeFlightDateString)) {
				BookingDetail bD = utils.convertFlightVoToBookingDetail(fvo);
				List<BookingDetail> existingBookingDetail = bookingDetailDao.getBookingDetailByCriteria(
						bD.getFullFlightNumber(), bD.getDestination(), bD.getOrigin(), bD.getEtd(),
						primeFlight.getId());
				if (existingBookingDetail.isEmpty()) {
					bD.setFlight(primeFlight);
					bD.setFlightId(primeFlight.getId());
					bD = bookingDetailDao.save(bD);
				} else {
					bD = existingBookingDetail.get(0);
				}
				bD.setParserUUID(fvo.getUuid());
				bookingDetails.add(bD);
				FlightLeg leg = new FlightLeg();
				leg.setBookingDetail(bD);
				leg.setLegNumber(i);
				flightLegs.add(leg);
			}
		}
		return primeFlight;
	}

	@Override
	public PassengerInformationDTO makeNewPassengerObjects(Flight primeFlight, List<PassengerVo> passengers,
			Set<Passenger> messagePassengers, Set<BookingDetail> bookingDetails, Message message)
			throws ParseException {

		Set<Passenger> newPassengers = new HashSet<>();
		Set<Passenger> oldPassengers = new HashSet<>();
		Set<Long> oldPassengersId = new HashSet<>();
		boolean isPnrMessage = false;
		boolean isApisMessage = false;
		if (message instanceof Pnr) {
			isPnrMessage = true;
		} else {
			isApisMessage = true;
		}
		// Both PNR and APIS have a transmission date.
		// To be backwards compatible we will check each value instead of
		// changing the data model to bring up the edifact message to the message
		// instead of the sub classes.
		Date transmissionDate = null;
		if (isPnrMessage) {
			Pnr thisMessage = (Pnr) message;
			transmissionDate = thisMessage.getEdifactMessage().getTransmissionDate();
		} else if (isApisMessage){
			ApisMessage thisMessage = (ApisMessage) message;
			transmissionDate = thisMessage.getEdifactMessage().getTransmissionDate();
		}

		Integer hoursBeforeTakeOff = loaderServices.getHoursBeforeTakeOff(primeFlight, transmissionDate);
		Set<Document> documents = new HashSet<>();
		for (PassengerVo pvo : passengers) {
			Passenger existingPassenger = loaderServices.findPassengerOnFlight(primeFlight, pvo);
			if (existingPassenger == null) {
				Passenger newPassenger = utils.createNewPassenger(pvo, message);
				newPassenger.getBookingDetails().addAll(bookingDetails);
				newPassenger.setParserUUID(pvo.getPassengerVoUUID());
				if (hoursBeforeTakeOff != null) {
					newPassenger.getPassengerTripDetails().setHoursBeforeTakeOff(hoursBeforeTakeOff);
				}
				for (DocumentVo dvo : pvo.getDocuments()) {
					Document d = utils.createNewDocument(dvo, message);
					newPassenger.addDocument(d);
					documents.add(d);
				}
				setHasMessage(isPnrMessage, newPassenger);
				createSeatAssignment(pvo.getSeatAssignments(), newPassenger, primeFlight);
				LoaderUtils.calculateValidVisaDays(primeFlight, newPassenger);
				newPassengers.add(newPassenger);
			} else if (!oldPassengersId.contains(existingPassenger.getId())) {
				setHasMessage(isPnrMessage, existingPassenger);
				existingPassenger.setParserUUID(pvo.getPassengerVoUUID());
				existingPassenger.getBookingDetails().addAll(bookingDetails);
				oldPassengersId.add(existingPassenger.getId());
				updatePassenger(existingPassenger, pvo, message);
				if (hoursBeforeTakeOff != null) {
					existingPassenger.getPassengerTripDetails().setHoursBeforeTakeOff(hoursBeforeTakeOff);
				}
				messagePassengers.add(existingPassenger);
				logger.debug("@ createSeatAssignment");
				createSeatAssignment(pvo.getSeatAssignments(), existingPassenger, primeFlight);
				logger.debug("@ createBags");
				oldPassengers.add(existingPassenger);
			}
		}

		for (Document d: documents) {
			d.getMessages().add(message);
			message.getDocuments().add(d);
		}
		messagePassengers.addAll(oldPassengers);
		PassengerInformationDTO passengerInformationDTO = new PassengerInformationDTO();
		passengerInformationDTO.setNewPax(newPassengers);
		return passengerInformationDTO;
	}


	private void setHasMessage(boolean isPnrMessage, Passenger existingPassenger) {
		if (isPnrMessage) {
			existingPassenger.getDataRetentionStatus().setHasPnrMessage(true);
		} else {
			existingPassenger.getDataRetentionStatus().setHasApisMessage(true);
		}
	}

	@Override
	public int createPassengers(Set<Passenger> newPassengers, Set<Passenger> messagePassengers,
			Flight primeFlight, Set<BookingDetail> bookingDetails) {

		Iterable<Passenger> pax = passengerDao.saveAll(newPassengers);
		for (Passenger p : pax) {
			messagePassengers.add(p);
		}
		for (Passenger p : newPassengers) {
			try {
				PassengerIDTag paxIdTag = utils.createPassengerIDTag(p);
				p.setPassengerIDTag(paxIdTag);
			} catch (Exception ignored) {
				logger.error("Failed to make a pax hash id from pax with id " + p.getId()
						+ ". Passenger lacks fname, lname, gender, or dob. ");
			}
		}

		Set<FlightPassenger> flightPassengers = new HashSet<>();
		for (Passenger p : newPassengers) {
			FlightPassenger fp = new FlightPassenger();
			fp.setPassengerId(p.getId());
			fp.setFlightId(primeFlight.getId());
			flightPassengers.add(fp);
		}
		flightPassengerRepository.saveAll(flightPassengers);
		return newPassengers.size();
	}

	public void updateFlightPassengerCount(Flight primeFlight, int createdPassengers) {
		FlightPassengerCount flightPassengerCount = flightPassengerCountRepository.findById(primeFlight.getId())
				.orElse(null);
		if (flightPassengerCount == null) {
			flightPassengerCount = new FlightPassengerCount(primeFlight.getId(), createdPassengers);
			flightPassengerCount = 		flightPassengerCountRepository.save(flightPassengerCount);
		} else {
			int currentPassengers = flightPassengerCount.getPassengerCount();
			flightPassengerCount.setPassengerCount(currentPassengers + createdPassengers);
			flightPassengerCount = 		flightPassengerCountRepository.save(flightPassengerCount);
		}
		primeFlight.setFlightPassengerCount(flightPassengerCount);

	}

	@Override
	public Map<UUID, BagMeasurements> saveBagMeasurements(Set<BagMeasurementsVo> bagMeasurementsToSave) {
		Map<UUID, BagMeasurements> uuidBagMeasurementsMap = new HashMap<>();
		for (BagMeasurementsVo bagMeasurementsVo : bagMeasurementsToSave) {
			BagMeasurements bagMeasurements = new BagMeasurements();
			bagMeasurements.setBagCount(bagMeasurementsVo.getQuantity());
			if (bagMeasurementsVo.getWeightInKilos() != null) {
				long rounded = Math.round(bagMeasurementsVo.getWeightInKilos());
				bagMeasurements.setWeight((double) rounded);
			}
			bagMeasurements.setRawWeight(bagMeasurementsVo.getRawWeight());
			bagMeasurements.setParserUUID(bagMeasurementsVo.getUuid());
			bagMeasurements.setMeasurementIn(bagMeasurementsVo.getMeasurementType());
			bagMeasurementsRepository.save(bagMeasurements);
			uuidBagMeasurementsMap.put(bagMeasurements.getParserUUID(), bagMeasurements);
		}
		return uuidBagMeasurementsMap;
	}

	/**
	 * Create a single seat assignment for the given passenger, flight combination.
	 * TODO: Inefficient to have to pass in the entire list of seat assignments from
	 * the paxVo.
	 *
	 * @param seatAssignments
	 *            seatAssignment Value Object
	 * @param p
	 *            Passenger to add seat to
	 * @param f
	 *            Flight passenger is on.
	 */
	private void createSeatAssignment(List<SeatVo> seatAssignments, Passenger p, Flight f) {
		for (SeatVo seat : seatAssignments) {
			if (seat.getDestination().equals(f.getDestination()) && seat.getOrigin().equals(f.getOrigin())) {
				Seat s = new Seat();
				s.setPassenger(p);
				s.setFlight(f);
				s.setNumber(seat.getNumber());
				s.setPassengerId(p.getId());
				s.setApis(seat.getApis());
				s.setCabinClass(seat.getCabinClass());
				boolean alreadyExistsSeat = false;
				for (Seat s2 : p.getSeatAssignments()) {
					if (s.equals(s2)) {
						alreadyExistsSeat = true;
						if (s.getCabinClass() != null &&
								!s.getCabinClass().equals(s2.getCabinClass())) {
							s2.setCabinClass(s.getCabinClass());
						}
					}
				}
				if (!alreadyExistsSeat) {
					p.getSeatAssignments().add(s);
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
			if (pvo.getPaymentAmount() != null) {
				try {
					String paymentAmount = pvo.getPaymentAmount();
					paymentAmount = paymentAmount.replaceAll("[\\D.]", ".");
					double dollarAmount = Double.parseDouble(paymentAmount);
					pf.setWholeDollarAmount((int) dollarAmount);
				} catch (NumberFormatException nfe) {
					logger.warn("Payment amount is likely corrupt! Unable to create double or set int!");
				}
			}
			pf.setPnr(pnr);
			chkList.add(pf);
		}
		paymentFormDao.saveAll(chkList);
	}

	@Override
	public void updatePassenger(Passenger existingPassenger, PassengerVo pvo, Message message) throws ParseException {

		PassengerDetailFromMessage passengerDetailFromMessage = fromVoAndMessage(pvo, message, existingPassenger);
		existingPassenger.getPassengerDetailFromMessages().add(passengerDetailFromMessage);
		MessageType messageType = utils.getMessageType(message);
		utils.updatePassenger(pvo, existingPassenger);
		for (DocumentVo dvo : pvo.getDocuments()) {
			List<Document> existingDocs;
			if (dvo.getDocumentNumber() != null) {
				existingDocs = docDao.findByDocumentNumberAndPassengerAndMessageType(dvo.getDocumentNumber(), existingPassenger, messageType);
				if (existingDocs.isEmpty()) {
					Document d = utils.createNewDocument(dvo, message);
					message.getDocuments().add(d);
					d.getMessages().add(message);
					existingPassenger.addDocument(d);
				} else {
					Document d = existingDocs.get(0);
					message.getDocuments().add(d);
					d.getMessages().add(message);
					utils.updateDocument(dvo, d); // For legacy data, always grab first amongst
												  // potential list of docs
				}
			}
		}
	}
}
