/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.*;

import javax.persistence.*;

@Entity
@Table(name = "hits_summary")
public class HitsSummary extends BaseEntity {
    private static final long serialVersionUID = 3436310987156511552L;

    public HitsSummary() {
    }

    @Column(name = "created_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HitDetail> hitdetails = new HashSet<>();

    @Column(name = "flight_id", columnDefinition = "bigint unsigned")
    private Long flightId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", referencedColumnName = "id", updatable = false, insertable = false)
    private Flight flight;

    @Column(name = "passenger_id", columnDefinition = "bigint unsigned")
    private Long paxId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", referencedColumnName = "id", updatable = false, insertable = false)
    private Passenger passenger;

    @Column(name = "rule_hit_count")
    private Integer ruleHitCount = 0;

    @Column(name = "hit_type")
    private String hitType;

    @Column(name = "wl_hit_count")
    private Integer watchListHitCount = 0;

    @Column(name = "graph_hit_count")
    private Integer graphHitCount = 0;

    @Transient
    private Boolean saveHits = false;

    public Boolean getSaveHits() {
        return saveHits;
    }

    public void setSaveHits(Boolean saveHits) {
        this.saveHits = saveHits;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Set<HitDetail> getHitdetails() {
        return hitdetails;
    }

    public void setHitdetails(Set<HitDetail> hitdetails) {
        this.hitdetails = hitdetails;
    }

    public String getHitType() {
        return hitType;
    }

    public void setHitType(String hitType) {
        this.hitType = hitType;
    }

    public Integer getRuleHitCount() {
        return ruleHitCount;
    }

    public void setRuleHitCount(Integer ruleHitCount) {
        this.ruleHitCount = ruleHitCount;
    }

    public Integer getWatchListHitCount() {
        return watchListHitCount;
    }

    public void setWatchListHitCount(Integer watchListHitCount) {
        this.watchListHitCount = watchListHitCount;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Long getPaxId() {
        return paxId;
    }

    public void setPaxId(Long paxId) {
        this.paxId = paxId;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Integer getGraphHitCount() {
        return graphHitCount;
    }

    public void setGraphHitCount(Integer graphHitCount) {
        this.graphHitCount = graphHitCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HitsSummary)) return false;
        HitsSummary that = (HitsSummary) o;
        return getFlightId().equals(that.getFlightId()) &&
                getPaxId().equals(that.getPaxId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFlightId(), getPaxId());
    }
}
