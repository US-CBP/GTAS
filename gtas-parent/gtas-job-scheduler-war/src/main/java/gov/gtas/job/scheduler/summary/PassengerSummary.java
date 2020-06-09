package gov.gtas.job.scheduler.summary;

import java.util.ArrayList;
import java.util.List;

public class PassengerSummary {

    String version = "1.0";
    PassengerBiographic passengerBiographic = new PassengerBiographic();
    PassengerTrip passengerTrip = new PassengerTrip();
    List<PassengerDocument> passengerDocumentsList = new ArrayList<>();
    List<PassengerHit> passengerDerogs = new ArrayList<>();
    PassengerIds passengerIds = new PassengerIds();

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

    public List<PassengerHit> getPassengerDerogs() {
        return passengerDerogs;
    }

    public void setPassengerDerogs(List<PassengerHit> passengerDerogs) {
        this.passengerDerogs = passengerDerogs;
    }
}
