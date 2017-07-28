/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.HashSet;

import java.util.Set;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "hits_disposition_comments")
public class HitsDispositionComments extends BaseEntityAudit {
    private static final long serialVersionUID = 1L;

    public HitsDispositionComments() { }

    @Column(name = "comments", length = 1000)
    private String comments;

    @Column(name="hit_disp_id")
    private long hitDispId;

    @Column(name="hit_id")
    private long hitId;

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public long getHitDispId() {
        return hitDispId;
    }

    public void setHitDispId(long hitDispId) {
        this.hitDispId = hitDispId;
    }

    public long getHitId() {
        return hitId;
    }

    public void setHitId(long hitId) {
        this.hitId = hitId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        HitsDispositionComments that = (HitsDispositionComments) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(hitDispId, that.hitDispId)
                .append(hitId, that.hitId)
                .append(id, that.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(hitDispId)
                .append(hitId)
                .append(id)
                .toHashCode();
    }
}
