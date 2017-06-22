/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Embeddable
public class EdifactMessage {
    public EdifactMessage() { }
    
    /** PAXLST, PNRGOV, etc. derived from UNH */
    @Column(name = "message_type", length = 10)
    private String messageType;
    
    /** from UNB */
    @Column(name = "transmission_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date transmissionDate;
    
    /** the message sender; from UNB */
    @Column(name = "transmission_source")
    private String transmissionSource;

    /** message version (e.g., D13B) derived from UNH */
    @Column(length = 10)
    private String version;
    
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Date getTransmissionDate() {
        return transmissionDate;
    }

    public void setTransmissionDate(Date transmissionDate) {
        this.transmissionDate = transmissionDate;
    }

    public String getTransmissionSource() {
        return transmissionSource;
    }

    public void setTransmissionSource(String transmissionSource) {
        this.transmissionSource = transmissionSource;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
