/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.lookup;

import gov.gtas.model.BaseEntityAudit;
import gov.gtas.model.HitDetail;
import gov.gtas.model.HitsDisposition;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "ruleCat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HitsDisposition> hitsDispositions = new ArrayList<HitsDisposition>();

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

    public List<HitsDisposition> getHitsDispositions() {
        return hitsDispositions;
    }

    public void setHitsDispositions(List<HitsDisposition> hitsDispositions) {
        this.hitsDispositions = hitsDispositions;
    }
}
