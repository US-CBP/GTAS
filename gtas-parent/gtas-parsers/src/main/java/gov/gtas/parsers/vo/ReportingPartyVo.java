/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.vo;

import org.apache.commons.lang3.StringUtils;

import gov.gtas.validators.Validatable;

public class ReportingPartyVo implements Validatable {
    private String partyName;
    private String telephone;
    private String fax;
    
    public String getPartyName() {
        return partyName;
    }
    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }
    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    public String getFax() {
        return fax;
    }
    public void setFax(String fax) {
        this.fax = fax;
    }
    @Override
    public boolean isValid() {
        return StringUtils.isNotBlank(this.partyName);
    }    
}
