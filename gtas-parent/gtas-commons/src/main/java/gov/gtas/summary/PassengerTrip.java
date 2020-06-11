package gov.gtas.summary;

import gov.gtas.model.PassengerTripDetails;
import org.springframework.beans.BeanUtils;

public class PassengerTrip {

    private Long passengerId;

    private Integer numberOfDaysVisaValid;

    private String embarkation;

    private String debarkation;

    private String embarkCountry;

    private String debarkCountry;

    private String reservationReferenceNumber;

    private String pnrReservationReferenceNumber;

    private Integer coTravelerCount;

    private Integer hoursBeforeTakeOff;

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

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public Integer getNumberOfDaysVisaValid() {
        return numberOfDaysVisaValid;
    }

    public void setNumberOfDaysVisaValid(Integer numberOfDaysVisaValid) {
        this.numberOfDaysVisaValid = numberOfDaysVisaValid;
    }

    public String getEmbarkation() {
        return embarkation;
    }

    public void setEmbarkation(String embarkation) {
        this.embarkation = embarkation;
    }

    public String getDebarkation() {
        return debarkation;
    }

    public void setDebarkation(String debarkation) {
        this.debarkation = debarkation;
    }

    public String getEmbarkCountry() {
        return embarkCountry;
    }

    public void setEmbarkCountry(String embarkCountry) {
        this.embarkCountry = embarkCountry;
    }

    public String getDebarkCountry() {
        return debarkCountry;
    }

    public void setDebarkCountry(String debarkCountry) {
        this.debarkCountry = debarkCountry;
    }

    public String getReservationReferenceNumber() {
        return reservationReferenceNumber;
    }

    public void setReservationReferenceNumber(String reservationReferenceNumber) {
        this.reservationReferenceNumber = reservationReferenceNumber;
    }

    public String getPnrReservationReferenceNumber() {
        return pnrReservationReferenceNumber;
    }

    public void setPnrReservationReferenceNumber(String pnrReservationReferenceNumber) {
        this.pnrReservationReferenceNumber = pnrReservationReferenceNumber;
    }

    public Integer getCoTravelerCount() {
        return coTravelerCount;
    }

    public void setCoTravelerCount(Integer coTravelerCount) {
        this.coTravelerCount = coTravelerCount;
    }

    public Integer getHoursBeforeTakeOff() {
        return hoursBeforeTakeOff;
    }

    public void setHoursBeforeTakeOff(Integer hoursBeforeTakeOff) {
        this.hoursBeforeTakeOff = hoursBeforeTakeOff;
    }
}
