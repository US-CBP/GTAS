/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MessageVo {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";

    private String raw;
    private String hashCode;
    private String transmissionSource;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
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
