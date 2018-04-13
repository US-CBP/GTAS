/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;


import org.springframework.cache.annotation.Cacheable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Cacheable
@Entity
@Table(name = "passenger_id_tag")
public class PassengerIDTag extends BaseEntityAudit {
    private static final long serialVersionUID = 1L;

    @Column(name = "idTag")
    private String idTag;

    @OneToMany(fetch=FetchType.EAGER, targetEntity = Passenger.class, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(name = "pax_idtag", joinColumns = @JoinColumn(name = "pax_tag_id"), inverseJoinColumns = @JoinColumn(name = "pax_id"))
    private Set<Passenger> passengers = new HashSet<>();

    public String getIdTag() {
        return idTag;
    }

    public void setIdTag(String idTag) {
        this.idTag = idTag;
    }

    public Set<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(Set<Passenger> passengers) {
        this.passengers = passengers;
    }
}
