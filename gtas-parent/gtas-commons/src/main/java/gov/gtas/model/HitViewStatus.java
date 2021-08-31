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
import gov.gtas.enumtype.LookoutStatusEnum;

import javax.persistence.*;

import java.util.Date;
import java.util.Objects;

@Entity
//hvs_query_index uses 
@Table(name = "hit_view_status", 
indexes = {
		@Index(name = "hvs_query_eta ", columnList = "full_utc_eta_timestamp"),
		@Index(name= "hvs_query_etd", columnList = "full_utc_etd_timestamp")
},
		uniqueConstraints = {
		@UniqueConstraint(columnNames = { "hv_hit_detail", "hv_user_group" }, name = "hvs_unique_constraint") })
public class HitViewStatus extends BaseEntityAudit {
	
	public HitViewStatus() {
		super();
	}

	public HitViewStatus(HitDetail hitDetail, UserGroup userGroup, HitViewStatusEnum hvse, Passenger passenger,
			LookoutStatusEnum lookoutStatusEnum, Date eta, Date etd, String direction) {
		this.hitDetail = hitDetail;
		this.userGroup = userGroup;
		this.hitViewStatusEnum = hvse;
		this.passenger = passenger;
		this.lookoutStatusEnum = lookoutStatusEnum;
		this.eta = eta;
		this.etd = etd;
		this.direction = direction;
		
	}

	public HitViewStatus(HitDetail hitDetail, UserGroup userGroup, HitViewStatusEnum hvse, Passenger passenger,
						 LookoutStatusEnum lookoutStatusEnum) {
		this.hitDetail = hitDetail;
		this.userGroup = userGroup;
		this.hitViewStatusEnum = hvse;
		this.passenger = passenger;
		this.lookoutStatusEnum = lookoutStatusEnum;
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

	@Enumerated(EnumType.STRING)
	@Column(name = "lookout_status", nullable = false)
	private LookoutStatusEnum lookoutStatusEnum;

	
	/*
	 * THE FOLLOWING FIELDS ARE DUPLICATED FOR QUERY PERFORMANCE.
	 *  
	 * From Mutable Flight Details:
	 * ETD - corresponds to the utc timestamp of the flight departure
	 * ETA - corresponds to the etc timestamp of the flight arrival
	 * 
	 * From Flight:
	 * Direction - corresponds to the direction in the flight table.
	 * 
	 * It's important to note that the etd and eta are *not* kept up todate. 
	 * As the table name suggest, these values can be mutated. There is a very 
	 * low risk that it will desync, although this is unlikely as these values
	 * rarely change.
	 * 
	 * The flight direction will never change as the 
	 * data in the flight table is immutable. 
	 * 
	 * 
	 * */
	@Column(name = "full_utc_etd_timestamp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date etd; // PLEASE SEE ABOVE COMMENT FOR MORE INFORMATION

	@Column(name = "full_utc_eta_timestamp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date eta; 	// PLEASE SEE ABOVE COMMENT FOR MORE INFORMATION
	
	@Column(length = 1, name = "direction")
	private String direction; // PLEASE SEE ABOVE COMMENT FOR MORE INFORMATION

	
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

	public LookoutStatusEnum getLookoutStatusEnum() { return lookoutStatusEnum; }

	public void setLookoutStatusEnum(LookoutStatusEnum lookoutStatusEnum) { this.lookoutStatusEnum = lookoutStatusEnum; }

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		HitViewStatus that = (HitViewStatus) o;
		return getHitDetail().equals(that.getHitDetail()) && getUserGroup().equals(that.getUserGroup());
	}
	
	/**
	 * @return the etd
	 */
	public Date getEtd() {
		return etd;
	}

	/**
	 * @param etd the etd to set
	 */
	public void setEtd(Date etd) {
		this.etd = etd;
	}

	/**
	 * @return the eta
	 */
	public Date getEta() {
		return eta;
	}

	/**
	 * @param eta the eta to set
	 */
	public void setEta(Date eta) {
		this.eta = eta;
	}

	/**
	 * @return the direction
	 */
	public String getDirection() {
		return direction;
	}

	/**
	 * @param direction the direction to set
	 */
	public void setDirection(String direction) {
		this.direction = direction;
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
