/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */

package gov.gtas.vo.passenger;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class HitsDispositionCommentsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    public HitsDispositionCommentsVo() { }

    private String comments;

    private long hitDispId;

    private long hitId;

    private Set<AttachmentVo> attachmentSet = new HashSet<AttachmentVo>();

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

    public Set<AttachmentVo> getAttachmentSet() {
        return attachmentSet;
    }

    public void setAttachmentSet(Set<AttachmentVo> attachmentSet) {
        this.attachmentSet = attachmentSet;
    }
}
