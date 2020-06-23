package gov.gtas.summary;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageSummary {

    private SummaryMetaData summaryMetaData = new SummaryMetaData();
    private String rawMessage;
    private String hashCode;
    private String flightIdTag;
    private EventIdentifier eventIdentifier;
    private Boolean relatedToHit;
    private String messageType;
    private Date transmissionDate;
    private String transmissionSource;
    private String sourceMessageVersion;
    private String pnrRefNumber;
    private MessageAction action;
    private List<MessageTravelInformation> messageTravelInformation = new ArrayList<>();
    private List<MessagePhone> messagePhones = new ArrayList<>();
    private List<MessageFrequentFlyer> messageFrequentFlyers = new ArrayList<>();
    private List<MessageCreditCard> messageCreditCards = new ArrayList<>();
    private List<MessageAddress> messageAddresses = new ArrayList<>();
    private List<MessageEmail> messageEmails = new ArrayList<>();
    private List<PassengerSummary> passengerSummaries = new ArrayList<>();

    public MessageSummary() {}

    public MessageSummary(String hashCode, String flightIdTag){
        this.hashCode = hashCode;
        this.flightIdTag = flightIdTag;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Date getTransmissionDate() {
        return transmissionDate;
    }

    public void setTransmissionDate(Date transmissionDate) {
        this.transmissionDate = transmissionDate;
    }

    public String getTransmissionSource() {
        return transmissionSource;
    }

    public void setTransmissionSource(String transmissionSource) {
        this.transmissionSource = transmissionSource;
    }

    public String getSourceMessageVersion() {
        return sourceMessageVersion;
    }

    public void setSourceMessageVersion(String sourceMessageVersion) {
        this.sourceMessageVersion = sourceMessageVersion;
    }

    public Boolean getRelatedToHit() {
        return relatedToHit;
    }

    public void setRelatedToHit(Boolean relatedToHit) {
        this.relatedToHit = relatedToHit;
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

    public String getPnrRefNumber() {
        return pnrRefNumber;
    }

    public void setPnrRefNumber(String pnrRefNumber) {
        this.pnrRefNumber = pnrRefNumber;
    }
}