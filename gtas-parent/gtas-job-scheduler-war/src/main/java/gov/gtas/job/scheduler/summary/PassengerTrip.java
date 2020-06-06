package gov.gtas.job.scheduler.summary;

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
