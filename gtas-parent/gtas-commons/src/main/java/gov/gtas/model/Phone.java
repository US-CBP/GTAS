/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "phone")
public class Phone extends BaseEntityAudit {
    private static final long serialVersionUID = 1L;  
    public Phone() { }
    
    @Column(nullable = false)
    private String number;

	@ManyToMany(
        mappedBy = "phones",
        targetEntity = Pnr.class
    )
    private Set<Pnr> pnrs = new HashSet<>();
    
    @ManyToMany(
            mappedBy = "phones",
            targetEntity = Pnr.class
        )
    private Set<ApisMessage> apisMessages = new HashSet<>();

    
    public Set<ApisMessage> getApisMessages() {
		return apisMessages;
	}

	public void setApisMessages(Set<ApisMessage> apisMessages) {
		this.apisMessages = apisMessages;
	}
	
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
    
    public Set<Pnr> getPnrs() {
        return pnrs;
    }

    public void setPnrs(Set<Pnr> pnrs) {
        this.pnrs = pnrs;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.number);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Phone other = (Phone) obj;
        return Objects.equals(this.number, other.number);
    }       
}
