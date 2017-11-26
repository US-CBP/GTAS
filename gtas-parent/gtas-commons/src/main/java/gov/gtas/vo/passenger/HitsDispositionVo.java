/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */

package gov.gtas.vo.passenger;

import gov.gtas.model.HitsDispositionComments;
import gov.gtas.model.lookup.RuleCat;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Set;

public class HitsDispositionVo {

    private long hitId;
    private long caseId;
    private String description;
    private String status;
    private String valid;
    private Set<HitsDispositionComments> dispComments;
    private Set<RuleCat> ruleCatSet;
    private String category;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        HitsDispositionVo that = (HitsDispositionVo) o;

        return new EqualsBuilder()
                .append(hitId, that.hitId)
                .append(caseId, that.caseId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(hitId)
                .append(caseId)
                .toHashCode();
    }

    public long getHitId() {
        return hitId;
    }

    public void setHitId(long hitId) {
        this.hitId = hitId;
    }

    public long getCaseId() {
        return caseId;
    }

    public void setCaseId(long caseId) {
        this.caseId = caseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public Set<HitsDispositionComments> getDispComments() {
        return dispComments;
    }

    public void setDispComments(Set<HitsDispositionComments> dispComments) {
        this.dispComments = dispComments;
    }

    public Set<RuleCat> getRuleCatSet() {
        return ruleCatSet;
    }

    public void setRuleCatSet(Set<RuleCat> ruleCatSet) {
        this.ruleCatSet = ruleCatSet;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
