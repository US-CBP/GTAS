package gov.gtas.services;

import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.enumtype.MessageType;
import gov.gtas.enumtype.TripTypeEnum;
import gov.gtas.model.*;
import gov.gtas.parsers.util.TextUtils;
import gov.gtas.parsers.vo.*;
import gov.gtas.repository.*;
import gov.gtas.summary.*;
import gov.gtas.util.LobUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
public class GenericLoadingServiceImpl implements GenericLoading {

    private static final Logger logger = LoggerFactory.getLogger(GenericLoadingServiceImpl.class);

    final
    FlightRepository flightRepository;

    @Value("${loader.country}")
    String country;

    @Value("${default.external.hit}")
    String defaultExternalHitCategory;

    private final MutableFlightDetailsRepository mutableFlightDetailsRepository;

    private final BookingDetailRepository bookingDetailRepository;

    private final LoaderServices loaderServices;

    private final HitMakerRepository hitMakerRepository;

    final
    GtasLoader gtasLoader;

    private final PnrRepository pnrRepository;

    private final ApisMessageRepository apisMessageRepository;

    private final MessageStatusRepository messageStatusRepository;

    @Value("${additional.processing.enabled.passenger}")
    private Boolean additionalProcessing;

    public GenericLoadingServiceImpl(FlightRepository flightRepository,
                                     MutableFlightDetailsRepository mutableFlightDetailsRepository,
                                     BookingDetailRepository bookingDetailRepository,
                                     LoaderServices loaderServices,
                                     HitMakerRepository hitMakerRepository, GtasLoader gtasLoader,
                                     PnrRepository pnrRepository,
                                     ApisMessageRepository apisMessageRepository,
                                     MessageStatusRepository messageStatusRepository) {
        this.flightRepository = flightRepository;
        this.mutableFlightDetailsRepository = mutableFlightDetailsRepository;
        this.bookingDetailRepository = bookingDetailRepository;
        this.loaderServices = loaderServices;
        this.hitMakerRepository = hitMakerRepository;
        this.gtasLoader = gtasLoader;
        this.pnrRepository = pnrRepository;
        this.apisMessageRepository = apisMessageRepository;
        this.messageStatusRepository = messageStatusRepository;
    }

