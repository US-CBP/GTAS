/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.vo;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MessageVo {
    private String raw;
    private String hashCode;
    private String transmissionSource;
    private Date transmissionDate;
    private String messageType;
    private String version;
    
    public String getRaw() {
        return raw;
    }
    public void setRaw(String raw) {
        this.raw = raw;
    }
    public String getHashCode() {
        return hashCode;
    }
    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }
    public String getTransmissionSource() {
        return transmissionSource;
    }
    public void setTransmissionSource(String transmissionSource) {
        this.transmissionSource = transmissionSource;
    }
    public Date getTransmissionDate() {
        return transmissionDate;
    }
    public void setTransmissionDate(Date transmissionDate) {
        this.transmissionDate = transmissionDate;
    }
    public String getMessageType() {
        return messageType;
    }
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE); 
    }
}
