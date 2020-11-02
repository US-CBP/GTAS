package gov.gtas.services.dto;

public class LookoutSendRequest {
    private Long passengerId;
    private Long flightId;
    private String note;
    private String noteRtf;
    private String noteCat;
    private Boolean sendRaw;
    private String countryGroupName;

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNoteRtf() {
        return noteRtf;
    }

    public void setNoteRtf(String noteRtf) {
        this.noteRtf = noteRtf;
    }

    public String getNoteCat() {
        return noteCat;
    }

    public void setNoteCat(String noteCat) {
        this.noteCat = noteCat;
    }

    public Boolean getSendRaw() {
        return sendRaw;
    }

    public void setSendRaw(Boolean sendRaw) {
        this.sendRaw = sendRaw;
    }

    public String getCountryGroupName() {
        return countryGroupName;
    }

    public void setCountryGroupName(String countryGroupName) {
        this.countryGroupName = countryGroupName;
    }
}
