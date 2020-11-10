/*
 * All GTAS code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.omni.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class OmniPassenger {
    private String gtasId;
    private String firstName;
    private String middleName;
    private String lastName;
    private OmniRawProfile omniRawProfile;
    private List<OmniDocument> documents;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dob;

    public String getGtasId() {
        return gtasId;
    }
	
    public void setGtasId(String gtasId) {
        this.gtasId = gtasId;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getMiddleName() {
        return middleName;
    }
    
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public Date getDob() {
        return dob;
    }
    
    public void setDob(Date dob) {
        this.dob = dob;
    }

    public OmniRawProfile getOmniRawProfile() {
        return omniRawProfile;
    }

    public void setOmniRawProfile(OmniRawProfile omniRawProfile) {
        this.omniRawProfile = omniRawProfile;
    }

    public List<OmniDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<OmniDocument> documents) {
        this.documents = documents;
    }
}
