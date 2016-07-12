/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.vo;

import org.apache.commons.lang3.StringUtils;

import gov.gtas.validators.Validatable;

public class EmailVo implements Validatable {
    private String address;
    private String domain;

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getDomain() {
        return domain;
    }
    public void setDomain(String domain) {
        this.domain = domain;
    }
    @Override
    public boolean isValid() {
        return StringUtils.isNotBlank(this.address);
    }
}
