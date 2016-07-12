/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "loader_audit_logs")
public class InvalidObjectInfo extends BaseEntityAudit{
    
    private static final long serialVersionUID = 1L;  
    
    @Column(name = "object_key")
    private String messageKey;
    
    @Size(max = 4000)
    @Column(name = "object_value")
    private String invalidObjectValue;
    
    @Column(name = "object_type")
    private String invalidObjectType;
    
    @Column(name = "description")
    private String failureDescription;
    
    
    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getInvalidObjectValue() {
        return invalidObjectValue;
    }

    public void setInvalidObjectValue(String invalidObjectValue) {
        this.invalidObjectValue = invalidObjectValue;
    }

    public String getInvalidObjectType() {
        return invalidObjectType;
    }

    public void setInvalidObjectType(String invalidObjectType) {
        this.invalidObjectType = invalidObjectType;
    }

    public String getFailureDescription() {
        return failureDescription;
    }

    public void setFailureDescription(String failureDescription) {
        this.failureDescription = failureDescription;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.messageKey, this.invalidObjectValue,this.invalidObjectType);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final InvalidObjectInfo other = (InvalidObjectInfo) obj;
        return Objects.equals(this.messageKey, other.messageKey) 
                && Objects.equals(this.invalidObjectType,other.invalidObjectType)
                && Objects.equals(this.invalidObjectValue,other.invalidObjectValue);
    }           
}
