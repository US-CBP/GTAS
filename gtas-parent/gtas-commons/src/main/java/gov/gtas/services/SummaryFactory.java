package gov.gtas.services;

import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.enumtype.MessageType;
import gov.gtas.model.*;
import gov.gtas.model.lookup.FlightDirectionCode;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.summary.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SummaryFactory {
    private final static Logger logger = LoggerFactory.getLogger(SummaryFactory.class);

    public static EventIdentifier from(String eventType, Flight flight) {
        EventIdentifier eventIdentifier = new EventIdentifier();
        eventIdentifier.setEventType(eventType);
        eventIdentifier.setCountryOrigin(flight.getOriginCountry());
        eventIdentifier.setCountryDestination(flight.getDestinationCountry());
        List<String> identList = new ArrayList<>(6);
        identList.add(flight.getOrigin());
        identList.add(flight.getDestination());
        identList.add(flight.getCarrier());
        identList.add(flight.getFlightNumber());
        String strpedTime = getStripedTimeString(flight);
        identList.add(strpedTime);
        identList.add(Long.toString(flight.getEtdDate().getTime()));
        eventIdentifier.setIdentifierArrayList(identList);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(identList.get(i));
        }
        eventIdentifier.setIdentifier(sb.toString());
        return eventIdentifier;
    }

    public static EventIdentifier from(Flight flight, Message message) {
        EventIdentifier eventIdentifier = new EventIdentifier();
        String eventType = "NO_TYPE";
        if (message instanceof ApisMessage) {
            eventType = "RULE_APIS";
        } else if (message instanceof Pnr) {
            eventType = "RULE_PNR";
        }
        eventIdentifier.setEventType(eventType);
        eventIdentifier.setCountryOrigin(flight.getOriginCountry());
        eventIdentifier.setCountryDestination(flight.getDestinationCountry());
        List<String> identList = new ArrayList<>(6);
        identList.add(flight.getOrigin());
        identList.add(flight.getDestination());
        identList.add(flight.getCarrier());
        identList.add(flight.getFlightNumber());
        String strpedTime = getStripedTimeString(flight);
        identList.add(strpedTime);
        identList.add(Long.toString(flight.getEtdDate().getTime()));
        eventIdentifier.setIdentifierArrayList(identList);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(identList.get(i));
        }
        eventIdentifier.setIdentifier(sb.toString());
        return eventIdentifier;
    }

    private static String getStripedTimeString(Flight flight) {
        Date strpedTimeDate = stripTime(flight.getEtdDate());
        return strpedTimeDate == null ? "" : Long.toString(strpedTimeDate.getTime());
    }

    public static Date stripTime(Date d) {
        if (d == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Document from(PassengerDocument passengerDocument, MessageType messageType, Flight bcEvent, Passenger passenger) {
        Document document = new Document();
        document.setDocumentNumber(passengerDocument.getDocumentNumber());
        document.setDocumentType(passengerDocument.getDocumentType());
        document.setMessageType(messageType);
        document.setExpirationDate(passengerDocument.getExpirationDate());
        document.setFlight(bcEvent);
        document.setFlightId(bcEvent.getId());
        document.setNumberOfDaysValid(passengerDocument.getNumberOfDaysValid());
        document.setPassenger(passenger);
        document.setPassengerId(passenger.getId());
        return document;
    }

    public static MessageAddress from(String flightIdTag, String messageHash, Address address) {
        MessageAddress pa = new MessageAddress();
        pa.setCity(address.getCity());
        pa.setCountry(address.getCountry());
        pa.setLine1(address.getLine1());
        pa.setLine2(address.getLine2());
        pa.setLine3(address.getLine3());
        pa.setMessageHash(messageHash);
        pa.setState(address.getState());
        pa.setFlightIdTag(flightIdTag);
        pa.setMessageHash(messageHash);
        return pa;
    }

    public static MessageCreditCard from(String messageIdTag, String flightIdTag, CreditCard cc) {
        MessageCreditCard pcc = new MessageCreditCard();
        pcc.setAccountHolder(cc.getAccountHolder());
        pcc.setAccountHolderAddress(cc.getAccountHolderAddress());
        pcc.setAccountHolderPhone(cc.getAccountHolderPhone());
        pcc.setCardType(cc.getCardType());
        pcc.setExpiration(cc.getExpiration());
        pcc.setNumber(cc.getNumber());
        pcc.setFlightIdTag(flightIdTag);
        pcc.setMessageIdTag(messageIdTag);
        return pcc;
    }

    public static MessageEmail from(String flightIdTag, String messageIdTag, Email email) {
        MessageEmail pe = new MessageEmail();
        pe.setAddress(email.getAddress());
        pe.setDomain(email.getDomain());
        pe.setFlightIdTag(flightIdTag);
        pe.setMessageIdTag(messageIdTag);
        return pe;
    }

    public static MessageFrequentFlyer from(String messageHash, String flightHash, FrequentFlyer ff) {
        MessageFrequentFlyer mff = new MessageFrequentFlyer();
        mff.setCarrier(ff.getCarrier());
        mff.setNumber(ff.getNumber());
        mff.setFlightIdTag(flightHash);
        mff.setMessageHash(messageHash);
        return mff;
    }

    public static MessagePhone from(String messageIdTag, String flightIdTag, Phone phone) {
        MessagePhone passengerPhone = new MessagePhone();
        passengerPhone.setNumber(phone.getNumber());
        passengerPhone.setMessageIdTag(messageIdTag);
        passengerPhone.setFlightIdTag(flightIdTag);
        return passengerPhone;
    }

    public static MessageTravelInformation from(Flight flight, String flightIdTag) {
        MessageTravelInformation pfi = new MessageTravelInformation();
        pfi.setBorderCrossingEvent(true);
        pfi.setCarrier(flight.getCarrier());
        pfi.setDestination(flight.getDestination());
        pfi.setDestinationCountry(flight.getDestinationCountry());
        pfi.setOrigin(flight.getOrigin());
        pfi.setOriginCountry(flight.getOriginCountry());
        pfi.setIdTag(flight.getIdTag());
        pfi.setDirection(flight.getDirection());
        pfi.setEta(flight.getMutableFlightDetails().getEta());
        pfi.setEtaDate(flight.getMutableFlightDetails().getEtaDate());
        pfi.setEtd(flight.getMutableFlightDetails().getEtd());
        pfi.setEtdDate(flight.getEtdDate());
        pfi.setLocalEtaDate(flight.getMutableFlightDetails().getLocalEtaDate());
        pfi.setLocalEtdDate(flight.getMutableFlightDetails().getLocalEtdDate());
        pfi.setFlightIdTag(flight.getIdTag());
        pfi.setPassengerCount(flight.getFlightPassengerCount().getPassengerCount());
        pfi.setFlightId(flight.getId());
        pfi.setFlightIdTag(flight.getIdTag());
        pfi.setEtdDate(flight.getEtdDate());
        pfi.setFullFlightNumber(flight.getFullFlightNumber());
        pfi.setFlightNumber(flight.getFlightNumber());
        pfi.setBorderCrossingEvent(true);
        return pfi;
    }

    public static MessageTravelInformation from(BookingDetail bookingDetail, String flightIdTag) {
        MessageTravelInformation pfi = new MessageTravelInformation();
        pfi.setBorderCrossingEvent(true);
        pfi.setFlightNumber(bookingDetail.getFlightNumber());
        pfi.setFullFlightNumber(bookingDetail.getFullFlightNumber());
        pfi.setDestination(bookingDetail.getDestination());
        pfi.setDestinationCountry(bookingDetail.getDestinationCountry());
        pfi.setEta(bookingDetail.getEta());
        pfi.setEtaDate(bookingDetail.getEtaDate());
        pfi.setEtd(bookingDetail.getEtd());
        pfi.setEtaDate(bookingDetail.getEtaDate());
        pfi.setLocalEtaDate(bookingDetail.getLocalEtaDate());
        pfi.setLocalEtdDate(bookingDetail.getLocalEtdDate());
        pfi.setEtdDate(bookingDetail.getEtdDate());
        pfi.setLocalEtdDate(bookingDetail.getLocalEtdDate());
        pfi.setFlightId(bookingDetail.getId());
        pfi.setOrigin(bookingDetail.getOrigin());
        pfi.setOriginCountry(bookingDetail.getOriginCountry());
        pfi.setBorderCrossingEvent(true);
        pfi.setBorderCrossingEvent(false);
        return pfi;
    }

    public static PassengerBiographic from(PassengerDetails pd) {
        PassengerBiographic pb = new PassengerBiographic();
        pb.setDob(pd.getDob());
        pb.setFirstName(pd.getFirstName());
        pb.setAge(pd.getAge());
        pb.setLastName(pd.getLastName());
        pb.setGender(pd.getGender());
        pb.setMiddleName(pd.getMiddleName());
        pb.setNationality(pd.getNationality());
        pb.setPassengerType(pd.getPassengerType());
        pb.setResidencyCountry(pd.getResidencyCountry());
        pb.setTitle(pd.getTitle());
        return pb;
    }

    public static PassengerDocument from(Document d, String flightTagId) {
        PassengerDocument pd = new PassengerDocument();
        pd.setDocumentNumber(d.getDocumentNumber());
        pd.setDocumentType(d.getDocumentType());
        pd.setExpirationDate(d.getExpirationDate());
        pd.setFlightId(d.getFlightId());
        pd.setIssuanceCountry(d.getIssuanceCountry());
        pd.setMessageType(d.getMessageType().toString());
        pd.setFlightTagId(flightTagId);
        return pd;
    }

    public static Flight from(MessageTravelInformation messageTravelInformation, String homeCountry) {
        Flight flight = new Flight();
        flight.setIdTag(messageTravelInformation.getIdTag());
        flight.setFlightNumber(messageTravelInformation.getFlightNumber());
        flight.setFullFlightNumber(messageTravelInformation.getFullFlightNumber());
        flight.setOrigin(messageTravelInformation.getOrigin());
        flight.setOriginCountry(messageTravelInformation.getOriginCountry());
        flight.setDestination(messageTravelInformation.getDestination());
        flight.setDestinationCountry(messageTravelInformation.getDestinationCountry());
        flight.setCarrier(messageTravelInformation.getCarrier());
        flight.setEtdDate(messageTravelInformation.getEtdDate());
        String originCountry = flight.getOriginCountry();
        String destCountry = flight.getDestinationCountry();

        if (homeCountry.equals(originCountry) && homeCountry.equals(destCountry)) {
            flight.setDirection(FlightDirectionCode.C.name());
        } else if (homeCountry.equals(originCountry)) {
            flight.setDirection(FlightDirectionCode.O.name());
        } else if (homeCountry.equals(destCountry)) {
            flight.setDirection(FlightDirectionCode.I.name());
        } else {
            flight.setDirection(FlightDirectionCode.A.name());
        }
        return flight;
    }

    public static BookingDetail from(MessageTravelInformation mti, Flight bcEvent) {
        BookingDetail bookingDetail = new BookingDetail();
        bookingDetail.setOrigin(mti.getOrigin());
        bookingDetail.setEtdDate(mti.getEtdDate());
        bookingDetail.setEtaDate(mti.getEtaDate());
        bookingDetail.setLocalEtaDate(mti.getLocalEtaDate());
        bookingDetail.setLocalEtdDate(mti.getLocalEtdDate());
        bookingDetail.setOriginCountry(mti.getOriginCountry());
        bookingDetail.setDestination(mti.getDestination());
        bookingDetail.setDestinationCountry(mti.getDestinationCountry());
        bookingDetail.setEta(mti.getEta());
        bookingDetail.setEtaDate(mti.getEtaDate());
        bookingDetail.setLocalEtaDate(mti.getLocalEtaDate());
        bookingDetail.setEtd(mti.getEtd());
        bookingDetail.setLocalEtdDate(mti.getLocalEtdDate());
        bookingDetail.setEtdDate(mti.getEtdDate());
        bookingDetail.setFlightId(bcEvent.getId());
        bookingDetail.setFlight(bcEvent);
        bookingDetail.setFullFlightNumber(mti.getFullFlightNumber());
        bookingDetail.setFlightNumber(mti.getFlightNumber());
        bookingDetail.setCreatedAt(new Date());
        return bookingDetail;

    }


    public static PassengerHit from(HitDetail hd) {
        PassengerHit pd = new PassengerHit();

        HitMaker hm = hd.getHitMaker();
        pd.setHitTypeEnum(hm.getHitTypeEnum().toString());

        HitCategory hc = hm.getHitCategory();
        pd.setHitCategory(hc.getName());

        pd.setCreatedDate(hd.getCreatedDate());
        pd.setDescription(hd.getDescription());
        pd.setFlightId(hd.getFlightId());
        pd.setHitType(hd.getHitType());
        pd.setHitMakerId(hd.getHitMakerId());
        pd.setPassengerId(hd.getPassengerId());
        pd.setTitle(hd.getTitle());
        pd.setRuleConditions(hd.getRuleConditions());
        pd.setRuleId(hd.getRuleId());
        return pd;
    }

    public static PassengerIds from(PassengerIDTag passengerIDTag) {
        PassengerIds pids = new PassengerIds();
        if (passengerIDTag == null) {
            return pids;
        }
        pids.setIdTag(passengerIDTag.getIdTag());
        pids.setPax_id(passengerIDTag.getPax_id());
        pids.setTamrId(passengerIDTag.getTamrId());
        return pids;
    }

    public static PassengerTrip from(PassengerTripDetails ptd) {
        PassengerTrip pt = new PassengerTrip();
        pt.setCoTravelerCount(ptd.getCoTravelerCount());
        pt.setDebarkation(ptd.getDebarkation());
        pt.setDebarkCountry(ptd.getDebarkCountry());
        pt.setEmbarkation(ptd.getEmbarkation());
        pt.setEmbarkCountry(ptd.getEmbarkCountry());
        pt.setHoursBeforeTakeOff(ptd.getHoursBeforeTakeOff());
        pt.setPassengerId(ptd.getPassengerId());
        pt.setNumberOfDaysVisaValid(ptd.getNumberOfDaysVisaValid());
        pt.setPnrReservationReferenceNumber(ptd.getReservationReferenceNumber());
        pt.setReservationReferenceNumber(ptd.getReservationReferenceNumber());
        return pt;
    }


    public static void addBookingDetail(BookingDetail bookingDetail, MessageSummary ms) {
        MessageTravelInformation mti = SummaryFactory.from(bookingDetail, ms.getFlightIdTag());
        ms.getMessageTravelInformation().add(mti);
    }

    public static void addAddress(Address address, MessageSummary ms) {
        MessageAddress ma = SummaryFactory.from(ms.getHashCode(), ms.getFlightIdTag(), address);
        ms.getMessageAddresses().add(ma);
    }

    public static void addPhone(Phone phone, MessageSummary ms) {
        MessagePhone mp = SummaryFactory.from(ms.getHashCode(), ms.getFlightIdTag(), phone);
        ms.getMessagePhones().add(mp);
    }

    public static void addFrequentFlyer(FrequentFlyer frequentFlyer, MessageSummary ms) {
        MessageFrequentFlyer mff = SummaryFactory.from(ms.getHashCode(), ms.getFlightIdTag(), frequentFlyer);
        ms.getMessageFrequentFlyers().add(mff);
    }

    public static void addCreditCard(CreditCard creditCard, MessageSummary ms) {
        MessageCreditCard mcc = SummaryFactory.from(ms.getHashCode(), ms.getFlightIdTag(), creditCard);
        ms.getMessageCreditCards().add(mcc);
    }

    public static void addEmail(Email email, MessageSummary ms) {
        MessageEmail me = SummaryFactory.from(ms.getHashCode(), ms.getFlightIdTag(), email);
        ms.getMessageEmails().add(me);
    }

    public static void addPassengerNoHits(Passenger passenger, MessageSummary ms) {
        PassengerSummary ps = new PassengerSummary();
        ps.setGtasId(passenger.getId());
        PassengerBiographic pb = SummaryFactory.from(passenger.getPassengerDetails());
        PassengerTrip pt = SummaryFactory.from(passenger.getPassengerTripDetails());
        PassengerIds pids = SummaryFactory.from(passenger.getPassengerIDTag());
        for (Document d : passenger.getDocuments()) {
            PassengerDocument pd = SummaryFactory.from(d, ms.getFlightIdTag());
            ps.getPassengerDocumentsList().add(pd);
        }
        ps.setPassengerBiographic(pb);
        ps.setPassengerTrip(pt);
        ps.setPassengerIds(pids);
        ms.getPassengerSummaries().add(ps);
    }

    public static PassengerPendingDetail from(PendingHitDetails pendingHitDetails) {
        PassengerPendingDetail passengerPendingDetail = new PassengerPendingDetail();
        passengerPendingDetail.setCreatedDate(pendingHitDetails.getCreatedDate());
        passengerPendingDetail.setDescription(pendingHitDetails.getDescription());
        passengerPendingDetail.setFlightId(pendingHitDetails.getFlightId());
        passengerPendingDetail.setHitEnum(pendingHitDetails.getHitEnum().toString());
        passengerPendingDetail.setHitMakerId(pendingHitDetails.getHitMakerId());
        passengerPendingDetail.setHitType(pendingHitDetails.getHitType());
        passengerPendingDetail.setPercentage(pendingHitDetails.getPercentage());
        passengerPendingDetail.setRuleConditions(passengerPendingDetail.getRuleConditions());
        passengerPendingDetail.setPassengerId(pendingHitDetails.getPassengerId());
        return passengerPendingDetail;
    }

    public static PendingHitDetails from(PassengerPendingDetail passengerPendingDetail) {
        PendingHitDetails phd = new PendingHitDetails();
        phd.setCreatedDate(passengerPendingDetail.getCreatedDate());
        phd.setDescription(passengerPendingDetail.getDescription());
        phd.setFlightId(passengerPendingDetail.getFlightId());
        Optional<HitTypeEnum> hteo = HitTypeEnum.fromString(passengerPendingDetail.getHitEnum());

        if (hteo.isPresent()) {
            phd.setHitEnum(hteo.get());
        } else {
            logger.error("Passenger Pending Detail does NOT have relevant Hit Type! Defaulting to UDR");
            phd.setHitEnum(HitTypeEnum.USER_DEFINED_RULE);
        }

        phd.setHitMakerId(passengerPendingDetail.getHitMakerId());
        phd.setHitType(passengerPendingDetail.getHitType());
        phd.setPercentage(passengerPendingDetail.getPercentage());
        phd.setRuleConditions(passengerPendingDetail.getRuleConditions());
        phd.setPassengerId(passengerPendingDetail.getPassengerId());
        return phd;
    }


}
