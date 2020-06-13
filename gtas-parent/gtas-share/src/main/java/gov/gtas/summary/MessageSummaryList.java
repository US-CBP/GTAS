package gov.gtas.summary;

import java.util.ArrayList;
import java.util.List;

public class  MessageSummaryList {

    private EventIdentifier eventIdentifier;
    private MessageAction messageAction;
    private List<MessageSummary> messageSummaryList = new ArrayList<>();

    public MessageSummaryList() {}

    public static MessageSummaryList from(List<MessageSummary> processedMessageSummaries) {
        MessageSummaryList msl = new MessageSummaryList();
        EventIdentifier ei = processedMessageSummaries.stream().map(MessageSummary::getEventIdentifier).findFirst().orElse(new EventIdentifier());
        msl.setEventIdentifier(ei);
        MessageAction ma = processedMessageSummaries.stream().map(MessageSummary::getAction).findFirst().orElse(MessageAction.ERROR);
        msl.setMessageAction(ma);
        msl.setMessageSummaryList(processedMessageSummaries);
        return msl;
    }

    public List<MessageSummary> getMessageSummaryList() {
        return messageSummaryList;
    }

    public void setMessageSummaryList(List<MessageSummary> messageSummaryList) {
        this.messageSummaryList = messageSummaryList;
    }

    public EventIdentifier getEventIdentifier() {
        return eventIdentifier;
    }

    public void setEventIdentifier(EventIdentifier eventIdentifier) {
        this.eventIdentifier = eventIdentifier;
    }

    public MessageAction getMessageAction() {
        return messageAction;
    }

    public void setMessageAction(MessageAction messageAction) {
        this.messageAction = messageAction;
    }
}
