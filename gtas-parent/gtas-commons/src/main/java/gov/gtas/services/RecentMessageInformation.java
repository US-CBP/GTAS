package gov.gtas.services;

import gov.gtas.enumtype.MessageType;

public class RecentMessageInformation {

    private MessageType messageType;
    private Long messageId;

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
}
