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
    private Date flightETADate;
    private Date flightETDDate;
    private String flightDirection;
	private String lastName;
    private String firstName;
    private String middleName;
    private String nationality;
    private Date dob;
    private String document;
    private Long highPriorityRuleCatId;
    private String flightNumber;
    private String createDate; 
    private String status;
    private String description;
    private Set<HitsDisposition> hitsDispositions;
    private Set<HitsDispositionVo> hitsDispositionVos;
    private Set<GeneralCaseCommentVo> generalCaseCommentVos;
    private String caseOfficerStatus;
    private Boolean oneDayLookoutFlag;
    private Date currentTime;
    private String countDownTimeDisplay;
    private Date countdownTime;
    private String disposition;


    public String getCaseOfficerStatus() {
        return caseOfficerStatus;
    }

    public void setCaseOfficerStatus(String caseOfficerStatus) {
        this.caseOfficerStatus = caseOfficerStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
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

	public Boolean getOneDayLookoutFlag() {
		return oneDayLookoutFlag;
	}

	public void setOneDayLookoutFlag(Boolean oneDayLookoutFlag) {
		this.oneDayLookoutFlag = oneDayLookoutFlag;
	}

    public Date getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Date currentTime) {
        this.currentTime = currentTime;
    }

    public String getCountDownTimeDisplay() {
        return countDownTimeDisplay;
    }

    public void setCountDownTimeDisplay(String countDownTimeDisplay) {
        this.countDownTimeDisplay = countDownTimeDisplay;
    }

    public Date getCountdownTime() {
        return countdownTime;
    }

    public void setCountdownTime(Date countdownTime) {
        this.countdownTime = countdownTime;
    }

	public String getDisposition() {
		return disposition;
	}

	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}


    public Set<GeneralCaseCommentVo> getGeneralCaseCommentVos() {
        return generalCaseCommentVos;
    }

    public void setGeneralCaseCommentVos(Set<GeneralCaseCommentVo> generalCaseCommentVos) {
        this.generalCaseCommentVos = generalCaseCommentVos;
    }

}
