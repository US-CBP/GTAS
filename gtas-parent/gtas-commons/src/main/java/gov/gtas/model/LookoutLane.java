/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import gov.gtas.enumtype.LookoutStatusEnum;

import javax.persistence.*;

@Entity
@Table(name="lookout_lane")
public class LookoutLane extends BaseEntityAudit{
    private static final long serialVersionUID = 1L;

    public LookoutLane(){

    }
    @Column(name = "display_name")
    private String displayName;

    @Column(name= "ord")
    private int ord;

    @Enumerated(EnumType.STRING)
    @Column(name = "lookout_status" )
    private LookoutStatusEnum status;

    @Column(name = "archived")
    private boolean archived;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getOrd() { return ord; }

    public void setOrd(int ord) { this.ord = ord; }

    public LookoutStatusEnum getStatus() {
        return status;
    }

    public void setStatus(LookoutStatusEnum status) {
        this.status = status;
    }

    public boolean isArchived() { return archived; }

    public void setArchived(boolean archived) { this.archived = archived; }
}
