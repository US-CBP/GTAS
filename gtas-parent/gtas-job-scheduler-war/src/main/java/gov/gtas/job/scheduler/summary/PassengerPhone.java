package gov.gtas.job.scheduler.summary;


import gov.gtas.model.Phone;
import org.springframework.beans.BeanUtils;

public class PassengerPhone {

    private String number;

    private Long flightId;

    private Long passengerId;


    public static PassengerPhone from(Long passengerId, Phone phone){
        PassengerPhone passengerPhone = new PassengerPhone();
        BeanUtils.copyProperties(phone, passengerPhone);
        passengerPhone.setPassengerId(passengerId);
        return passengerPhone;
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
