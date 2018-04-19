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

import java.util.Objects;
import java.util.Set;

public class HitsDispositionVo {

    private long hitId;
    private long caseId;
    private String description;
    private String status;
    private String valid;
    private Set<HitsDispositionComments> dispComments;
    private Set<HitsDispositionCommentsVo> dispCommentsVo;
    private Set<RuleCat> ruleCatSet;
    private String category;
    private long hit_disp_id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        HitsDispositionVo that = (HitsDispositionVo) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(hitId, that.hitId)
                 .append(caseId, that.caseId)
                //.append(aCase, that.aCase)
                .append(hit_disp_id, that.hit_disp_id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(hitId)
                .append(caseId)
                //.append(aCase)
                .append(hit_disp_id)
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

    public Set<HitsDispositionCommentsVo> getDispCommentsVo() {
        return dispCommentsVo;
    }

    public void setDispCommentsVo(Set<HitsDispositionCommentsVo> dispCommentsVo) {
        this.dispCommentsVo = dispCommentsVo;
    }

    public long getHit_disp_id() {
        return hit_disp_id;
    }

    public void setHit_disp_id(long hit_disp_id) {
        this.hit_disp_id = hit_disp_id;
    }

}
