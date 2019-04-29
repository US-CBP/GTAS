/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.request.builder;

import gov.gtas.bo.BasicRuleServiceRequest;
import gov.gtas.bo.RuleServiceRequest;
import gov.gtas.bo.RuleServiceRequestType;
import gov.gtas.bo.match.PnrAddressLink;
import gov.gtas.bo.match.PnrBookingLink;
import gov.gtas.bo.match.PnrCreditCardLink;
import gov.gtas.bo.match.PnrDwellTimeLink;
import gov.gtas.bo.match.PnrEmailLink;
import gov.gtas.bo.match.PnrFrequentFlyerLink;
import gov.gtas.bo.match.PnrPassengerLink;
import gov.gtas.bo.match.PnrPhoneLink;
import gov.gtas.bo.match.PnrTravelAgencyLink;
import gov.gtas.model.*;

import java.util.*;
import java.util.stream.Collectors;

import gov.gtas.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Rule Engine Request Builder constructs Rule Engine execution requests from
 * APIS and PNR messages. The constructed request contains all entities (e.g.,
 * passenger, flight) associated with the APIS and PNR messages supplied.
 * Duplicate entities are removed in the construction process.
 */


@Component
public class RuleEngineRequestBuilder {
    private static final Logger logger = LoggerFactory
            .getLogger(RuleEngineRequestBuilder.class);
    private List<Object> requestObjectList;
    private Set<PnrPassengerLink> passengerLinkSet;
    private Set<Long> flightIdSet;
    private Set<Long> addressIdSet;
    private Set<Long> phoneIdSet;
    private Set<Long> emailIdSet;
    private Set<Long> creditCardIdSet;
    private Set<Long> frequentFlyerIdSet;
    private Set<Long> travelAgencyIdSet;
    private Set<Long> dwellTimeIdSet;
    private Set<Long> bookingDetailIdSet;
    private Set<PnrAddressLink> addressLinks;
    private Set<PnrPhoneLink> phoneLinks;
    private Set<PnrEmailLink> emailLinks;
    private Set<PnrCreditCardLink> creditCardLinks;
    private Set<PnrFrequentFlyerLink> frequentFlyerLinks;
    private Set<PnrTravelAgencyLink> travelAgencyLinks;
    private Set<PnrDwellTimeLink> dwellTimeLinks;
    private Set<PnrBookingLink> pnrBookingLinks;
    private Set<PassengerFlightTuple> passengerFlightSet;

    private RuleServiceRequestType requestType;


    private final
    PnrRepository pnrRepository;

    private final
    ApisMessageRepository apisMessageRepository;

    private final
    PassengerTripRepository passengerTripRepository;

    private final
    PassengerDetailRepository passengerDetailRepository;

    private final
    SeatRepository seatRepository;

    private final
    BagRepository bagRepository;

    private final
    FlightPaxRepository flightPaxRepository;

    private final
    DocumentRepository documentRepository;

    @Autowired
    public RuleEngineRequestBuilder(PnrRepository pnrRepository,
                                    PassengerTripRepository passengerTripRepository,
                                    PassengerDetailRepository passengerDetailRepository,
                                    SeatRepository seatRepository, BagRepository bagRepository,
                                    FlightPaxRepository flightPaxRepository,
                                    DocumentRepository documentRepository,
                                    ApisMessageRepository apisMessageRepository) {
        this.requestObjectList = new ArrayList<>(30000);
        this.addressIdSet = new HashSet<>();
        this.creditCardIdSet = new HashSet<>();
        this.bookingDetailIdSet = new HashSet<>();
        this.emailIdSet = new HashSet<>();
        this.flightIdSet = new HashSet<>();
        this.frequentFlyerIdSet = new HashSet<>();
        this.passengerLinkSet = new HashSet<>();
        this.phoneIdSet = new HashSet<>();
        this.travelAgencyIdSet = new HashSet<>();
        this.passengerFlightSet = new HashSet<>();
        this.dwellTimeIdSet = new HashSet<>();
        this.addressLinks = new HashSet<>();
        this.phoneLinks = new HashSet<>();
        this.emailLinks = new HashSet<>();
        this.creditCardLinks = new HashSet<>();
        this.frequentFlyerLinks = new HashSet<>();
        this.travelAgencyLinks = new HashSet<>();
        this.dwellTimeLinks = new HashSet<>();
        this.pnrBookingLinks = new HashSet<>();
        this.requestType = null;
        this.pnrRepository = pnrRepository;
        this.passengerTripRepository = passengerTripRepository;
        this.passengerDetailRepository = passengerDetailRepository;
        this.seatRepository = seatRepository;
        this.bagRepository = bagRepository;
        this.flightPaxRepository = flightPaxRepository;
        this.documentRepository = documentRepository;
        this.apisMessageRepository = apisMessageRepository;
    }