    @Transactional
    @Override
    public MessageInformation load(MessageSummary messageSummary, String filePath) {

        MessageStatus messageStatus = new MessageStatus();
        messageStatus.setMessageStatusEnum(MessageStatusEnum.RECEIVED);
        messageStatus.setNoLoadingError(true);
        MessageInformation messageInformation = new MessageInformation();
        EventIdentifier eventIdentifier = messageSummary.getEventIdentifier();

        if (isPnr(eventIdentifier)) {
            Pnr pnr = new Pnr();
            pnr = bootstrapMessage(filePath, messageStatus, pnr, pnrRepository);
            messageStatus = pnr.getStatus();
            try {
                // Load flight and passenger
                loadEventAndPassenger(messageSummary, messageStatus, eventIdentifier, pnr);

                // Set hit information, if any
                messageInformation.getPendingHitDetailsSet().addAll(pnr.getPendingHitDetails());

                // PNR Specific Loading
                pnr.setRecordLocator(messageSummary.getPnrRefNumber());
                gtasLoader.calculateDwellTimes(pnr);
                TripTypeEnum tripType = gtasLoader.calculateTripType(pnr.getFlightLegs(), pnr.getDwellTimes());
                pnr.setTripType(tripType.toString());
                populatePnrObjects(messageSummary, pnr);

                // Prep message for sending to additional processing
                if (additionalProcessing) {
                    String rawMessage = messageSummary.getRawMessage();
                    String[] eventKey = messageSummary.getEventIdentifier().getIdentifierArrayList().toArray(new String[0]);
                    gtasLoader.prepareAdditionalProcessing(messageInformation, pnr, eventKey, rawMessage);
                }
            } catch (Exception e) {
                pnr.getStatus().setNoLoadingError(false);
                pnr.setError(e.toString());
                GtasLoaderImpl.handleException(e, pnr);
            } finally {
                boolean success = gtasLoader.createMessage(pnr);
                if (!success) {
                    logger.error("Message failed to save in the database!");
                    messageStatus.setNoLoadingError(false);
                    messageStatus.setMessageStatusEnum(MessageStatusEnum.FAILED_LOADING);
                    messageInformation.setMessageStatus(messageStatus);
                }
                messageInformation.setMessageStatus(messageStatus);

            }
        } else if (isApis(eventIdentifier))  {
            ApisMessage apisMessage = new ApisMessage();
            apisMessage = bootstrapMessage(filePath, messageStatus, apisMessage, apisMessageRepository);
            messageStatus = apisMessage.getStatus();
            try {
                // Load flight and passenger
                loadEventAndPassenger(messageSummary, messageStatus, eventIdentifier, apisMessage);

                // Set hit information, if any
                messageInformation.getPendingHitDetailsSet().addAll(apisMessage.getPendingHitDetails());

                // Prep message for sending to additional processing
                if (additionalProcessing) {
                    String rawMessage = messageSummary.getRawMessage();
                    String[] eventKey = messageSummary.getEventIdentifier().getIdentifierArrayList().toArray(new String[0]);
                    gtasLoader.prepareAdditionalProcessing(messageInformation, apisMessage, eventKey, rawMessage);
                }
            } catch (Exception e) {
                messageStatus.setNoLoadingError(false);
                messageStatus.setMessageStatusEnum(MessageStatusEnum.FAILED_LOADING);
                GtasLoaderImpl.handleException(e, apisMessage);
            } finally {
                if (!gtasLoader.createMessage(apisMessage)) {
                    logger.error("Message failed to save in the database!");
                    apisMessage.getStatus().setNoLoadingError(false);
                    apisMessage.getStatus().setMessageStatusEnum(MessageStatusEnum.FAILED_LOADING);
                }
            }
            messageInformation.setMessageStatus(messageStatus);
        } else {
            // Any generic message implementation for non-pnr or apis messages in the future can go here.
            throw new IllegalStateException("Unsupported Event Type!");
        }

        return messageInformation;
    }

    private void loadEventAndPassenger(MessageSummary messageSummary, MessageStatus messageStatus, EventIdentifier eventIdentifier, Message message) throws LoaderException {

        //Check hash codes
        String hashCode = getHashCode(messageSummary);
        gtasLoader.checkHashCode(hashCode);
        message.setHashCode(hashCode);

        //Set Edifact related fields
        message.setStatus(messageStatus);
        EdifactMessage edifactMessage = getEdifactMessage(messageSummary);
        message.setEdifactMessage(edifactMessage);
        message.setRaw(LobUtils.createClob(messageSummary.getRawMessage()));

        //Make flight and passenger. Save temp hits to message (as passengers are created)
        Flight bcEvent = ProcessFlightAndPassenger(messageSummary, eventIdentifier, message);

        message.setPassengerCount(message.getPassengers().size());
        message.getFlights().add(bcEvent);
        updateMessageStatus(messageStatus, bcEvent);
    }

    private String getHashCode(MessageSummary messageSummary) {
        String hashCode;
        if (messageSummary.getHashCode() == null && messageSummary.getRawMessage() != null) {
            hashCode = TextUtils.getMd5Hash(messageSummary.getRawMessage(), StandardCharsets.UTF_8);
        } else if (messageSummary.getHashCode() == null) {
            throw new RuntimeException("Hash code not found or able to be created!");
        } else {
            hashCode = messageSummary.getHashCode();
        }
        return hashCode;
    }

