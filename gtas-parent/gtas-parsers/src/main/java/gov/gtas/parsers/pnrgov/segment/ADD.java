/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.pnrgov.segment;

import java.util.List;

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
            // if (freeText != null && freeText.contains("CTC")) {
            if (freeText != null) {
                this.telephone = freeText;
            }
            if(freeText != null && freeText.contains("CTCE")){
            	this.email = freeText;
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
