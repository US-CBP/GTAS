/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import javax.persistence.*;

@Entity
@Table(name = "message_status")
public class MessageStatus {

    @Id
    @Column(name = "ms_message_id", columnDefinition = "bigint unsigned")
    private
    Long messageId;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "ms_message_id", referencedColumnName = "id", updatable = false, insertable = false)
    @org.hibernate.annotations.ForeignKey( name = "none")
    private Message message;

    @Enumerated(EnumType.STRING)
    @Column(name="ms_status")
    private MessageStatusEnum messageStatusEnum;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Transient
    private boolean success;

    public MessageStatus(){}

    public MessageStatus(Long message, MessageStatusEnum status) {
        this.messageId = message;
        this.messageStatusEnum = status;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public MessageStatusEnum getMessageStatusEnum() {
        return messageStatusEnum;
    }

    public void setMessageStatusEnum(MessageStatusEnum messageStatusEnum) {
        this.messageStatusEnum = messageStatusEnum;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }


}
