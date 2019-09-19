/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.vo;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import gov.gtas.parsers.pnrgov.segment.SSRDoco;
import gov.gtas.parsers.util.ParseUtils;
import gov.gtas.validators.Validatable;

public class DocumentVo implements Validatable {
    private String documentType;
    private String documentNumber;
    private Date expirationDate;
    private Date issuanceDate;
    private String issuanceCountry;
    /**
     * date format used for passport/visa expiration, issuance date
     */
    private static final String DOC_DATE_FORMAT = "ddMMMyy";
    
    public DocumentVo() {
    	
    }
    
    public DocumentVo(SSRDoco ssrDoco) {
    	this.setDocumentNumber(ssrDoco.getVisaDocNumber());
    	this.setDocumentType(ssrDoco.getSsrDocoType().toString());
    	this.setExpirationDate(null); //SSRDoco does not have an expiration date field
    	this.setIssuanceCountry(ssrDoco.getVisaDocPlaceOfIssuance());
    	this.setIssuanceDate(ParseUtils.parseDateTime(ssrDoco.getVisaDocIssuanceDate(), DOC_DATE_FORMAT));
    }

    public String getDocumentType() {
        return documentType;
    }
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
    public String getDocumentNumber() {
        return documentNumber;
    }
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
    public Date getExpirationDate() {
        return expirationDate;
    }
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
    public Date getIssuanceDate() {
        return issuanceDate;
    }
    public void setIssuanceDate(Date issuanceDate) {
        this.issuanceDate = issuanceDate;
    }
    public String getIssuanceCountry() {
        return issuanceCountry;
    }
    public void setIssuanceCountry(String issuanceCountry) {
        this.issuanceCountry = issuanceCountry;
    }
    @Override
    public boolean isValid() {
        return StringUtils.isNotBlank(this.documentNumber) 
               && StringUtils.isNotBlank(this.documentType);
    }    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
