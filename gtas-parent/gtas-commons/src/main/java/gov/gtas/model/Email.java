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
@Table(name = "email",
uniqueConstraints={@UniqueConstraint(columnNames={"address"})})
public class Email extends BaseEntityAudit {
    private static final long serialVersionUID = 1L;  
    public Email() { }
    
    @Column(nullable = false)
    private String address;
    
    private String domain;
    
    @ManyToMany(
        mappedBy = "emails",
        targetEntity = Pnr.class
    )
    private Set<Pnr> pnrs = new HashSet<>();

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
    
    public Set<Pnr> getPnrs() {
        return pnrs;
    }

    public void setPnrs(Set<Pnr> pnrs) {
        this.pnrs = pnrs;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.address);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Email other = (Email) obj;
        return Objects.equals(this.address, other.address);
    }       
}
