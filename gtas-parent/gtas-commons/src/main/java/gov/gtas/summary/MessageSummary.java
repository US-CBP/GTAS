package gov.gtas.summary;

public class MessageSummary {

    private String rawMessage;
    private String messageType;
    private String primeFlight;
    private Boolean relatedToDerog;
    private String countryCode;

    public Boolean getRelatedToDerog() {
        return relatedToDerog;
    }

    public void setRelatedToDerog(Boolean relatedToDerog) {
        this.relatedToDerog = relatedToDerog;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPrimeFlight() {
        return primeFlight;
    }

    public void setPrimeFlight(String primeFlight) {
        this.primeFlight = primeFlight;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }
}