    private <T extends Message> T bootstrapMessage(String filePath, MessageStatus messageStatus, T message,
                                                         MessageRepository<T> repository) {
        message.setCreateDate(new Date());
        message.setFilePath(filePath);
        message = repository.save(message);
        message.setStatus(messageStatus);
        messageStatus.setMessageId(message.getId());
        messageStatus = messageStatusRepository.save(messageStatus);
        messageStatus.setNoLoadingError(true);
        message.setStatus(messageStatus);
        message = repository.save(message);
        return message;
    }

    private void updateMessageStatus(MessageStatus messageStatus, Flight bcEvent) {
        messageStatus.setMessageStatusEnum(MessageStatusEnum.PARSED);
        messageStatus.setFlightId(bcEvent.getId());
        messageStatus.setFlight(bcEvent);
    }

    private Flight ProcessFlightAndPassenger(MessageSummary messageSummary, EventIdentifier eventIdentifier, Message pnr) {
        Flight bcEvent = processMessageTravel(messageSummary, eventIdentifier, pnr);
        int passengerCount = processPassengerInformation(messageSummary, pnr, bcEvent);

        // Pending hit details require a passenger Id to process, but one is not guaranteed until passengers are fully processed.
        // processPassengerInformation will add a mostly completed pending hit detail to the message. This will add the ID
        // and the passenger object needed to save correctly.
        addPassengerToPendingHitDetails(pnr);
        gtasLoader.updateFlightPassengerCount(bcEvent, passengerCount);
        return bcEvent;
    }

    private void addPassengerToPendingHitDetails(Message pnr) {
        List<PendingHitDetails> phdList = pnr.getPendingHitDetails();
        Map<UUID, Passenger> paxMap = pnr.getPassengers().stream().collect(
                Collectors.toMap(Passenger::getUuid, Function.identity()));
        for (PendingHitDetails phd : phdList) {
            Passenger p = paxMap.get(phd.getPassengerGUID());
            phd.setPassenger(p);
            phd.setPassengerId(p.getId());
        }
    }

