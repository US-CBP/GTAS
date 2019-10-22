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
import java.util.Objects;

@Entity
@Table(name = "hit_view_status", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "hv_hit_detail", "hv_user_group" }, name = "hvs_unique_constraint") })
public class HitViewStatus extends BaseEntityAudit {

	HitViewStatus() {
	}

	public HitViewStatus(HitDetail hitDetail, UserGroup userGroup, HitViewStatusEnum hvse, Passenger passenger) {
		this.hitDetail = hitDetail;
		this.userGroup = userGroup;
		this.hitViewStatusEnum = hvse;
		this.passenger = passenger;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hv_passenger_id", referencedColumnName = "id", nullable = false)
	@JsonIgnore
	private Passenger passenger;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hv_hit_detail", referencedColumnName = "id", nullable = false)
	@JsonIgnore
	private HitDetail hitDetail;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hv_user_group", referencedColumnName = "id", nullable = false)
	@JsonIgnore
	private UserGroup userGroup;

	@Enumerated(EnumType.STRING)
	@Column(name = "hv_status", nullable = false)
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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		HitViewStatus that = (HitViewStatus) o;
		return getHitDetail().equals(that.getHitDetail()) && getUserGroup().equals(that.getUserGroup());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getHitDetail(), getUserGroup());
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}
}
