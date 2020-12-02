/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.vo.passenger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FlightPaxVo {
  protected Long id;
  private String firstName;
  private String middleName;
  private String lastName;
  private String gender;
  private String nationality;
  private String passengerType;
  private Date dob;
  private String seat;
  private Boolean onRuleHitList = Boolean.FALSE;
  private Boolean onWatchList = Boolean.FALSE;
  private String coTravellerId;
  private Integer ruleHitCount;
  private Integer watchlistHitCount;
  private Integer graphHitCount;
  private Integer fuzzyHitCount;
  private Integer manualHitCount;
  private Integer externalHitCount;
  private Integer lowPrioHitCount;
  private Integer medPrioHitCount;
  private Integer highPrioHitCount;

  private List<String> documents = new ArrayList<>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void addDocument(DocumentVo d) {
    documents.add(d.getDocumentNumber());
  }

  public List<String> getDocuments() {
    return documents;
  }

  public String getSeat() {
    return seat;
  }

  public void setSeat(String seat) {
    this.seat = seat;
  }

  public String getMiddleName() {
    return middleName;
  }

  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getNationality() {
    return nationality;
  }

  public void setNationality(String nationality) {
    this.nationality = nationality;
  }

  public String getPassengerType() {
    return passengerType;
  }

  public void setPassengerType(String passengerType) {
    this.passengerType = passengerType;
  }

  public Date getDob() {
    return dob;
  }

  public void setDob(Date dob) {
    this.dob = dob;
  }

  public Boolean getOnRuleHitList() {
    return onRuleHitList;
  }

  public void setOnRuleHitList(Boolean onRuleHitList) {
    this.onRuleHitList = onRuleHitList;
  }

  public Boolean getOnWatchList() {
    return onWatchList;
  }

  public void setOnWatchList(Boolean onWatchList) {
    this.onWatchList = onWatchList;
  }

  public String getCoTravellerId() { return coTravellerId; }

  public void setCoTravellerId(String coTravellerId) { this.coTravellerId = coTravellerId; }

  public Integer getRuleHitCount() { return ruleHitCount; }

  public void setRuleHitCount(Integer ruleHitCount) { this.ruleHitCount = ruleHitCount; }

  public Integer getWatchlistHitCount() { return watchlistHitCount; }

  public void setWatchlistHitCount(Integer watchlistHitCount) { this.watchlistHitCount = watchlistHitCount; }

  public Integer getGraphHitCount() { return graphHitCount; }

  public void setGraphHitCount(Integer graphHitCount) { this.graphHitCount = graphHitCount; }

  public Integer getFuzzyHitCount() { return fuzzyHitCount; }

  public void setFuzzyHitCount(Integer fuzzyHitCount) { this.fuzzyHitCount = fuzzyHitCount; }

  public Integer getManualHitCount() { return manualHitCount; }

  public void setManualHitCount(Integer manualHitCount) { this.manualHitCount = manualHitCount; }

  public Integer getExternalHitCount() { return externalHitCount; }

  public void setExternalHitCount(Integer externalHitCount) { this.externalHitCount = externalHitCount; }

  public void setDocuments(List<String> documents) { this.documents = documents; }

  public Integer getLowPrioHitCount() { return lowPrioHitCount; }

  public void setLowPrioHitCount(Integer lowPrioHitCount) { this.lowPrioHitCount = lowPrioHitCount; }

  public Integer getMedPrioHitCount() { return medPrioHitCount; }

  public void setMedPrioHitCount(Integer medPrioHitCount) { this.medPrioHitCount = medPrioHitCount; }

  public Integer getHighPrioHitCount() { return highPrioHitCount; }

  public void setHighPrioHitCount(Integer highPrioHitCount) { this.highPrioHitCount = highPrioHitCount; }
}