    private int processPassengerInformation(MessageSummary messageSummary, Message message, Flight bcEvent) {

        Set<BookingDetail> bd = message.getBookingDetails();
        Set<Passenger> pax = message.getPassengers();
        List<PassengerSummary> passengerSummaries = messageSummary.getPassengerSummaries();
        String recordLocatorNumber = messageSummary.getPnrRefNumber();
        Integer hoursBeforeTakeOff = loaderServices.getHoursBeforeTakeOff(bcEvent, messageSummary.getTransmissionDate());
        Set<Passenger> newPassengers = new HashSet<>();
        MessageType mt =  MessageType.fromString(messageSummary.getMessageType()).orElse(MessageType.NO_TYPE);
        List<PendingHitDetails> phdList = new ArrayList<>();
        for (PassengerSummary ps : passengerSummaries) {
            Optional<Passenger> passengerOptional = loaderServices.findPassengerOnFlight(bcEvent, ps, recordLocatorNumber);
            if (passengerOptional.isPresent()) {
                Passenger existingPassenger = passengerOptional.get();

                PassengerDetails passengerDetails = existingPassenger.getPassengerDetails();
                populatePassengerDetails(ps, passengerDetails);

                createPendingHitDetails(bcEvent, phdList, ps, existingPassenger);
                PassengerTripDetails tripDetails = existingPassenger.getPassengerTripDetails();
                populatePassengerTripInfo(hoursBeforeTakeOff, ps, tripDetails);
                LoaderUtils.calculateValidVisaDays(bcEvent, existingPassenger);
                existingPassenger.getPassengerTripDetails().setHoursBeforeTakeOff(hoursBeforeTakeOff);
                existingPassenger.getBookingDetails().addAll(bd);
                existingPassenger.getDataRetentionStatus().setHasPnrMessage(true);
                for (PassengerDocument passDoc : ps.getPassengerDocumentsList()) {
                    Document messageDoc = SummaryFactory.from(passDoc, mt, bcEvent, existingPassenger);
                    for (Document d : existingPassenger.getDocuments()) {
                        if (messageDoc.equals(d)) {
                            messageDoc = d;
                            break;
                        }
                    }
                    messageDoc.getMessages().add(message);
                    existingPassenger.getDocuments().add(messageDoc);
                    message.getDocuments().add(messageDoc);
                    if (mt == MessageType.PNR) {
                        existingPassenger.getDataRetentionStatus().setHasPnrMessage(true);
                    } else if (mt == MessageType.APIS) {
                        existingPassenger.getDataRetentionStatus().setHasApisMessage(true);
                    }
                }
                existingPassenger.getBookingDetails().addAll(message.getBookingDetails());
                PassengerDetailFromMessage pdfm = fromVoAndMessage(ps, message, existingPassenger);
                existingPassenger.getPassengerDetailFromMessages().add(pdfm);
                pax.add(existingPassenger);
            } else {
                Passenger passenger = new Passenger();
                PassengerDetails passengerDetails = new PassengerDetails(passenger);
                passenger.setPassengerDetails(passengerDetails);
                populatePassengerDetails(ps, passengerDetails);
                PassengerTripDetails passengerTripDetails = new PassengerTripDetails(passenger);
                populatePassengerTripInfo(hoursBeforeTakeOff, ps, passengerTripDetails);
                passenger.setPassengerTripDetails(passengerTripDetails);
                for (PassengerDocument passDoc : ps.getPassengerDocumentsList()) {
                    Document messageDoc = SummaryFactory.from(passDoc, mt, bcEvent, passenger);
                    messageDoc.getMessages().add(message);
                    passenger.getDocuments().add(messageDoc);
                    message.getDocuments().add(messageDoc);
                }
                passenger.getBookingDetails().addAll(message.getBookingDetails());
                newPassengers.add(passenger);
                PassengerDetailFromMessage pdfm = fromVoAndMessage(ps, message, passenger);
                passenger.getPassengerDetailFromMessages().add(pdfm);
                createPendingHitDetails(bcEvent, phdList, ps, passenger);
                if (mt == MessageType.PNR) {
                    passenger.getDataRetentionStatus().setHasPnrMessage(true);
                } else if (mt == MessageType.APIS) {
                    passenger.getDataRetentionStatus().setHasApisMessage(true);
                }
            }
        }
        message.getPendingHitDetails().addAll(phdList);
        return gtasLoader.createPassengers(newPassengers, pax, bcEvent, bd);
    }

    private void createPendingHitDetails(Flight bcEvent, List<PendingHitDetails> phdList, PassengerSummary ps, Passenger existingPassenger) {
        List<PassengerHit> passengerHits = ps.getPassengerHits();
        for (PassengerHit ph : passengerHits) {
            String hitCategory = ph.getHitCategory();
            ExternalHit ehl;
            List<ExternalHit> hm = hitMakerRepository.getExternalHitsCategoryByName(hitCategory);
            if (hm.isEmpty()) {
                List<ExternalHit> defaultHit = hitMakerRepository.getExternalHitsCategoryByName(defaultExternalHitCategory);
                if (defaultHit.isEmpty()) {
                    logger.error("HIT ATTEMPTED TO PROCESS WITH NO CORRESPONDING HIT DETAIL. NO HIT WILL BE PROCESSED - IS DEFAULT HIT CATEGORY PROPERTY SET CORRECTLY?");
                    continue;
                } else {
                    ehl = defaultHit.get(0);
                }
            } else {
                ehl = hm.get(0);
            }
            PendingHitDetails phd = new PendingHitDetails();
            phd.setHitEnum(HitTypeEnum.EXTERNAL_HIT);
            phd.setCreatedDate(new Date());
            phd.setCreatedBy("LOADER");
            phd.setDescription(ph.getDescription());
            phd.setFlight(bcEvent);
            phd.setFlightId(bcEvent.getId());
            phd.setPassengerGUID(existingPassenger.getUuid());
            phd.setHitType(HitTypeEnum.EXTERNAL_HIT.toString());
            phd.setPercentage(ph.getPercentage());
            phd.setRuleConditions(ph.getRuleConditions());
            phd.setHitMakerId(ehl.getId());
            phd.setHitMaker(ehl);
            phd.setTitle(ph.getTitle());
            phdList.add(phd);
        }
    }