    /**
     * Builds and returns the request object.
     *
     * @return the request object.
     */
    public RuleServiceRequest build() {
        BasicRuleServiceRequest basicRuleServiceRequest = new BasicRuleServiceRequest(requestObjectList, this.requestType);
        this.requestObjectList = new ArrayList<>(300000);
        this.addressIdSet = new HashSet<>();
        this.creditCardIdSet = new HashSet<>();
        this.bookingDetailIdSet = new HashSet<>();
        this.emailIdSet = new HashSet<>();
        this.flightIdSet = new HashSet<>();
        this.frequentFlyerIdSet = new HashSet<>();
        this.passengerLinkSet = new HashSet<>();
        this.phoneIdSet = new HashSet<>();
        this.travelAgencyIdSet = new HashSet<>();
        this.passengerFlightSet = new HashSet<>();
        this.dwellTimeIdSet = new HashSet<>();
        this.addressLinks = new HashSet<>();
        this.phoneLinks = new HashSet<>();
        this.emailLinks = new HashSet<>();
        this.creditCardLinks = new HashSet<>();
        this.frequentFlyerLinks = new HashSet<>();
        this.travelAgencyLinks = new HashSet<>();
        this.dwellTimeLinks = new HashSet<>();
        this.pnrBookingLinks = new HashSet<>();
        this.requestType = null;
        return basicRuleServiceRequest;
    }

    /**
     * @return the passengerFlightSet
     */
    public Set<PassengerFlightTuple> getPassengerFlightSet() {
        return passengerFlightSet;
    }

    /**
     * Adds an Apis Message and its associated entities.
     *
     * @param apisMessage the message to add.
     */
    public void addApisMessage(List<ApisMessage> apisMessage) {
        logger.debug("Entering APIS messages");
        Set<Long> apisIds = apisMessage.stream().map(ApisMessage::getId).collect(Collectors.toSet());
        Set<Passenger> passengerSet = apisMessageRepository.getPassengerWithFlightInfo(apisIds);
        addFlights(passengerSet);
        Set<Flight> flightSet = addFlights(passengerSet);
        addFlights(flightSet);
        addPassengerInfo(passengerSet);
        logger.debug("APIS done loading.");
        if (this.requestType == null
                || this.requestType == RuleServiceRequestType.APIS_MESSAGE) {
            this.requestType = RuleServiceRequestType.APIS_MESSAGE;
        } else {
            this.requestType = RuleServiceRequestType.ANY_MESSAGE;
        }
    }

    private Set<Flight> addFlights(Set<Passenger> passengerSet) {
        Set<Flight> flightSet = new HashSet<>();
        for (Passenger p : passengerSet) {
            passengerFlightSet.add(new PassengerFlightTuple(
                    p, p.getFlight()));
            flightSet.add(p.getFlight());
        }
        return flightSet;
    }

