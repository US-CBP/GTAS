package gov.gtas.job.scheduler.summary;

import gov.gtas.model.Email;
import org.springframework.beans.BeanUtils;

public class PassengerEmail {

    private String address;

    private String domain;

    private Long flightId;

    private Long passengerId;

    public static PassengerEmail from(Long passengerId, Email email) {
        PassengerEmail pe = new PassengerEmail();
        BeanUtils.copyProperties(email, pe);
        pe.setPassengerId(passengerId);
        return pe;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }
}
