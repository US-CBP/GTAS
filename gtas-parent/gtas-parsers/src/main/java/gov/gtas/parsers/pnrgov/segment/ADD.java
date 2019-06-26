/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.pnrgov.segment;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import gov.gtas.parsers.edifact.Composite;
import gov.gtas.parsers.edifact.Segment;

/**
 * <p>
 * ADD: Address Information
 * <p>
 * Address Information of a traveler in PNR
 * <p>
 * The ADD in GR.1 at level 2 may contain a contact address for the PNR.
 * <p>
 * The ADD in GR.2 at level 3 may contain emergency contact information and or/
 * UMNR delivery and collection addresses
 * <p>
 * The ADD in GR.4 at level 5 may contain the address of the payer of the
 * ticket.
 * <p>
 * If the address and/or telephone information cannot be broken down in separate
 * elements, the information may be found in OSIs and SSRs.
 * <p>
 * Ex:The contact address is 4532 Wilson Street, Philadelphia, zip code 34288
 * (ADD++700:4532 WILSON STREET:PHILADELPHIA:PA::US:34288’)
 * EMAIL
 * (ADD++700:::::::EK CTCE SOME.ABC//YAHOO.COM)
 * ADD++E:::::::FIRST.LAST@GMAIL.COM' //Issue 467
 * ADD++:::::::TBM MAIL TO+:::::::FIRST NAME LAST NAME:::::::99 STREET:::::::CITY STATE LONGZIPCODE'
 */
public class ADD extends Segment {
    private String addressType;
    private String streetNumberAndName;
    private String city;
    private String stateOrProvinceCode;

    /** ISO 3166-1-alpha 2 code */
    private String countryCode;

    private String postalCode;

    private String telephone;
    private String email;

    public ADD(List<Composite> composites) {
        super(ADD.class.getSimpleName(), composites);

        Composite c = getComposite(1);
        if (c != null) {

            this.addressType = c.getElement(0);
            this.streetNumberAndName = c.getElement(1);
            this.city = c.getElement(2);
            this.stateOrProvinceCode = c.getElement(3);
            // Country sub-entity name
            // not recorded
            this.countryCode = c.getElement(5);
            this.postalCode = c.getElement(6);

            String freeText = c.getElement(7);

            if(freeText != null && freeText.contains("CTCE")){
            	this.email = freeText;
            }

            if((StringUtils.isNotBlank(c.getElement(0)) && ("E".equalsIgnoreCase(c.getElement(0))))){
            	this.email = freeText;
            }
            if((StringUtils.isNotBlank(c.getElement(0)) && ("M".equalsIgnoreCase(c.getElement(0))))){
            	this.telephone = freeText;
            }
            if((StringUtils.isNotBlank(c.getElement(0)) && ("H".equalsIgnoreCase(c.getElement(0))))){
            	this.telephone = freeText;
            }
            if((StringUtils.isNotBlank(c.getElement(0)) && ("O".equalsIgnoreCase(c.getElement(0))))){
            	this.telephone = freeText;
            }
            //ADD++N:::::::M+1234567890' the number of :'s may vary.
            //Data | phone | ADD++N:::::::M+ #581 fix
            if((StringUtils.isNotBlank(c.getElement(0)) && ("N".equalsIgnoreCase(c.getElement(0))))){
            	List<Composite> composits = getComposites();
            	Composite lastOne=composits.get(composits.size()-1);
            	if(StringUtils.isNotBlank(lastOne.getElement(0))){
            		this.telephone = lastOne.getElement(0);
            	}
            }
            if(StringUtils.isBlank(c.getElement(0)) && StringUtils.isNotBlank(c.getElement(7)) &&
            		c.getElement(7).contains("TBM")){
               	//ADD++:::::::TBM MAIL TO+:::::::FIRST NAME LAST NAME:::::::99 STREET:::::::CITY STATE LONGZIPCODE'
            	Composite c2 = getComposite(2);
            	if(c2 != null){
            		this.addressType = "MAIL TO";
                    this.streetNumberAndName = c2.getElement(14);
                    String temp=c2.getElement(21);
                    if(StringUtils.isNotBlank(temp)){
                    	String[] tokens=temp.split(" ");
                    	if(tokens.length >= 3){
                    		this.city = tokens[0];
                    		this.stateOrProvinceCode = tokens[1];
                    		this.postalCode = tokens[2];
                    	}
                    }

            	}
            }
        }
    }

    
    public String getEmail() {
		return email;
	}

	public String getAddressType() {
        return addressType;
    }

    public String getStreetNumberAndName() {
        return streetNumberAndName;
    }

    public String getCity() {
        return city;
    }

    public String getStateOrProvinceCode() {
        return stateOrProvinceCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getTelephone() {
        return telephone;
    }
}
