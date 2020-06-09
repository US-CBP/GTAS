package gov.gtas.job.scheduler.summary;

import gov.gtas.model.FrequentFlyer;
import org.springframework.beans.BeanUtils;

public class MessageFrequentFlyer {
    private String carrier;

    private String number;

    private String flightIdTag;

    private String messageHash;

    public static MessageFrequentFlyer from(String messageHash, String flightHash, FrequentFlyer ff) {
        MessageFrequentFlyer mff = new MessageFrequentFlyer();
        BeanUtils.copyProperties(ff, mff);
        mff.setFlightIdTag(flightHash);
        mff.setMessageHash(messageHash);
        return mff;
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


    public String getMessageHash() {
        return messageHash;
    }

    public void setMessageHash(String messageHash) {
        this.messageHash = messageHash;
    }

    public String getFlightIdTag() {
        return flightIdTag;
    }

    public void setFlightIdTag(String flightIdTag) {
        this.flightIdTag = flightIdTag;
    }
}
