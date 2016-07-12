/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.error;

import gov.gtas.util.DateCalendarUtils;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

public class BasicErrorDetailInfo implements ErrorDetailInfo {
    private Long errorId;
    private String errorCode;
    private String timestamp;
    private String errorDescription;
    private List<String> errorDetails;
    
    public BasicErrorDetailInfo(Long id, String code, Date timestamp, String description, List<String> details){
        this.errorDetails = details;
        this.errorCode = code;
        this.timestamp = DateCalendarUtils.formatRuleEngineDateTime(timestamp);
        this.errorDescription = description != null?description:StringUtils.EMPTY;
        this.errorId = id;
    }

    @Override
    public Long getErrorId() {
        return errorId;
    }
    @Override
    public String[] getErrorDetails() {
        if(CollectionUtils.isEmpty(this.errorDetails)){
            return new String[0];
        } else {
            String[] detArr = new String[this.errorDetails.size()];
            detArr = this.errorDetails.toArray(detArr);
            return detArr;          
        }
    }               
    @Override
    public String getErrorDescription() {
        return this.errorDescription;
    }               
    @Override
    public String getErrorCode() {
        return this.errorCode;
    }
    /**
     * @param errorDetails the errorDetails to set
     */
    public void setErrorDetails(List<String> errorDetails) {
        this.errorDetails = errorDetails;
    }
    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    /**
     * @param errorDescription the errorDescription to set
     */
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
    @Override
    public String getErrorTimestamp() {
        return this.timestamp;
    }

}
