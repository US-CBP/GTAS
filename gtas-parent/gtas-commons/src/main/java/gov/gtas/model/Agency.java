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

/**
 * Travel agency
 */
@Entity
@Table(name = "agency")
public class Agency extends BaseEntityAudit {
    private static final long serialVersionUID = 1L;
    public Agency() { }
    
    /** 
     * Company identification: could be an airline code or
     * the name of the agency.
     */
    @Column(nullable = false)    
    private String name;
    
    /** IATA airport/city code of the delivering system */
    private String location;
    
    /** IATA travel agency ID number */
    private String identifier;
    
    private String country;

    private String phone;
    
    private String city;
    
    @ManyToMany(
        mappedBy = "agencies",
        targetEntity = Pnr.class
    )
    private Set<Pnr> pnrs = new HashSet<>();
    
    
    public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<Pnr> getPnrs() {
        return pnrs;
    }

    public void setPnrs(Set<Pnr> pnrs) {
        this.pnrs = pnrs;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.location);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Agency other = (Agency) obj;
        return Objects.equals(this.name, other.name)
                && Objects.equals(this.location, other.location);
    }       
}
