package gov.gtas.summary;

public class MessageEmail {

    private String address;

    private String domain;

    private String flightIdTag;

    private String messageIdTag;

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
