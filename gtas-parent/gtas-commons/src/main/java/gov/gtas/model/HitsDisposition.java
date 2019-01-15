/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.gtas.model.lookup.RuleCat;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.persistence.criteria.Fetch;


import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "hits_disposition")
public class HitsDisposition extends BaseEntityAudit {
    private static final long serialVersionUID = 1L;

    public HitsDisposition() {
    }

    public HitsDisposition(Long hit) {
        this.hitId = hit;
    }

    @Column(name = "hit_id")
    private long hitId;


    @ManyToOne(fetch = FetchType.EAGER, optional=false)
    @JoinColumn(name="case_id", insertable=false, updatable=false)
    private Case aCase;

    @Column(name = "description")
    private String description;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "valid", nullable = true)
    private String valid;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinColumn(name = "hit_disp_id", nullable = false)
    private Set<HitsDispositionComments> dispComments = new HashSet<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rule_cat_id")
    private RuleCat ruleCat;

    public void addHitsDispositionComments (HitsDispositionComments _tempComments){
        dispComments.add(_tempComments);
        //_tempComments.setHitDispId(this);
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<HitsDispositionComments> getDispComments() {
        return dispComments;
    }

    public void setDispComments(Set<HitsDispositionComments> dispComments) {
        this.dispComments = dispComments;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getHitId() {
        return hitId;
    }

    public void setHitId(long hitId) {
        this.hitId = hitId;
    }


    public Case getaCase() {
        return aCase;
    }

    public void setaCase(Case aCase) {
        this.aCase = aCase;
    }

    public RuleCat getRuleCat() {
        return ruleCat;
    }

    public void setRuleCat(RuleCat ruleCat) {
        this.ruleCat = ruleCat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        HitsDisposition that = (HitsDisposition) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(hitId, that.hitId)
               // .append(caseId, that.caseId)
                .append(aCase, that.aCase)
                .append(id, that.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(hitId)
                //.append(caseId)
                .append(aCase)
                .append(id)
                .toHashCode();
    }
}
