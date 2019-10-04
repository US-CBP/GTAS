/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.gtas.enumtype.HitViewStatusEnum;

import javax.persistence.*;

@Entity
@Table(name="hit_view_status")
public class HitViewStatus extends BaseEntityAudit {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hv_hit_detail", referencedColumnName = "id")
    @JsonIgnore
    private HitDetail hitDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hv_user_group", referencedColumnName = "id")
    @JsonIgnore
    private UserGroup userGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "hv_status")
    private HitViewStatusEnum hitViewStatusEnum;

    public HitDetail getHitDetail() {
        return hitDetail;
    }

    public void setHitDetail(HitDetail hitDetail) {
        this.hitDetail = hitDetail;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    public HitViewStatusEnum getHitViewStatusEnum() {
        return hitViewStatusEnum;
    }

    public void setHitViewStatusEnum(HitViewStatusEnum hitViewStatusEnum) {
        this.hitViewStatusEnum = hitViewStatusEnum;
    }

}
