/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.passenger;

import gov.gtas.model.HitsDisposition;
import java.util.Set;

import java.util.Date;

public class CaseVo {
    private Long id;
    private Long passengerId;
    private String paxName;
    private String paxType;
    private Long paxId;
    private Long flightId;
    private String hitType;
    private Date flightEta;
    private Date flightEtd;
    private String flightDirection;
	private String lastName;
    private String firstName;
    private String middleName;
    private String citizenshipCountry;
    private Date dob;
    private String document;
    private Long highPriorityRuleCatId;
    private String flightNumber;
    private String createDate; 
    private String status;
    private String description;
    private Set<HitsDisposition> hitsDispositions;
    private Set<HitsDispositionVo> hitsDispositionVos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCitizenshipCountry() {
        return citizenshipCountry;
    }

    public void setCitizenshipCountry(String citizenshipCountry) {
        this.citizenshipCountry = citizenshipCountry;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public Set<HitsDispositionVo> getHitsDispositionVos() {
        return hitsDispositionVos;
    }

    public void setHitsDispositionVos(Set<HitsDispositionVo> hitsDispositionVos) {
        this.hitsDispositionVos = hitsDispositionVos;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getHitType() {
		return hitType;
	}
	public void setHitType(String hitType) {
		this.hitType = hitType;
	}
	public Date getFlightEta() {
		return flightEta;
	}
	public void setFlightEta(Date flightEta) {
		this.flightEta = flightEta;
	}
	public Date getFlightEtd() {
		return flightEtd;
	}
	public void setFlightEtd(Date flightEtd) {
		this.flightEtd = flightEtd;
	}
	public Long getPassengerId() {
        return passengerId;
    }
    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }
    public Long getFlightId() {
        return flightId;
    }
    public void setFlightId(Long flightId) {
        this.flightId = flightId;
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
    public String getMiddleName() {
        return middleName;
    }
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    public String getFlightNumber() {
        return flightNumber;
    }
    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
    public String getCreateDate() {
        return createDate;
    }
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
    public String getStatus() {
        return status;
    }

    public String getPaxName() {
        return paxName;
    }

    public void setPaxName(String paxName) {
        this.paxName = paxName;
    }

    public String getPaxType() {
        return paxType;
    }

    public void setPaxType(String paxType) {
        this.paxType = paxType;
    }

    public String getFlightDirection() {
		return flightDirection;
	}

    public Long getPaxId() {
        return paxId;
    }

    public void setPaxId(Long paxId) {
        this.paxId = paxId;
    }

    public Long getHighPriorityRuleCatId() {
        return highPriorityRuleCatId;
    }

    public void setHighPriorityRuleCatId(Long highPriorityRuleCatId) {
        this.highPriorityRuleCatId = highPriorityRuleCatId;
    }

    public void setFlightDirection(String flightDirection) {
        this.flightDirection = flightDirection;
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

    public Set<HitsDisposition> getHitsDispositions() {
        return hitsDispositions;
    }

    public void setHitsDispositions(Set<HitsDisposition> hitsDispositions) {
        this.hitsDispositions = hitsDispositions;
    }
}
