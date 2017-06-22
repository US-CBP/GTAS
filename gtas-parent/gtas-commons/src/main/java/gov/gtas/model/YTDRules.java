/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
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
@Table(name = "ytd_rules")
public class YTDRules {

    private static final long serialVersionUID = 1L;

    public YTDRules() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Basic(optional = false)
    @Column(name = "id", nullable = false, columnDefinition = "bigint unsigned")
    private Long id;

    @Column(name = "RuleName", nullable = false)
    private String ruleName;

    @Column(name = "RuleHits", nullable = false)
    private Long ruleHits;

    @Column(name = "CreatedBy", nullable = false)
    private String createdBy;

    @Column(name = "LastUpdatedBy", nullable = false)
    private String lastUpdatedBy;

    @Column(name = "CreatedOn", nullable = false)
    //@Temporal(TemporalType.DATE)
    private String createdOn;

    @Column(name = "LastEditedOn", nullable = false)
    //@Temporal(TemporalType.DATE)
    private String lastEditedOn;


    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Long getRuleHits() {
        return ruleHits;
    }

    public void setRuleHits(Long ruleHits) {
        this.ruleHits = ruleHits;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getLastEditedOn() {
        return lastEditedOn;
    }

    public void setLastEditedOn(String lastEditedOn) {
        this.lastEditedOn = lastEditedOn;
    }
}
