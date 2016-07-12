/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.vo;

import org.apache.commons.lang3.StringUtils;

import gov.gtas.validators.Validatable;

public class PhoneVo implements Validatable {
    private String number;
    private String city;
    
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    @Override
    public boolean isValid() {
        return StringUtils.isNotBlank(this.number);
    }
}
