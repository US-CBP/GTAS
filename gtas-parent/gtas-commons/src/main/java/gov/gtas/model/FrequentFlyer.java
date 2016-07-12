/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
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
@Table(name = "frequent_flyer",
    uniqueConstraints={@UniqueConstraint(columnNames={"carrier", "number"})}
)
public class FrequentFlyer extends BaseEntityAudit {
    private static final long serialVersionUID = 1L;  
    public FrequentFlyer() { }
    
    @Column(nullable = false)
    private String carrier;
    
    @Column(nullable = false)
    private String number;
    
    @ManyToMany(
        mappedBy = "frequentFlyers",
        targetEntity = Pnr.class
    )
    private Set<Pnr> pnrs = new HashSet<>();
    
    public Set<Pnr> getPnrs() {
        return pnrs;
    }

    public void setPnrs(Set<Pnr> pnrs) {
        this.pnrs = pnrs;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.number, this.carrier);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final FrequentFlyer other = (FrequentFlyer) obj;
        return Objects.equals(this.number, other.number) && 
                Objects.equals(this.carrier, other.carrier);
    }       
}