    /**
     * Adds a PNR message and its associated entities.
     * <p>
     * the pnr to add.
     */
    public void addPnr(List<Pnr> pnrList) {
        // add PNR objects
        logger.debug("Calling DB");
        Set<Long> pnrIds = pnrList.stream().map(Pnr::getId).collect(Collectors.toSet());
        Map<Long, Set<Address>> addressObjects = createAddressMap(pnrIds);
        Map<Long, Set<Phone>> phoneObjects = createPhoneMap(pnrIds);
        Map<Long, Set<Email>> emailsObjects = createEmailMap(pnrIds);
        Map<Long, Set<CreditCard>> creditCardObjects = createCreditCardMap(pnrIds);
        Map<Long, Set<BookingDetail>> bookingDetailObjects = createBookingDetailMap(pnrIds);
        Map<Long, Set<FrequentFlyer>> frequentFlyerObjects = createFrequentFlyersMap(pnrIds);
        Map<Long, Set<Agency>> travelAgency = createTravelAgencyMap(pnrIds);
        Map<Long, Set<DwellTime>> dwellMap = createDwellTime(pnrIds);
        Map<Long, Set<Passenger>> paxMap = createPaxMap(pnrIds);
        Set<PaymentForm> paymentForms = pnrRepository.getPaymentFormsByPnrIds(pnrIds);
        addPaymentFormObjects(paymentForms);

        Set<Passenger> passengersFromPnr = pnrRepository.getPassengersWithFlight(pnrIds);
        Set<Flight> flightSet = addFlights(passengersFromPnr);
        addFlights(flightSet);
        addPassengerInfo(passengersFromPnr);

        requestObjectList.addAll(pnrList);
        for (Long pnrId : pnrIds) {
            logger.debug("in the pnr ");
            addAddressObjects(pnrId, addressObjects.get(pnrId));
            addFrequentFlyerObjects(pnrId, frequentFlyerObjects.get(pnrId));
            addBookingDetailObjects(pnrId, bookingDetailObjects.get(pnrId));
            addCreditCardObjects(pnrId, creditCardObjects.get(pnrId));
            addEmailObjects(pnrId, emailsObjects.get(pnrId));
            addPhoneObjects(pnrId, phoneObjects.get(pnrId));
            addTravelAgencyObjects(pnrId, travelAgency.get(pnrId));
            addDwellTimeObjects(pnrId, dwellMap.get(pnrId));
            addPassengerObjects(pnrId, paxMap.get(pnrId));
        }
        logger.debug("all done we go");

        if (this.requestType == null
                || this.requestType == RuleServiceRequestType.PNR_MESSAGE) {
            this.requestType = RuleServiceRequestType.PNR_MESSAGE;
        } else {
            this.requestType = RuleServiceRequestType.ANY_MESSAGE;
        }
    }

    private void addPassengerInfo(Set<Passenger> passengerSet) {
        Set<Long> paxIds = passengerSet.stream().map(Passenger::getId).collect(Collectors.toSet());
        addToRequestObjectSet(passengerSet);
        addPassengesInformationFacts(paxIds);
    }

    private void addPassengesInformationFacts(Set<Long> paxIds) {
        logger.debug("pax info");
        logger.debug("paxMap");
        Set<PassengerDetails> passengerDetails = passengerDetailRepository.getDetailsofPaxId(paxIds);

        logger.debug("trip detail map");
        Set<PassengerTripDetails> passengerTripDetails = passengerTripRepository.getTripDetailsByPaxId(paxIds);

        logger.debug("seatMap");
        Set<Seat> paxSeats = seatRepository.getByPaxId(paxIds);
        logger.debug("bags map");
        Set<Bag> bags = bagRepository.getAllByPaxId(paxIds);
        logger.debug("flightpax");
        Set<FlightPax> flightPaxSet = flightPaxRepository.findFlightFromPassIdList(paxIds);
        logger.debug("document");
        Set<Document> documentSet = documentRepository.getAllByPaxId(paxIds);
        addToRequestObjectSet(passengerTripDetails);
        addToRequestObjectSet(passengerDetails);
        addToRequestObjectSet(paxSeats);
        addToRequestObjectSet(bags);
        addToRequestObjectSet(flightPaxSet);
        addToRequestObjectSet(documentSet);
        logger.debug("pax info done");
    }


