/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.tamr.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class TamrPassenger {
    private String gtasId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dob;

    private List<TamrDocument> documents;
    private List<String> citizenshipCountry;

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
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public Date getDob() {
        return dob;
    }
    
    public void setDob(Date dob) {
        this.dob = dob;
    }
    
    public List<TamrDocument> getDocuments() {
        return documents;
    }
    
    public void setDocuments(List<TamrDocument> documents) {
        this.documents = documents;
    }
    
    public List<String> getCitizenshipCountry() {
        return citizenshipCountry;
    }
    
    public void setCitizenshipCountry(List<String> citizenshipCountry) {
        this.citizenshipCountry = citizenshipCountry;
    }
}
