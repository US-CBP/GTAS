/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.json;

import static gov.gtas.constant.JsonResponseConstants.ATTR_ERROR_CODE;
import static gov.gtas.constant.JsonResponseConstants.ATTR_ERROR_DETAIL;
import static gov.gtas.constant.JsonResponseConstants.ATTR_ERROR_ID;
import gov.gtas.enumtype.Status;
import gov.gtas.error.ErrorDetailInfo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
/**
 * Rule meta-data JSON object class.
 */
public class JsonServiceResponse implements Serializable {
    private static final long serialVersionUID = -1376823917772400644L;
    private Status status;
    private String message;
    private Object result;
    private List<ServiceResponseDetailAttribute> responseDetails;
    
    /**
     * Basic constructor.
     * @param status the status.
     * @param message response message.
     */
    public JsonServiceResponse(Status status, String message){
        this.status = status;
        this.message = message;
        this.responseDetails = new LinkedList<JsonServiceResponse.ServiceResponseDetailAttribute>();
    }
    /**
     * Constructor to support get or "get list" response.
     * @param status the status.
     * @param message response message.
     * @param result the result object.
     */
    public JsonServiceResponse(Status status, String message, Object result){
        this.status = status;
        this.message = message;
        this.result = result;
        this.responseDetails = new LinkedList<JsonServiceResponse.ServiceResponseDetailAttribute>();
    }
    /**
     * Constructor for error response.
     * @param error  the error details object.
     * 
     */
    public JsonServiceResponse(ErrorDetailInfo error){
        this.status = Status.FAILURE;
        this.message = error.getErrorDescription();
        this.responseDetails = new LinkedList<JsonServiceResponse.ServiceResponseDetailAttribute>();
        responseDetails.add(new ServiceResponseDetailAttribute(ATTR_ERROR_CODE, error.getErrorCode()));
        if(error.getErrorId() != null){
            responseDetails.add(new ServiceResponseDetailAttribute(ATTR_ERROR_ID, error.getErrorId()));
        }
        String[] errorDetail = error.getErrorDetails();
        if(errorDetail != null && errorDetail.length > 0){
            responseDetails.add(new ServiceResponseDetailAttribute(ATTR_ERROR_DETAIL, errorDetail));
        }
    }
    /**
     * Constructor for error response.
     * @param errorCode
     * @param description
     * @param errorDetail
     */
    public JsonServiceResponse(String errorCode, String description, String[] errorDetail){
        this.status = Status.FAILURE;
        this.message = description;
        this.responseDetails = new LinkedList<JsonServiceResponse.ServiceResponseDetailAttribute>();
        responseDetails.add(new ServiceResponseDetailAttribute(ATTR_ERROR_CODE, errorCode));
        if(errorDetail != null && errorDetail.length > 0){
            responseDetails.add(new ServiceResponseDetailAttribute(ATTR_ERROR_DETAIL, errorDetail));
        }
    }
    /**
     * Fetches the value of an attribute by name.
     * @param responseDetailName the name of the attribute to fetch.
     * @return the attribute value or null if not found.
     */
    public String findResponseDetailValue(final String responseDetailName){
        String ret = null;
        for(ServiceResponseDetailAttribute attr:this.responseDetails){
            if(responseDetailName.equals(attr.getAttributeName())){
                ret = (String)attr.getAttributeValue();
                break;
            }
        }
        return ret;
    }
    /**
     * @return the status
     */
    public Status getStatus() {
        return status;
    }
    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * @return the result
     */
    public Object getResult() {
        return result;
    }
    /**
     * @param result the result to set
     */
    public void setResult(Object result) {
        this.result = result;
    }
    /**
     * @return the responseDetails
     */
    public List<ServiceResponseDetailAttribute> getResponseDetails() {
        return responseDetails;
    }
    /**
     * @param responseDetail the responseDetail to add.
     */
    public void addResponseDetails(
            ServiceResponseDetailAttribute responseDetail) {
        this.responseDetails.add(responseDetail);
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    public static class ServiceResponseDetailAttribute{
        private String attributeName;
        private Serializable attributeValue;
        public ServiceResponseDetailAttribute(String attrName, String attrValue){
            attributeName = attrName;
            attributeValue = attrValue;
        }
        public ServiceResponseDetailAttribute(String attrName, Serializable attrValue){
            attributeName = attrName;
            attributeValue = attrValue;
        }
        /**
         * @return the attributeName
         */
        public String getAttributeName() {
            return attributeName;
        }
        /**
         * @return the attributeValue
         */
        public Serializable getAttributeValue() {
            return attributeValue;
        }
        
    }
}
