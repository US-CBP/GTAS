/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import javax.persistence.*;

import gov.gtas.model.lookup.DispositionStatus;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "cases")
public class Case extends BaseEntityAudit {
    private static final long serialVersionUID = 1L;

    public Case() { }

    @Column(name = "flight_id", nullable = false)
    private Long flight_id;

    @Column(name = "pax_id", nullable = false)
    private Long pax_id;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "description", nullable = true)
    private String description;

    @OneToMany
    @JoinColumn(name="hit_id", referencedColumnName = "id")
    private Set<HitsDisposition> hits_disp;

    public Set<HitsDisposition> getHits_disp() {
        return hits_disp;
    }

    public void setHits_disp(Set<HitsDisposition> hits_disp) {
        this.hits_disp = hits_disp;
    }

    public Long getFlight_id() {
        return flight_id;
    }

    public void setFlight_id(Long flight_id) {
        this.flight_id = flight_id;
    }

    public Long getPax_id() {
        return pax_id;
    }

    public void setPax_id(Long pax_id) {
        this.pax_id = pax_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
