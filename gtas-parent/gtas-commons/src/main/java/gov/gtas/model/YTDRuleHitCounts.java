/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;


import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "ytd_rule_hit_counts")
public class YTDRuleHitCounts {

    private static final long serialVersionUID = 1L;

    public YTDRuleHitCounts() {
    }

    @Id
    @Column(name = "ruleid", nullable = false)
    private Long ruleId;

    @Column(name = "ruleref", nullable = false)
    private Long ruleRef;

    @Column(name = "hits", nullable = false)
    private Long hits;

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Long getRuleRef() {
        return ruleRef;
    }

    public void setRuleRef(Long ruleRef) {
        this.ruleRef = ruleRef;
    }

    public Long getHits() {
        return hits;
    }

    public void setHits(Long hits) {
        this.hits = hits;
    }
}