    public static PassengerDetailFromMessage fromVoAndMessage(PassengerSummary passengerSummary, Message message, Passenger p) {
        PassengerDetailFromMessage passengerDetailFromMessage = new PassengerDetailFromMessage(p);
        populatePassengerDetailsFromMessage(passengerSummary, passengerDetailFromMessage);
        EdifactMessage em = message.getEdifactMessage();
        if ("PNRGOV".equalsIgnoreCase(em.getMessageType()) || "PNR".equalsIgnoreCase(em.getMessageType())) {
            passengerDetailFromMessage.setMessageType(MessageType.PNR);

        } else if ("PAXLST".equalsIgnoreCase(em.getMessageType()) || "APIS".equalsIgnoreCase(em.getMessageType())) {
            passengerDetailFromMessage.setMessageType(MessageType.APIS);
        }
        passengerDetailFromMessage.setMessage(message);
        return passengerDetailFromMessage;
    }

    private static void populatePassengerDetailsFromMessage(PassengerSummary passengerSummary, PassengerDetailFromMessage passengerDetailFromMessage) {
        passengerDetailFromMessage.setAge(passengerSummary.getPassengerBiographic().getAge());
        passengerDetailFromMessage.setDob(passengerSummary.getPassengerBiographic().getDob());
        passengerDetailFromMessage.setFirstName(passengerSummary.getPassengerBiographic().getFirstName());
        passengerDetailFromMessage.setLastName(passengerSummary.getPassengerBiographic().getLastName());
        passengerDetailFromMessage.setGender(passengerSummary.getPassengerBiographic().getGender());
        passengerDetailFromMessage.setMiddleName(passengerSummary.getPassengerBiographic().getMiddleName());
        passengerDetailFromMessage.setNationality(passengerSummary.getPassengerBiographic().getNationality());
        passengerDetailFromMessage.setPassengerType(passengerSummary.getPassengerBiographic().getPassengerType());
        passengerDetailFromMessage.setTitle(passengerSummary.getPassengerBiographic().getTitle());
        passengerDetailFromMessage.setSuffix(passengerSummary.getPassengerBiographic().getSuffix());
        passengerDetailFromMessage.setResidencyCountry(passengerSummary.getPassengerBiographic().getResidencyCountry());
    }

