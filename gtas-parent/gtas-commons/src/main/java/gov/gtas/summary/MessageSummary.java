package gov.gtas.summary;


import gov.gtas.model.*;

import java.util.ArrayList;
import java.util.List;

public class MessageSummary {

    private SummaryMetaData summaryMetaData = new SummaryMetaData();
    private String rawMessage;
    private String hashCode;
    private String flightIdTag;
    private EventIdentifier eventIdentifier;
    private Boolean relatedToDerog;
    private MessageAction action;
    private List<MessageTravelInformation> messageTravelInformation = new ArrayList<>();
    private List<MessagePhone> messagePhones = new ArrayList<>();
    private List<MessageFrequentFlyer> messageFrequentFlyers = new ArrayList<>();
    private List<MessageCreditCard> messageCreditCards = new ArrayList<>();
    private List<MessageAddress> messageAddresses = new ArrayList<>();
    private List<MessageEmail> messageEmails = new ArrayList<>();
    private List<PassengerSummary> passengerSummaries = new ArrayList<>();

    public MessageSummary() {};

    public MessageSummary(String hashCode, String flightIdTag){
        this.hashCode = hashCode;
        this.flightIdTag = flightIdTag;
    }

    public void addAddress(Address address) {
        MessageAddress ma = MessageAddress.from(this.hashCode, this.flightIdTag, address);
        this.getMessageAddresses().add(ma);
    }
    public void addPhone(Phone phone) {
        MessagePhone mp = MessagePhone.from(this.hashCode, this.flightIdTag, phone);
        this.getMessagePhones().add(mp);
    }
    public void addFrequentFlyer(FrequentFlyer frequentFlyer) {
        MessageFrequentFlyer mff = MessageFrequentFlyer.from(this.hashCode, this.flightIdTag, frequentFlyer);
        this.getMessageFrequentFlyers().add(mff);
    }
    public void addCreditCard(CreditCard creditCard) {
        MessageCreditCard mcc = MessageCreditCard.from(this.hashCode, this.flightIdTag, creditCard);
        this.getMessageCreditCards().add(mcc);
    }
    public void addEmail(Email email) {
        MessageEmail me = MessageEmail.from(this.hashCode, this.flightIdTag, email);
        this.getMessageEmails().add(me);
    }
    public void addPassengerNoHits(Passenger passenger) {
        PassengerSummary ps = new PassengerSummary();
        ps.setGtasId(passenger.getId());
        PassengerBiographic pb = PassengerBiographic.from(passenger.getPassengerDetails());
        PassengerTrip pt = PassengerTrip.from(passenger.getPassengerTripDetails());
        PassengerIds pids = PassengerIds.from(passenger.getPassengerIDTag());
        for (Document d : passenger.getDocuments()) {
            PassengerDocument pd = PassengerDocument.from(d, flightIdTag);
            ps.getPassengerDocumentsList().add(pd);
        }
        ps.setPassengerBiographic(pb);
        ps.setPassengerTrip(pt);
        ps.setPassengerIds(pids);
        this.getPassengerSummaries().add(ps);
    }
    public Boolean getRelatedToDerog() {
        return relatedToDerog;
    }

    public void setRelatedToDerog(Boolean relatedToDerog) {
        this.relatedToDerog = relatedToDerog;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    public EventIdentifier getEventIdentifier() {
        return eventIdentifier;
    }

    public void setEventIdentifier(EventIdentifier eventIdentifier) {
        this.eventIdentifier = eventIdentifier;
    }

    public List<MessagePhone> getMessagePhones() {
        return messagePhones;
    }

    public void setMessagePhones(List<MessagePhone> messagePhones) {
        this.messagePhones = messagePhones;
    }

    public List<MessageFrequentFlyer> getMessageFrequentFlyers() {
        return messageFrequentFlyers;
    }

    public void setMessageFrequentFlyers(List<MessageFrequentFlyer> messageFrequentFlyers) {
        this.messageFrequentFlyers = messageFrequentFlyers;
    }

    public List<MessageCreditCard> getMessageCreditCards() {
        return messageCreditCards;
    }

    public void setMessageCreditCards(List<MessageCreditCard> messageCreditCards) {
        this.messageCreditCards = messageCreditCards;
    }

    public List<MessageAddress> getMessageAddresses() {
        return messageAddresses;
    }

    public void setMessageAddresses(List<MessageAddress> messageAddresses) {
        this.messageAddresses = messageAddresses;
    }

    public List<MessageEmail> getMessageEmails() {
        return messageEmails;
    }

    public void setMessageEmails(List<MessageEmail> messageEmails) {
        this.messageEmails = messageEmails;
    }

    public List<PassengerSummary> getPassengerSummaries() {
        return passengerSummaries;
    }

    public void setPassengerSummaries(List<PassengerSummary> passengerSummaries) {
        this.passengerSummaries = passengerSummaries;
    }

    public SummaryMetaData getSummaryMetaData() {
        return summaryMetaData;
    }

    public void setSummaryMetaData(SummaryMetaData summaryMetaData) {
        this.summaryMetaData = summaryMetaData;
    }

    public List<MessageTravelInformation> getMessageTravelInformation() {
        return messageTravelInformation;
    }

    public void setMessageTravelInformation(List<MessageTravelInformation> messageTravelInformation) {
        this.messageTravelInformation = messageTravelInformation;
    }

    public MessageAction getAction() {
        return action;
    }

    public void setAction(MessageAction action) {
        this.action = action;
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public String getFlightIdTag() {
        return flightIdTag;
    }

    public void setFlightIdTag(String flightIdTag) {
        this.flightIdTag = flightIdTag;
    }
}