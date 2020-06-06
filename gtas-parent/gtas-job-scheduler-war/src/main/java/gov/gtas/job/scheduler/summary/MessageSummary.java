package gov.gtas.job.scheduler.summary;

import gov.gtas.job.scheduler.EventIdentifier;

public class MessageSummary {

    private String rawMessage;
    private EventIdentifier eventIdentifier;
    private Boolean relatedToDerog;

    public Boolean getRelatedToDerog() {
        return relatedToDerog;
    }

    public void setRelatedToDerog(Boolean relatedToDerog) {
        this.relatedToDerog = relatedToDerog;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    public EventIdentifier getEventIdentifier() {
        return eventIdentifier;
    }

    public void setEventIdentifier(EventIdentifier eventIdentifier) {
        this.eventIdentifier = eventIdentifier;
    }
}