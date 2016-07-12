/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.vo;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import gov.gtas.validators.Validatable;

public class CreditCardVo implements Validatable {
    private String cardType;
    private String number;
    private Date expiration;
    private String accountHolder;
    
    public String getCardType() {
        return cardType;
    }
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public Date getExpiration() {
        return expiration;
    }
    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }
    public String getAccountHolder() {
        return accountHolder;
    }
    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }
    @Override
    public boolean isValid() {
        return StringUtils.isNotBlank(this.number);
    }
}
