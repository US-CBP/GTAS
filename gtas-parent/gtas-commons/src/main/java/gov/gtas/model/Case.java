/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gov.gtas.enumtype.EncounteredStatusEnum;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cases")
public class Case extends BaseEntityAudit {
    private static final long serialVersionUID = 1L;

    public Case() { }

    @Column(name = "flightId", nullable = false, columnDefinition = "bigint unsigned")
    private Long flightId;

    @Column(name = "paxId", nullable = false)
    private Long paxId;

    @Column(name = "eta_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date flightETADate;

    @Column(name = "etd_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date flightETDDate;

    @Column(name = "passengerName", nullable = true)
    private String paxName;

    @Column(name = "dob", nullable = true)
    private Date dob;

    @Column(name = "citizenshipCountry", nullable = true)
    private String citizenshipCountry;

    @Column(name = "passengerType", nullable = true)
    private String paxType;

    @Column(name = "document", nullable = true)
    private String document;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "highPriorityRuleCatId", nullable = false)
    private Long highPriorityRuleCatId = new Long(1L);

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, orphanRemoval=true)
    @JoinColumn(name="case_id", nullable = false)
    private Set<HitsDisposition> hitsDispositions = new HashSet<>();

    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "cc_id")
    private Set<CaseComment> caseComments = new HashSet<>();

    @Column(name = "case_officer_status")
    private String caseOfficerStatus;


    @Transient
    @Temporal(TemporalType.TIMESTAMP)
    private Date countdown;

    public void addHitsDisposition(HitsDisposition _tempHit){
        hitsDispositions.add(_tempHit);
        //_tempHit.setaCase(this);
    }

    @Column(name = "flightNumber")
    private String flightNumber;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "firstName")
    private String firstName;
    
    @Column(name = "one_day_lookout_flag")
    private Boolean oneDayLookoutFlag;
    
    @Column(name = "disposition", nullable = true)
    private String disposition;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "flightId",insertable=false, updatable=false, referencedColumnName = "id")
    private Flight flight;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "encountered_status", nullable=false)
    private EncounteredStatusEnum encounteredStatus = EncounteredStatusEnum.NOT_ENCOUNTERED;//The default value for encountered status

 
	public EncounteredStatusEnum getEncounteredStatus() {
		return encounteredStatus;
	}


	public void setEncounteredStatus(EncounteredStatusEnum encounteredStatus) {
		this.encounteredStatus = encounteredStatus;
	}



	public Set<HitsDisposition> getHitsDispositions() {
        return hitsDispositions;
    }



    public String getCaseOfficerStatus() {
        return caseOfficerStatus;
    }

    public void setCaseOfficerStatus(String caseOfficerStatus) {
        this.caseOfficerStatus = caseOfficerStatus;
    }



    public Set<CaseComment> getCaseComments() {
        return caseComments;
    }

    public void setCaseComments(Set<CaseComment> caseComments) {
        this.caseComments = caseComments;
    }

    public void setHitsDispositions(Set<HitsDisposition> hitsDispositions) {
        this.hitsDispositions = hitsDispositions;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getCitizenshipCountry() {
        return citizenshipCountry;
    }

    public void setCitizenshipCountry(String citizenshipCountry) {
        this.citizenshipCountry = citizenshipCountry;
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

    public Date getFlightETADate() {
        return flightETADate;
    }

    public void setFlightETADate(Date flightETADate) {
        this.flightETADate = flightETADate;
    }

    public Date getFlightETDDate() {
        return flightETDDate;
    }

    public void setFlightETDDate(Date flightETDDate) {
        this.flightETDDate = flightETDDate;
    }

    public void setPaxId(Long paxId) {

        this.paxId = paxId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPaxName() {
        return paxName;
    }

    public String getPassengerName(){return paxName;}

    public void setPaxName(String paxName) {
        this.paxName = paxName;
    }

    public String getPaxType() {
        return paxType;
    }

    public void setPaxType(String paxType) {
        this.paxType = paxType;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public Long getHighPriorityRuleCatId() {
        return highPriorityRuleCatId;
    }

    public void setHighPriorityRuleCatId(Long highPriorityRuleCatId) {
        this.highPriorityRuleCatId = highPriorityRuleCatId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }



    public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}

    public Date getCountdown() {
        return countdown;
    }

    public void setCountdown(Date countdown) {
        this.countdown = countdown;
    }

	public Boolean getOneDayLookoutFlag() {
		return oneDayLookoutFlag;
	}

	public void setOneDayLookoutFlag(Boolean oneDayLookoutFlag) {
		this.oneDayLookoutFlag = oneDayLookoutFlag;
	}

	
	
	public String getDisposition() {
		return disposition;
	}

	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Case aCase = (Case) o;

        return new EqualsBuilder()
                .append(flightId, aCase.flightId)
                .append(paxId, aCase.paxId)
                .append(status, aCase.status)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(flightId)
                .append(paxId)
                .append(status)
                .toHashCode();
    }

}
