/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.lookup;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.gtas.model.BaseEntityAudit;
import gov.gtas.model.HitDetail;
import gov.gtas.model.HitsDisposition;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "rule_category")
public class RuleCat extends BaseEntityAudit {
    private static final long serialVersionUID = 1L;

    @Column(name = "catId", nullable = false)
    private Long catId;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "priority", nullable = false)
    private Long priority;

    @JsonIgnore
    @OneToMany(mappedBy = "ruleCat", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<HitsDisposition> hitsDispositions = new HashSet<>();

    public Long getCatId() {
        return catId;
    }

    public void setCatId(Long catId) {
        this.catId = catId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public RuleCat() {
    }

    public RuleCat(Long catId) {
        this.catId = catId;
    }

//    public List<HitsDisposition> getHitsDispositions() {
//        return hitsDispositions;
//    }
//
//    public void setHitsDispositions(List<HitsDisposition> hitsDispositions) {
//        this.hitsDispositions = hitsDispositions;
//    }


    public Set<HitsDisposition> getHitsDispositions() {
        return hitsDispositions;
    }

    public void setHitsDispositions(Set<HitsDisposition> hitsDispositions) {
        this.hitsDispositions = hitsDispositions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        RuleCat ruleCat = (RuleCat) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(catId, ruleCat.catId)
                .append(category, ruleCat.category)
                .append(description, ruleCat.description)
                .append(priority, ruleCat.priority)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(catId)
                .append(category)
                .append(description)
                .append(priority)
                .toHashCode();
    }
}
