/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.udr;

import gov.gtas.enumtype.YesNoEnum;
import gov.gtas.util.DateCalendarUtils;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * RuleMeta
 */
@Entity
@Table(name = "rule_meta")
public class RuleMeta implements Serializable {
    /**
     * serial version UID
     */
    private static final long serialVersionUID = 384462394390643572L;
    
    @Id
    @Column(name="ID")
    private Long id;
    
    @OneToOne
    @JoinColumn(name="ID", referencedColumnName="ID", insertable=false, updatable=false)
    private UdrRule parent;
    
    @Column(name = "TITLE", nullable=false, length = 20)
    private String title;
    
    @Column(name = "DESCRIPTION", length = 1024)
    private String description;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_DT", nullable = false, length = 19)
    private Date startDt;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_DT", length = 19)
    private Date endDt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "ENABLE_FLAG", nullable = false, length = 1) 
    private YesNoEnum enabled;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "HIGH_PRIORITY_FLAG", nullable = false, length = 1)  
    private YesNoEnum priorityHigh;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "HIT_SHARE_FLAG", nullable = false, length = 1)  
    private YesNoEnum hitSharing;
    
    /**
     * Constructor for JPA.
     */
    public RuleMeta() {
        super();
        this.priorityHigh = YesNoEnum.N;
        this.hitSharing = YesNoEnum.N;
    }

    public RuleMeta(Date startDt, Date endDt) {
        this();
        this.startDt = startDt;
        this.endDt = endDt;
    }

    public RuleMeta(UdrRule parentRule, String title, String description, Date startDt,
            Date endDt, YesNoEnum enabled, YesNoEnum priorityHigh,
            YesNoEnum hitSharing) {
        this.id = parentRule.getId();
        this.title = title;
        this.description = description;     
        this.startDt = startDt;     
        this.endDt = endDt;
        this.enabled = enabled;
        this.priorityHigh = priorityHigh;
        this.hitSharing = hitSharing;
    }


    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the enabled
     */
    public YesNoEnum getEnabled() {
        return enabled;
    }

    /**
     * @return the parent
     */
    public UdrRule getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(UdrRule parent) {
        this.parent = parent;
        if(parent != null && parent.getId() != null){
           this.id = parent.getId();
        }
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(YesNoEnum enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the priorityHigh
     */
    public YesNoEnum getPriorityHigh() {
        return priorityHigh;
    }

    /**
     * @param priorityHigh the priorityHigh to set
     */
    public void setPriorityHigh(YesNoEnum priorityHigh) {
        this.priorityHigh = priorityHigh;
    }

    /**
     * @return the hitSharing
     */
    public YesNoEnum getHitSharing() {
        return hitSharing;
    }

    /**
     * @param hitSharing the hitSharing to set
     */
    public void setHitSharing(YesNoEnum hitSharing) {
        this.hitSharing = hitSharing;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDt() {
        return this.startDt;
    }

    public void setStartDt(Date startDt) {
        this.startDt = startDt;
    }

    public Date getEndDt() {
        return this.endDt;
    }

    public void setEndDt(Date endDt) {
        this.endDt = endDt;
    }


    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(title);
        hashCodeBuilder.append(description);
        hashCodeBuilder.append(startDt);
        hashCodeBuilder.append(endDt);
        hashCodeBuilder.append(enabled);
        hashCodeBuilder.append(priorityHigh);
        hashCodeBuilder.append(hitSharing);
        return hashCodeBuilder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RuleMeta)) {
            return false;
        }
        RuleMeta other = (RuleMeta) obj;
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        equalsBuilder.append(title, other.title);
        equalsBuilder.append(description, other.description);
        
        //start and end date equality up to seconds
        if(!DateCalendarUtils.dateRoundedEquals(startDt,  other.startDt)
            || !DateCalendarUtils.dateRoundedEquals(endDt,  other.endDt)    ){
            return false;
        }
        
        equalsBuilder.append(enabled, other.enabled);
        equalsBuilder.append(priorityHigh, other.priorityHigh);
        equalsBuilder.append(hitSharing, other.hitSharing);
        return equalsBuilder.isEquals();
    }
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE); 
    }        

}
