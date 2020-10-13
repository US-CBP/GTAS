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
  private Long coTravellerId;

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

  public Long getCoTravellerId() { return coTravellerId; }

  public void setCoTravellerId(Long coTravellerId) { this.coTravellerId = coTravellerId; }
}
