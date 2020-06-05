package gov.gtas.summary;

import java.util.ArrayList;
import java.util.List;

public class PassengerSummary {

    String version = "1.0";
    SummaryMetaData summaryMetaData = new SummaryMetaData();
    PassengerBiographic passengerBiographic = new PassengerBiographic();
    PassengerTrip passengerTrip = new PassengerTrip();
    PassengerFlightInfo passengerFlightInfo = new PassengerFlightInfo();
    List<PassengerDocument> passengerDocumentsList = new ArrayList<>();
    List<PassengerDerog> passengerDerogs = new ArrayList<>();
    List<PassengerPhone> phones = new ArrayList<>();
    List<PassengerFrequentFlyerInfo> pffs = new ArrayList<>();
    List<PassengerCreditCard> pccs = new ArrayList<>();
    List<PassengerAddress> padrs = new ArrayList<>();
    List<PassengerEmail> pemails = new ArrayList<>();
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

    public SummaryMetaData getSummaryMetaData() {
        return summaryMetaData;
    }

    public void setSummaryMetaData(SummaryMetaData summaryMetaData) {
        this.summaryMetaData = summaryMetaData;
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

    public PassengerFlightInfo getPassengerFlightInfo() {
        return passengerFlightInfo;
    }

    public void setPassengerFlightInfo(PassengerFlightInfo passengerFlightInfo) {
        this.passengerFlightInfo = passengerFlightInfo;
    }

    public List<PassengerDocument> getPassengerDocumentsList() {
        return passengerDocumentsList;
    }

    public void setPassengerDocumentsList(List<PassengerDocument> passengerDocumentsList) {
        this.passengerDocumentsList = passengerDocumentsList;
    }

    public List<PassengerDerog> getPassengerDerogs() {
        return passengerDerogs;
    }

    public void setPassengerDerogs(List<PassengerDerog> passengerDerogs) {
        this.passengerDerogs = passengerDerogs;
    }

    public List<PassengerPhone> getPhones() {
        return phones;
    }

    public void setPhones(List<PassengerPhone> phones) {
        this.phones = phones;
    }

    public List<PassengerFrequentFlyerInfo> getPffs() {
        return pffs;
    }

    public void setPffs(List<PassengerFrequentFlyerInfo> pffs) {
        this.pffs = pffs;
    }

    public List<PassengerCreditCard> getPccs() {
        return pccs;
    }

    public void setPccs(List<PassengerCreditCard> pccs) {
        this.pccs = pccs;
    }

    public List<PassengerAddress> getPadrs() {
        return padrs;
    }

    public void setPadrs(List<PassengerAddress> padrs) {
        this.padrs = padrs;
    }

    public List<PassengerEmail> getPemails() {
        return pemails;
    }

    public void setPemails(List<PassengerEmail> pemails) {
        this.pemails = pemails;
    }
}
