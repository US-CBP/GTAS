/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.vo;

import org.apache.commons.lang3.StringUtils;

import gov.gtas.validators.Validatable;

public class AddressVo implements Validatable {
    private String type;
    private String line1;
    private String line2;
    private String line3;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String phoneNumber;
    private String email;
    
    
    
    public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getLine1() {
        return line1;
    }
    public void setLine1(String line1) {
        this.line1 = line1;
    }
    public String getLine2() {
        return line2;
    }
    public void setLine2(String line2) {
        this.line2 = line2;
    }
    public String getLine3() {
        return line3;
    }
    public void setLine3(String line3) {
        this.line3 = line3;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean isValid() {
        boolean status = true;
        if (this.getCountry() != null && this.getCountry().length() > 3) 
            status=false;
        if (!StringUtils.isNotBlank(this.line1)) {
            status = false;
        }
        return status;
    }
}
