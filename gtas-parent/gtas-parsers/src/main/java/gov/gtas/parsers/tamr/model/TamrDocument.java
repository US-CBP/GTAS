/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.tamr.model;

public class TamrDocument {
    private String documentId;
    private String documentType;
    private String documentIssuingCountry;
    
    public String getDocumentId() {
        return documentId;
    }
    
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    
    public String getDocumentType() {
        return documentType;
    }
    
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
    
    public String getDocumentIssuingCountry() {
        return documentIssuingCountry;
    }
    
    public void setDocumentIssuingCountry(String documentIssuingCountry) {
        this.documentIssuingCountry = documentIssuingCountry;
    }
}
