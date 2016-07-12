/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo;

import gov.gtas.model.HitDetail;
import gov.gtas.model.HitsSummary;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class HitDetailVo {
    
    private HitsSummary parent;

    private String ruleConditions;

    private Date createDate;

    private Long ruleId;
    
    private String ruleTitle;
    
    private String ruleDesc;
    
    private String ruleType;
    
    private HashMap<Integer, List<HitDetail>> HitsRulesAndDetails;

    private List<HitDetail> hitsDetailsList = new ArrayList<HitDetail>();
    
    public HitsSummary getParent() {
        return parent;
    }

    public void setParent(HitsSummary parent) {
        this.parent = parent;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public String getRuleTitle() {
        return ruleTitle;
    }

    public void setRuleTitle(String ruleTitle) {
        this.ruleTitle = ruleTitle;
    }

    public String getRuleDesc() {
        return ruleDesc;
    }

    public void setRuleDesc(String ruleDesc) {
        this.ruleDesc = ruleDesc;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getRuleConditions() {
        return ruleConditions;
    }

    public void setRuleConditions(String ruleConditions) {
        this.ruleConditions = ruleConditions;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public List<HitDetail> getHitsDetailsList() {
        return hitsDetailsList;
    }

    public void setHitsDetailsList(List<HitDetail> hitsDetailsList) {
        this.hitsDetailsList = hitsDetailsList;
    }

    public HashMap<Integer, List<HitDetail>> getHitsRulesAndDetails() {
        return HitsRulesAndDetails;
    }

    public void setHitsRulesAndDetails(
            HashMap<Integer, List<HitDetail>> hitsRulesAndDetails) {
        HitsRulesAndDetails = hitsRulesAndDetails;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((ruleConditions == null) ? 0 : ruleConditions.hashCode());
        result = prime * result
                + ((ruleDesc == null) ? 0 : ruleDesc.hashCode());
        result = prime * result + ((ruleId == null) ? 0 : ruleId.hashCode());
        result = prime * result
                + ((ruleTitle == null) ? 0 : ruleTitle.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof HitDetailVo))
            return false;
        HitDetailVo other = (HitDetailVo) obj;
        if (ruleConditions == null) {
            if (other.ruleConditions != null)
                return false;
        } else if (!ruleConditions.equals(other.ruleConditions))
            return false;
        if (ruleDesc == null) {
            if (other.ruleDesc != null)
                return false;
        } else if (!ruleDesc.equals(other.ruleDesc))
            return false;
        if (ruleId == null) {
            if (other.ruleId != null)
                return false;
        } else if (!ruleId.equals(other.ruleId))
            return false;
        if (ruleTitle == null) {
            if (other.ruleTitle != null)
                return false;
        } else if (!ruleTitle.equals(other.ruleTitle))
            return false;
        return true;
    }
    
    
    
}
