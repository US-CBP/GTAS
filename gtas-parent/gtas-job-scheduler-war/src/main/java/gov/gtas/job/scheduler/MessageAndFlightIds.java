package gov.gtas.job.scheduler;

import java.util.Set;

public class MessageAndFlightIds {
    private final Set<Long> flightIds;
    private final Set<Long> messageIds;

    public MessageAndFlightIds(Set<Long> flightIds, Set<Long> messageIds) {
        this.flightIds = flightIds;
        this.messageIds = messageIds;
    }

    public Set<Long> getFlightIds() {
        return flightIds;
    }

    public Set<Long> getMessageIds() {
        return messageIds;
    }
}
