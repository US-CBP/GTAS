/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "credit_card",
    uniqueConstraints={@UniqueConstraint(columnNames={"card_type", "number", "expiration"})}
)
public class CreditCard extends BaseEntityAudit {
    private static final long serialVersionUID = 1L;  
    public CreditCard() { }
    
    @Column(name = "card_type")
    private String cardType;
    
    @Column(nullable = false)
    private String number;
    
    @Temporal(TemporalType.DATE)
    private Date expiration;
    
    @Column(name = "account_holder")
    private String accountHolder;

    @ManyToMany(
        mappedBy = "creditCards",
        targetEntity = Pnr.class
    )
    private Set<Pnr> pnrs = new HashSet<>();

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

    public Set<Pnr> getPnrs() {
        return pnrs;
    }

    public void setPnrs(Set<Pnr> pnrs) {
        this.pnrs = pnrs;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.cardType, this.number, this.expiration);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final CreditCard other = (CreditCard) obj;
        return Objects.equals(this.cardType, other.cardType)
                && Objects.equals(this.number, other.number) 
                && Objects.equals(this.expiration, other.expiration);
    }           
}