    private Map<Long, Set<Passenger>> createPaxMap(Set<Long> pnrIds) {
        Map<Long, Set<Passenger>> objectMap = new HashMap<>();
        List<Object[]> oList = pnrRepository.getPax(pnrIds);
        for (Object[] answerKey : oList) {
            Long pnrId = (Long) answerKey[0];
            Passenger object = (Passenger) answerKey[1];
            processObject(object, objectMap, pnrId);
        }
        return objectMap;
    }

    private Map<Long, Set<DwellTime>> createDwellTime(Set<Long> pnrIds) {
        Map<Long, Set<DwellTime>> objectMap = new HashMap<>();
        List<Object[]> oList = pnrRepository.getDwellTimeByPnr(pnrIds);
        for (Object[] answerKey : oList) {
            Long pnrId = (Long) answerKey[0];
            DwellTime object = (DwellTime) answerKey[1];
            processObject(object, objectMap, pnrId);
        }
        return objectMap;
    }

    private Map<Long, Set<Agency>> createTravelAgencyMap(Set<Long> pnrIds) {
        Map<Long, Set<Agency>> objectMap = new HashMap<>();
        List<Object[]> oList = pnrRepository.getTravelAgencyByPnr(pnrIds);
        for (Object[] answerKey : oList) {
            Long pnrId = (Long) answerKey[0];
            Agency object = (Agency) answerKey[1];
            processObject(object, objectMap, pnrId);
        }
        return objectMap;
    }

    private Map<Long, Set<FrequentFlyer>> createFrequentFlyersMap(Set<Long> pnrIds) {
        Map<Long, Set<FrequentFlyer>> objectMap = new HashMap<>();
        List<Object[]> oList = pnrRepository.getFrequentFlyerByPnrId(pnrIds);
        for (Object[] answerKey : oList) {
            Long pnrId = (Long) answerKey[0];
            FrequentFlyer object = (FrequentFlyer) answerKey[1];
            processObject(object, objectMap, pnrId);
        }
        return objectMap;
    }

    private Map<Long, Set<BookingDetail>> createBookingDetailMap(Set<Long> pnrIds) {
        Map<Long, Set<BookingDetail>> objectMap = new HashMap<>();
        List<Object[]> oList = pnrRepository.getBookingDetailsByPnrId(pnrIds);
        for (Object[] answerKey : oList) {
            Long pnrId = (Long) answerKey[0];
            BookingDetail object = (BookingDetail) answerKey[1];
            processObject(object, objectMap, pnrId);
        }
        return objectMap;
    }


    private Map<Long, Set<CreditCard>> createCreditCardMap(Set<Long> pnrIds) {
        Map<Long, Set<CreditCard>> objectMap = new HashMap<>();
        List<Object[]> oList = pnrRepository.getCreditCardByIds(pnrIds);
        for (Object[] answerKey : oList) {
            Long pnrId = (Long) answerKey[0];
            CreditCard object = (CreditCard) answerKey[1];
            processObject(object, objectMap, pnrId);
        }
        return objectMap;
    }

    private Map<Long, Set<Email>> createEmailMap(Set<Long> pnrIds) {
        Map<Long, Set<Email>> emailMap = new HashMap<>();
        List<Object[]> emailList = pnrRepository.getEmailByPnrIds(pnrIds);
        for (Object[] answerKey : emailList) {
            Long pnrId = (Long) answerKey[0];
            Email email = (Email) answerKey[1];
            processObject(email, emailMap, pnrId);
        }
        return emailMap;
    }

    private Map<Long, Set<Phone>> createPhoneMap(Set<Long> pnrIds) {
        Map<Long, Set<Phone>> phoneMap = new HashMap<>();
        List<Object[]> phoneList = pnrRepository.getPhonesByPnr(pnrIds);
        for (Object[] answerKey : phoneList) {
            Long pnrId = (Long) answerKey[0];
            Phone phone = (Phone) answerKey[1];
            processObject(phone, phoneMap, pnrId);
        }
        return phoneMap;
    }

    private Map<Long, Set<Address>> createAddressMap(Set<Long> pnrIds) {
        Map<Long, Set<Address>> addressMap = new HashMap<>();
        List<Object[]> addressList = pnrRepository.getAddressesByPnr(pnrIds);
        for (Object[] answerKey : addressList) {
            Long pnrId = (Long) answerKey[0];
            Address address = (Address) answerKey[1];
            processObject(address, addressMap, pnrId);
        }
        return addressMap;
    }


