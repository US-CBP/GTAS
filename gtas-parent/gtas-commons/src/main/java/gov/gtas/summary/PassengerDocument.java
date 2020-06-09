package gov.gtas.summary;

import gov.gtas.enumtype.MessageType;
import gov.gtas.model.Document;
import gov.gtas.model.Flight;
import org.springframework.beans.BeanUtils;

import java.util.Date;


public class PassengerDocument {
    private String documentType;

    private String documentNumber;

    private Date expirationDate;

    private Date issuanceDate;

    private String issuanceCountry;

    private MessageType messageType;

    private Long flightId;

    private Flight flight;

    private Long passengerId;

    private Integer numberOfDaysValid;

    private String flightTagId;


    public static PassengerDocument from(Document d, String flightTagId) {
        PassengerDocument pd =  new PassengerDocument();
        BeanUtils.copyProperties(d, pd);
        pd.setFlightTagId(flightTagId);

        return pd;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Date getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(Date issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public String getIssuanceCountry() {
        return issuanceCountry;
    }

    public void setIssuanceCountry(String issuanceCountry) {
        this.issuanceCountry = issuanceCountry;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public Integer getNumberOfDaysValid() {
        return numberOfDaysValid;
    }

    public void setNumberOfDaysValid(Integer numberOfDaysValid) {
        this.numberOfDaysValid = numberOfDaysValid;
    }

    public String getFlightTagId() {
        return flightTagId;
    }

    public void setFlightTagId(String flightTagId) {
        this.flightTagId = flightTagId;
    }
}
