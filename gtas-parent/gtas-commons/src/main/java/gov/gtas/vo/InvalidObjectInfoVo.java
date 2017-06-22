/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo;

import java.util.Date;
/**
 * class InvalidObjectInfoVo used to store invalid value object information and store the same in 
 * database for verification and track the failed to load objects due to insufficient information.
 * This class is used in MessageValidators and while validating the object graph the invalid object 
 * will be set to null to avoid loading issues and the same will be copied into this object which will 
 * be stored in database as InvalidObjectInfo
 */
public class InvalidObjectInfoVo {
    
    private String invalidObjectKey;
    private String invalidObjectValue;
    private String invalidObjectType;
    private String description;
    private Date createdDate;
    private String createdBy;
    private Long id;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getInvalidObjectKey() {
        return invalidObjectKey;
    }
    public void setInvalidObjectKey(String invalidObjectKey) {
        this.invalidObjectKey = invalidObjectKey;
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
    public Date getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    public String getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    

}
