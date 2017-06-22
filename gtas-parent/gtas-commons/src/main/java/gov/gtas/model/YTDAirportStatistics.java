/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "ytd_airport_stats")
public class YTDAirportStatistics {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Basic(optional = false)
    @Column(name = "ID", nullable = false, columnDefinition = "bigint unsigned")
    private Long id;

    @Column(name = "AIRPORT", nullable = false)
    private String airport;

    @Column(name = "FLIGHTS", nullable = false)
    private Long flights;

    @Column(name = "RULEHITS", nullable = false)
    private Long ruleHits;

    @Column(name = "WATCHLISTHITS", nullable = false)
    private Long watchListHits;

    public YTDAirportStatistics() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAirport() {
        return airport;
    }

    public void setAirport(String airport) {
        this.airport = airport;
    }

    public Long getFlights() {
        return flights;
    }

    public void setFlights(Long flights) {
        this.flights = flights;
    }

    public Long getRuleHits() {
        return ruleHits;
    }

    public void setRuleHits(Long ruleHits) {
        this.ruleHits = ruleHits;
    }

    public Long getWatchListHits() {
        return watchListHits;
    }

    public void setWatchListHits(Long watchListHits) {
        this.watchListHits = watchListHits;
    }
}
