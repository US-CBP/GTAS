package gov.gtas.summary;

import java.util.Date;

public class PassengerPendingDetail {

    private String Title;

    private String Description;

    private String hitType;

    private String hitEnum;

    private Long hitMakerId;

    /**
     * String representation of matched conditions; it can be split into String[]
     */
    private String ruleConditions;

    private Date createdDate;

    protected Long passengerId;

    private Long flightId;

    private float percentage = 1; // 1 = 100%

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

    public String getHitEnum() {
        return hitEnum;
    }

    public void setHitEnum(String hitEnum) {
        this.hitEnum = hitEnum;
    }

    public Long getHitMakerId() {
        return hitMakerId;
    }

    public void setHitMakerId(Long hitMakerId) {
        this.hitMakerId = hitMakerId;
    }

    public String getRuleConditions() {
        return ruleConditions;
    }

    public void setRuleConditions(String ruleConditions) {
        this.ruleConditions = ruleConditions;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
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

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }
}