    //Populate PNR message
    private void populatePnrObjects(MessageSummary messageSummary, Pnr pnr) {
        PnrVo pnrVo = new PnrVo();
        List<AddressVo> addressVos = messageSummary.getMessageAddresses()
                .stream().map(paddr -> {
                            AddressVo addressVo = new AddressVo();
                            addressVo.setCity(paddr.getCity());
                            addressVo.setCountry(paddr.getCountry());
                            addressVo.setLine1(paddr.getLine1());
                            addressVo.setLine2(paddr.getLine2());
                            addressVo.setLine3(paddr.getLine3());
                            addressVo.setState(paddr.getState());
                            addressVo.setPostalCode(paddr.getPostalCode());
                            return addressVo;
                        }
                ).collect(Collectors.toList());


        List<PhoneVo> phoneVos = messageSummary.getMessagePhones()
                .stream().map(pmsg -> {
                    PhoneVo pVo = new PhoneVo();
                    pVo.setNumber(pmsg.getNumber());
                    return pVo;
                }).collect(Collectors.toList());

        List<EmailVo> emailVos = messageSummary.getMessageEmails()
                .stream().map(emsg -> {
                            EmailVo eVo = new EmailVo();
                            eVo.setAddress(emsg.getAddress());
                            eVo.setDomain(emsg.getDomain());
                            return eVo;
                        }).collect(Collectors.toList());

        List<CreditCardVo> creditCardVos = messageSummary.getMessageCreditCards()
                .stream().map(cc -> {
                    CreditCardVo ccVo = new CreditCardVo();
                    ccVo.setAccountHolder(cc.getAccountHolder());
                    ccVo.setAccountHolderAddress(cc.getAccountHolderAddress());
                    ccVo.setAccountHolderPhone(cc.getAccountHolderPhone());
                    ccVo.setCardType(cc.getCardType());
                    ccVo.setNumber(cc.getNumber());
                    ccVo.setExpiration(cc.getExpiration());
                    return ccVo;
                }).collect(Collectors.toList());


        List<FrequentFlyerVo> ffVos = messageSummary.getMessageFrequentFlyers()
                .stream().map(fo -> {
                    FrequentFlyerVo ffVo = new FrequentFlyerVo();
                    ffVo.setCarrier(fo.getCarrier());
                    ffVo.setNumber(fo.getNumber());
                    return ffVo;
                }).collect(Collectors.toList());

        pnrVo.setPhoneNumbers(phoneVos);
        pnrVo.setAddresses(addressVos);
        pnrVo.setEmails(emailVos);
        pnrVo.setCreditCards(creditCardVos);
        pnrVo.setFrequentFlyerDetails(ffVos);
        gtasLoader.processPnr(pnr, pnrVo);
    }

    private void populatePassengerTripInfo(Integer hoursBeforeTakeOff, PassengerSummary ps, PassengerTripDetails tripDetails) {
        PassengerTrip pt = ps.getPassengerTrip();
        tripDetails.setHoursBeforeTakeOff(hoursBeforeTakeOff);
        tripDetails.setCoTravelerCount(pt.getCoTravelerCount());
        tripDetails.setDebarkation(pt.getDebarkation());
        tripDetails.setDebarkCountry(pt.getDebarkCountry());
        tripDetails.setEmbarkation(pt.getEmbarkation());
        tripDetails.setEmbarkCountry(pt.getEmbarkCountry());
        tripDetails.setPnrReservationReferenceNumber(pt.getPnrReservationReferenceNumber());
        tripDetails.setReservationReferenceNumber(pt.getReservationReferenceNumber());
    }

    private void populatePassengerDetails(PassengerSummary ps, PassengerDetails passengerDetails) {
        PassengerDetails.mapFields(ps, passengerDetails);
    }

    private EdifactMessage getEdifactMessage(MessageSummary messageSummary) {
        EdifactMessage edifactMessage = new EdifactMessage();
        edifactMessage.setMessageType(messageSummary.getMessageType());
        edifactMessage.setTransmissionDate(messageSummary.getTransmissionDate());
        edifactMessage.setTransmissionSource(messageSummary.getTransmissionSource());
        edifactMessage.setVersion(messageSummary.getSourceMessageVersion());
        return edifactMessage;
    }

