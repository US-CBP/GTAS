package gov.gtas.job.scheduler.summary;

import gov.gtas.model.HitDetail;
import org.springframework.beans.BeanUtils;

import java.util.Date;

public class PassengerDerog {

    private String Title;

    private String Description;

    private String hitType;

    private Long hitMakerId;

    private String ruleConditions;

    private Date createdDate;

    private Long ruleId;

    protected Long passengerId;

    private Long flightId;

    private float percentage = 1;

    public static PassengerDerog from(HitDetail hd) {
        PassengerDerog pd = new PassengerDerog();
        BeanUtils.copyProperties(hd, pd);
        return pd;
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

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
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
