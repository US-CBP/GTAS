package gov.gtas.email.dto;

import gov.gtas.enumtype.HitViewStatusEnum;

import java.util.Date;

public class HitEmailDto {

    private HitViewStatusEnum hitStatus;
    private String firstName;
    private String lastName;
    private String flightNumber;
    private Date dob;
    private String gender;
    private String documentType;
    private String documentNumber;
    private String severity;
    private String category;
    private String rule;
    private Date timeRemaining;


    public HitViewStatusEnum getHitStatus() {
        return hitStatus;
    }

    public void setHitStatus(HitViewStatusEnum hitStatus) {
        this.hitStatus = hitStatus;
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

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getDescription() {
        return severity + " | " + category + " | " + rule;
    }

    public Date getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(Date timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

}