    private Flight processMessageTravel(MessageSummary messageSummary, EventIdentifier eventIdentifier, Message message) {
        List<MessageTravelInformation> messageTravelInformation = messageSummary.getMessageTravelInformation();

        //Make sure one and only one border crossing event is listed.
        MessageTravelInformation bc = messageTravelInformation.stream()
                .filter(MessageTravelInformation::isBorderCrossingEvent)
                .reduce((a, b) -> {
                    throw new IllegalStateException("More than one message in Message Travel Information is designated a border crossing event!");
                })
                .orElseThrow(() -> new IllegalStateException("No Border Crossing Event!"));

        // Make border crossing
        Flight bcEvent = processBorderCrossingMessageTravel(eventIdentifier, bc);

        // order for travel legs so flight leg number is correct - it uses the index of a for loop
        // to tell which leg it is. etd is UTC time so we can use it for comparisons.
        messageTravelInformation.sort(Comparator.comparing(MessageTravelInformation::getEtd));

        for (int i = 0; i < messageTravelInformation.size(); i++) {
            MessageTravelInformation mti = messageTravelInformation.get(i);
            if (bc == mti) { // intentional pointer comparison. should be reference BC that made event.
                FlightLeg fl = new FlightLeg();
                fl.setFlight(bcEvent);
                fl.setLegNumber(i);
                fl.setMessage(message);
                message.getFlightLegs().add(fl);
            } else {
                // Make booking details and booking detail flight legs
                String destination = mti.getDestination();
                String origin = mti.getOrigin();
                String fullFlightNumber = mti.getFullFlightNumber();
                Date etd = mti.getEtd();
                List<BookingDetail> bdList = bookingDetailRepository.getBookingDetailByCriteria(fullFlightNumber, destination, origin,etd, bcEvent.getId());
                BookingDetail bd;
                if (bdList.isEmpty()) {
                    bd = SummaryFactory.from(mti, bcEvent);
                    bd = bookingDetailRepository.save(bd);
                } else {
                    bd = bdList.get(0);
                }
                FlightLeg leg = new FlightLeg();
                leg.setBookingDetail(bd);
                leg.setLegNumber(i);
                leg.setMessage(message);
                message.getFlightLegs().add(leg);
                message.getBookingDetails().add(bd);
            }
        }
        return bcEvent;
    }

    private Flight processBorderCrossingMessageTravel(EventIdentifier eventIdentifier, MessageTravelInformation bc) {

        Flight primeFlight = flightRepository.findFlightByIdTag(bc.getIdTag());
        if (primeFlight == null) {
            //create new prime flight.
            Flight flight = SummaryFactory.from(bc, country);
            primeFlight = flightRepository.save(flight);
        }
        /*
         * Update the information on a prime flight that can change. Always save the
         * most recent one as it will contain the most up to date information.
         */
        MutableFlightDetails mfd = mutableFlightDetailsRepository
                .findById(primeFlight.getId())
                .orElse(new MutableFlightDetails(primeFlight.getId()));
        mfd.setEtaDate(bc.getEtaDate());
        mfd.setLocalEtaDate(bc.getLocalEtaDate());
        mfd.setLocalEtdDate(bc.getLocalEtdDate());
        if (mfd.getLocalEtdDate() == null) {
            int ETD_DATE_WITH_TIMESTAMP = 5;
            Date primeFlightTimeStamp = new Date(Long.parseLong(eventIdentifier.getIdentifierArrayList().get(ETD_DATE_WITH_TIMESTAMP)));
            // Special case where prime flight doesnt have a timestamp
            mfd.setLocalEtdDate(primeFlightTimeStamp);
        }

        mfd.setEta(bc.getEta());
        mfd.setEtd(bc.getEtd());
        mfd = mutableFlightDetailsRepository.save(mfd);
        primeFlight.setMutableFlightDetails(mfd);
        return primeFlight;
    }

    private boolean isApis(EventIdentifier eventIdentifier) {
        return "APIS".equalsIgnoreCase(eventIdentifier.getEventType())
                || "APIS_PASSENGER".equalsIgnoreCase(eventIdentifier.getEventType())
                || "RULE_APIS".equalsIgnoreCase(eventIdentifier.getEventType());
    }

    private boolean isPnr(EventIdentifier eventIdentifier) {
        return "PNR".equalsIgnoreCase(eventIdentifier.getEventType())
        || "PNR_PASSENGER".equalsIgnoreCase(eventIdentifier.getEventType())
        || "RULE_PNR".equalsIgnoreCase(eventIdentifier.getEventType());
    }
}