    private <T extends Collection> void addToRequestObjectSet(T set) {
        if (!set.isEmpty()) {
            requestObjectList.addAll(set);
        }
    }

    public static <T> void processObject(T type, Map<Long, Set<T>> map, Long pnrId) {
        if (map.containsKey(pnrId)) {
            map.get(pnrId).add(type);
        } else {
            Set<T> objectHashSet = new HashSet<>(map.values().size() * 50);
            objectHashSet.add(type);
            map.put(pnrId, objectHashSet);
        }
    }

    /**
     * Adds flight objects to the builders list.
     *
     * @param flights the flights to add
     */
    private void addFlights(Collection<Flight> flights) {
        if (flights != null) {
            for (Flight flight : flights) {
                Long id = flight.getId();
                if (!this.flightIdSet.contains(id)) {
                    this.requestObjectList.add(flight);
                    this.flightIdSet.add(id);
                }
            }
        }

    }

    private void addAddressObjects(final Long pnrId,
                                   final Collection<Address> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            return;
        }
        for (Address addr : addresses) {
            Long id = addr.getId();
            if (!this.addressIdSet.contains(id)) {
                requestObjectList.add(addr);
                this.addressIdSet.add(id);
            }
            PnrAddressLink pnrAddressLink = new PnrAddressLink(pnrId, addr.getId());
            if (!this.addressLinks.contains(pnrAddressLink)) {
                requestObjectList.add(new PnrAddressLink(pnrId, addr
                        .getId()));
            }
        }
    }

    private void addPhoneObjects(final Long pnrId, final Collection<Phone> phones) {
        if (phones == null || phones.isEmpty()) {
            logger.debug("No phones info.");
            return;
        }
        for (Phone phone : phones) {
            Long id = phone.getId();
            if (!this.phoneIdSet.contains(id)) {
                requestObjectList.add(phone);
                this.phoneIdSet.add(id);
            }

            PnrPhoneLink pnrPhoneLink = new PnrPhoneLink(pnrId, phone
                    .getId());
            if (!phoneLinks.contains(pnrPhoneLink)) {
                requestObjectList.add(pnrPhoneLink);
                phoneLinks.add(pnrPhoneLink);
            }
        }
    }

    private void addEmailObjects(final Long pnrId, final Collection<Email> emails) {
        if (emails == null || emails.isEmpty()) {
            return;
        }
        for (Email email : emails) {
            long id = email.getId();
            if (!this.emailIdSet.contains(id)) {
                requestObjectList.add(email);
                this.emailIdSet.add(id);
            }
            PnrEmailLink pnrEmailLink = new PnrEmailLink(pnrId, email.getId());
            if (!emailLinks.contains(pnrEmailLink)) {
                requestObjectList.add(pnrEmailLink);
                emailLinks.add(pnrEmailLink);
            }
        }
    }

    private void addFrequentFlyerObjects(final Long pnrId,
                                         final Collection<FrequentFlyer> frequentFlyers) {
        if (frequentFlyers == null || frequentFlyers.isEmpty()) {
            return;
        }
        for (FrequentFlyer ff : frequentFlyers) {
            Long id = ff.getId();
            if (!this.frequentFlyerIdSet.contains(id)) {
                this.requestObjectList.add(ff);
                this.frequentFlyerIdSet.add(id);
            }
            PnrFrequentFlyerLink pnrFrequentFlyerLink = new PnrFrequentFlyerLink(pnrId, ff
                    .getId());
            if (!this.frequentFlyerLinks.contains(pnrFrequentFlyerLink)) {
                this.requestObjectList.add(pnrFrequentFlyerLink);
                this.frequentFlyerLinks.add(pnrFrequentFlyerLink);
            }
        }
    }

    private void addCreditCardObjects(final Long pnrId,
                                      final Collection<CreditCard> creditCards) {
        if (creditCards == null || creditCards.isEmpty()) {
            return;
        }
        for (CreditCard cc : creditCards) {
            Long id = cc.getId();
            if (!this.creditCardIdSet.contains(id)) {
                this.requestObjectList.add(cc);
                this.creditCardIdSet.add(id);
            }
            PnrCreditCardLink pnrCreditCardLink = new PnrCreditCardLink(pnrId, cc
                    .getId());
            if (this.creditCardLinks.contains(pnrCreditCardLink)) {
                this.requestObjectList.add(pnrCreditCardLink);
                this.creditCardLinks.add(pnrCreditCardLink);
            }
        }
    }

    private void addBookingDetailObjects(final Long pnrId,
                                         final Collection<BookingDetail> bookingDetails) {
        if (bookingDetails == null || bookingDetails.isEmpty()) {
            return;
        }
        for (BookingDetail bl : bookingDetails) {
            Long id = bl.getId();
            if (!this.bookingDetailIdSet.contains(id)) {
                this.requestObjectList.add(bl);
                this.bookingDetailIdSet.add(id);
            }
            PnrBookingLink pnrBookingLink = new PnrBookingLink(pnrId, bl
                    .getId());
            if (!this.pnrBookingLinks.contains(pnrBookingLink)) {
                this.requestObjectList.add(pnrBookingLink);
                this.pnrBookingLinks.add(pnrBookingLink);
            }
        }
    }

    private void addDwellTimeObjects(final Long pnrId,
                                     final Collection<DwellTime> dwellTimes) {
        if (CollectionUtils.isEmpty(dwellTimes)) {
            return;
        }
        for (DwellTime a : dwellTimes) {
            Long id = a.getId();
            if (!this.dwellTimeIdSet.contains(id)) {
                this.requestObjectList.add(a);
                this.dwellTimeIdSet.add(id);
            }

            PnrDwellTimeLink pnrDwellTimeLink = new PnrDwellTimeLink(pnrId, a
                    .getId());
            if (!this.dwellTimeLinks.contains(pnrDwellTimeLink)) {
                this.requestObjectList.add(pnrDwellTimeLink);
                this.dwellTimeLinks.add(pnrDwellTimeLink);
            }
        }
    }

    private void addTravelAgencyObjects(final Long pnrId,
                                        final Collection<Agency> agencies) {
        if (CollectionUtils.isEmpty(agencies)) {
            return;
        }
        for (Agency a : agencies) {
            Long id = a.getId();
            if (!this.travelAgencyIdSet.contains(id)) {
                this.requestObjectList.add(a);
                this.travelAgencyIdSet.add(id);
            }
            PnrTravelAgencyLink pnrTravelAgencyLink = new PnrTravelAgencyLink(pnrId, a
                    .getId());
            if (!this.travelAgencyLinks.contains(pnrTravelAgencyLink)) {
                requestObjectList.add(pnrTravelAgencyLink);
                this.travelAgencyLinks.add(pnrTravelAgencyLink);
            }
        }
    }

    /**
     * Adds passenger and documents for PNR and APIS messages. In case of PNR a
     * link object is also created.
     * <p>
     * the PNR object. If not null then a link object is also
     * created.
     *
     * @param passengers the collection of passengers.
     */
    private void addPassengerObjects(final Long pnrId,
                                     final Collection<Passenger> passengers) {
        if (passengers == null || passengers.isEmpty()) {
            return;
        }
        for (Passenger passenger : passengers) {
            if (pnrId != null) {
                addPnrPassengerLink(pnrId, passenger);
            }
        }
    }


    private void addPnrPassengerLink(final Long pnrId, final Passenger passenger) {
        PnrPassengerLink link = new PnrPassengerLink(pnrId,
                passenger.getId());
        if (!this.passengerLinkSet.contains(link)) {
            requestObjectList.add(link);
            this.passengerLinkSet.add(link);
        }
    }

    private void addPaymentFormObjects(final Collection<PaymentForm> paymentFormList) {
        if (paymentFormList != null && !paymentFormList.isEmpty()) {
            this.requestObjectList.addAll(paymentFormList);
        }
    }
}
