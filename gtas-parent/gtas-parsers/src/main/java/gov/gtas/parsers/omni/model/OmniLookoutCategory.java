/*
 * All GTAS code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.omni.model;

public class OmniLookoutCategory {
    private Long passengerNumber;
    private Long lookoutCategoryBitMask;

    public Long getPassengerNumber() {
        return passengerNumber;
    }

    public void setPassengerNumber(Long passengerNumber) {
        this.passengerNumber = passengerNumber;
    }

    public Long getLookoutCategoryBitMask() {
        return lookoutCategoryBitMask;
    }

    public void setLookoutCategoryBitMask(Long lookoutCategoryBitMask) {
        this.lookoutCategoryBitMask = lookoutCategoryBitMask;
    }
}
