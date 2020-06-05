package gov.gtas.summary;

import gov.gtas.model.Flight;
import org.springframework.beans.BeanUtils;

import java.util.Date;

public class PassengerFlightInfo {

    private String carrier;

    private String flightNumber;

    private String fullFlightNumber;

    private String origin;

    private String originCountry;

    private String destination;

    private String destinationCountry;

    private Date etdDate;

    private String direction;

    private String idTag;

    private Long flightId;

    private Date localEtdDate;

    private Date localEtaDate;

    private Date etd;

    private Date eta;

    private Date etaDate;

    private Integer passengerCount;

    public static PassengerFlightInfo from(Flight flight) {
        PassengerFlightInfo pfi = new PassengerFlightInfo();
        BeanUtils.copyProperties(flight, pfi);
        BeanUtils.copyProperties(flight.getMutableFlightDetails(), pfi);
        BeanUtils.copyProperties(flight.getFlightPassengerCount(), pfi);
        return pfi;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getFullFlightNumber() {
        return fullFlightNumber;
    }

    public void setFullFlightNumber(String fullFlightNumber) {
        this.fullFlightNumber = fullFlightNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDestinationCountry() {
        return destinationCountry;
    }

    public void setDestinationCountry(String destinationCountry) {
        this.destinationCountry = destinationCountry;
    }

    public Date getEtdDate() {
        return etdDate;
    }

    public void setEtdDate(Date etdDate) {
        this.etdDate = etdDate;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getIdTag() {
        return idTag;
    }

    public void setIdTag(String idTag) {
        this.idTag = idTag;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Date getLocalEtdDate() {
        return localEtdDate;
    }

    public void setLocalEtdDate(Date localEtdDate) {
        this.localEtdDate = localEtdDate;
    }

    public Date getLocalEtaDate() {
        return localEtaDate;
    }

    public void setLocalEtaDate(Date localEtaDate) {
        this.localEtaDate = localEtaDate;
    }

    public Date getEtd() {
        return etd;
    }

    public void setEtd(Date etd) {
        this.etd = etd;
    }

    public Date getEta() {
        return eta;
    }

    public void setEta(Date eta) {
        this.eta = eta;
    }

    public Date getEtaDate() {
        return etaDate;
    }

    public void setEtaDate(Date etaDate) {
        this.etaDate = etaDate;
    }

    public Integer getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(Integer passengerCount) {
        this.passengerCount = passengerCount;
    }
}
