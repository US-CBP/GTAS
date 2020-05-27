package gov.gtas.job.scheduler;

import gov.gtas.model.ApisMessage;
import gov.gtas.model.Message;
import gov.gtas.model.MessageStatus;
import gov.gtas.model.Pnr;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public abstract class DataSchedulerThread implements Callable<Boolean> {
    private List<MessageStatus> messageStatuses;

    private Date apisCutOffDate;
    private Date pnrCutOffDate;
    private GTASShareConstraint defaultShareConstraint;


    public void setMessageStatuses(List<MessageStatus> messageStatuses) {
        this.messageStatuses = messageStatuses;
    }

    public List<MessageStatus> getMessageStatuses() {
        return messageStatuses;
    }

    public MessageAndFlightIds getApisMessageIdsAndFlightIds() {
        List<Message> messages = getMessageStatuses().stream().map(MessageStatus::getMessage).collect(Collectors.toList());
        Set<Long> flightIds = getMessageStatuses().stream().map(MessageStatus::getFlightId).collect(Collectors.toSet());
        List<ApisMessage> apisMessageList = messages
                .stream()
                .filter(m -> m instanceof ApisMessage)
                .map(m -> (ApisMessage) m)
                .collect(Collectors.toList());
        Set<Long> apisMessageIds = apisMessageList.stream().map(ApisMessage::getId).collect(Collectors.toSet());
        return new MessageAndFlightIds(flightIds, apisMessageIds);
    }

    public MessageAndFlightIds getPnrMessageIdsAndFlightIds() {
        List<Message> messages = getMessageStatuses().stream().map(MessageStatus::getMessage).collect(Collectors.toList());
        Set<Long> flightIds = getMessageStatuses().stream().map(MessageStatus::getFlightId).collect(Collectors.toSet());
        List<Pnr> pnrMessageList = messages
                .stream()
                .filter(m -> m instanceof Pnr)
                .map(m -> (Pnr) m)
                .collect(Collectors.toList());
        Set<Long> apisMessageIds = pnrMessageList.stream().map(Pnr::getId).collect(Collectors.toSet());
        return new MessageAndFlightIds(flightIds, apisMessageIds);
    }

    public Date getApisCutOffDate() {
        return apisCutOffDate;
    }

    public void setApisCutOffDate(Date apisCutOffDate) {
        this.apisCutOffDate = apisCutOffDate;
    }

    public Date getPnrCutOffDate() {
        return pnrCutOffDate;
    }

    public void setPnrCutOffDate(Date pnrCutOffDate) {
        this.pnrCutOffDate = pnrCutOffDate;
    }


    public GTASShareConstraint getDefaultShareConstraint() {
        return defaultShareConstraint;
    }

    public void setDefaultShareConstraint(GTASShareConstraint defaultShareConstraint) {
        this.defaultShareConstraint = defaultShareConstraint;
    }

}
