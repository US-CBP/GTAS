package gov.gtas.summary;

import java.util.ArrayList;
import java.util.List;

public class PassengerSummary {

    String version = "1.0";
    PassengerBiographic passengerBiographic = new PassengerBiographic();
    PassengerTrip passengerTrip = new PassengerTrip();
    List<PassengerDocument> passengerDocumentsList = new ArrayList<>();
    List<PassengerHit> passengerHits = new ArrayList<>();
    PassengerIds passengerIds = new PassengerIds();
    private Long gtasId;

    public PassengerIds getPassengerIds() {
        return passengerIds;
    }

    public void setPassengerIds(PassengerIds passengerIds) {
        this.passengerIds = passengerIds;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public PassengerBiographic getPassengerBiographic() {
        return passengerBiographic;
    }

    public void setPassengerBiographic(PassengerBiographic passengerBiographic) {
        this.passengerBiographic = passengerBiographic;
    }

    public PassengerTrip getPassengerTrip() {
        return passengerTrip;
    }

    public void setPassengerTrip(PassengerTrip passengerTrip) {
        this.passengerTrip = passengerTrip;
    }

    public List<PassengerDocument> getPassengerDocumentsList() {
        return passengerDocumentsList;
    }

    public void setPassengerDocumentsList(List<PassengerDocument> passengerDocumentsList) {
        this.passengerDocumentsList = passengerDocumentsList;
    }

    public List<PassengerHit> getPassengerHits() {
        return passengerHits;
    }

    public void setPassengerHits(List<PassengerHit> passengerDerogs) {
        this.passengerHits = passengerDerogs;
    }

    public Long getGtasId() {
        return gtasId;
    }

    public void setGtasId(Long gtasId) {
        this.gtasId = gtasId;
    }
}
