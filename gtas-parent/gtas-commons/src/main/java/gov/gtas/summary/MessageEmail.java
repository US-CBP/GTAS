package gov.gtas.summary;

import gov.gtas.model.Email;
import org.springframework.beans.BeanUtils;

public class MessageEmail {

    private String address;

    private String domain;

    private String flightIdTag;

    private String messageIdTag;

    public static MessageEmail from(String flightIdTag, String messageIdTag, Email email) {
        MessageEmail pe = new MessageEmail();
        BeanUtils.copyProperties(email, pe);
        pe.setFlightIdTag(flightIdTag);
        pe.setMessageIdTag(messageIdTag);
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

    public String getFlightIdTag() {
        return flightIdTag;
    }

    public void setFlightIdTag(String flightIdTag) {
        this.flightIdTag = flightIdTag;
    }

    public String getMessageIdTag() {
        return messageIdTag;
    }

    public void setMessageIdTag(String messageIdTag) {
        this.messageIdTag = messageIdTag;
    }
}
