/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.gtas.enumtype.HitTypeEnum;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "pending_hit_detail")
public class PendingHitDetails extends BaseEntityAudit {

    public PendingHitDetails() {}

    public PendingHitDetails(HitTypeEnum hitEnum) {
        this.hitEnum = hitEnum;
        this.setHitType(hitEnum.toString());
    }

    @Column(name = "title", nullable = false)
    private String Title;

    @Column(name = "description")
    private String Description;

    @Column(name = "hit_type", nullable = false, length = 3)
    private String hitType;

    @Enumerated(EnumType.STRING)
    private HitTypeEnum hitEnum;

    // Binds directly to rule, watchlist item, or graph hit
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hm_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private HitMaker hitMaker;

    @JsonIgnore
    @Column(name = "hm_id", columnDefinition = "bigint unsigned", nullable = false)
    private Long hitMakerId;

    /**
     * String representation of matched conditions; it can be split into String[]
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "cond_text", columnDefinition = "TEXT NULL")
    private String ruleConditions;

    @Column(name = "created_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger", nullable = false, insertable = false, updatable = false, referencedColumnName = "id")
    protected Passenger passenger;

    @Column(name = "passenger", columnDefinition = "bigint unsigned")
    protected Long passengerId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight", insertable = false, updatable = false, referencedColumnName = "id")
    private Flight flight;

    @Column(name = "flight", columnDefinition = "bigint unsigned", nullable = false)
    private Long flightId;

    @Column(name = "percentage_match")
    private float percentage = 1; // 1 = 100%

    @Transient
    private UUID passengerGUID;

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getRuleConditions() {
        return ruleConditions;
    }

    public void setRuleConditions(String ruleConditions) {
        this.ruleConditions = ruleConditions;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getHitType() {
        return hitType;
    }

    public void setHitType(String hitType) {
        this.hitType = hitType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PendingHitDetails that = (PendingHitDetails) o;
        return Objects.equals(hitType, that.hitType) &&
                hitEnum == that.hitEnum &&
                Objects.equals(hitMakerId, that.hitMakerId) &&
                Objects.equals(passengerId, that.passengerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHitMakerId(), getPassengerId(), getHitEnum());
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public HitTypeEnum getHitEnum() {
        return hitEnum;
    }

    public void setHitEnum(HitTypeEnum hitEnum) {
        this.hitEnum = hitEnum;
    }

    public HitMaker getHitMaker() {
        return hitMaker;
    }

    public Long getHitMakerId() {
        return hitMakerId;
    }

    public void setHitMaker(HitMaker hitMaker) {
        this.hitMaker = hitMaker;
    }

    public void setHitMakerId(Long hitMakerId) {
        this.hitMakerId = hitMakerId;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public UUID getPassengerGUID() {
        return passengerGUID;
    }

    public void setPassengerGUID(UUID passengerGUID) {
        this.passengerGUID = passengerGUID;
    }
}
