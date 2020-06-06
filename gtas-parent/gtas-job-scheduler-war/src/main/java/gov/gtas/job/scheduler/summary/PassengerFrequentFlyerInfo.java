package gov.gtas.job.scheduler.summary;

import gov.gtas.model.FrequentFlyer;
import org.springframework.beans.BeanUtils;


public class PassengerFrequentFlyerInfo {

    private String carrier;

    private String number;

    private Long flightId;

    private Long passengerId;

    public static PassengerFrequentFlyerInfo from(Long passengerId, FrequentFlyer ff) {
        PassengerFrequentFlyerInfo pff = new PassengerFrequentFlyerInfo();
        BeanUtils.copyProperties(ff, pff);
        pff.setPassengerId(passengerId);
        return pff;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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
