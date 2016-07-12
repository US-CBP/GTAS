/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.udr;

import static gov.gtas.constant.DomainModelConstants.KB_UNIQUE_CONSTRAINT_NAME;
import gov.gtas.model.BaseEntity;

import java.util.Date;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * KnowledgeBase
 */
@Entity
@Table(name = "knowledge_base",
       uniqueConstraints= {@UniqueConstraint(name=KB_UNIQUE_CONSTRAINT_NAME, columnNames={"KB_NAME"})})
public class KnowledgeBase extends BaseEntity {
    private static final long serialVersionUID = 5027457099159173590L;
    
    @Version
    @Column(name = "VERSION")
    private long version;
    
    @Column(name = "KB_NAME", nullable=false, length = 20)
    private String kbName;
    
    @Lob @Basic(fetch=FetchType.EAGER)
    @Column(name="KB_BLOB", nullable=false, length=2000000)
    private byte[] kbBlob;
    
    @Lob @Basic(fetch=FetchType.EAGER)
    @Column(name="RL_BLOB", nullable=false, length=1000000)
    private byte[] rulesBlob;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATION_DT", nullable = false, length = 19)
    private Date creationDt;

    @OneToMany(mappedBy="knowledgeBase", fetch=FetchType.LAZY)
    private Set<Rule> rulesInKB;
    
    public KnowledgeBase(){
        
    }
    public KnowledgeBase(String kbName) {
        this.kbName = kbName;
    }

    public KnowledgeBase(long id, Date creationDt) {
        this.id = id;
        this.creationDt = creationDt;
    }

    public KnowledgeBase(long id, byte[] kbBlob, Date creationDt) {
        this.id = id;
        this.kbBlob = kbBlob;
        this.creationDt = creationDt;
    }

    public long getVersion() {
        return this.version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public byte[] getKbBlob() {
        return this.kbBlob;
    }

    public void setKbBlob(byte[] kbBlob) {
        this.kbBlob = kbBlob;
    }

    public Date getCreationDt() {
        return this.creationDt;
    }

    public void setCreationDt(Date creationDt) {
        this.creationDt = creationDt;
    }

    /**
     * @return the rulesBlob
     */
    public byte[] getRulesBlob() {
        return rulesBlob;
    }

    /**
     * @param rulesBlob the rulesBlob to set
     */
    public void setRulesBlob(byte[] rulesBlob) {
        this.rulesBlob = rulesBlob;
    }

    /**
     * @return the kbName
     */
    public String getKbName() {
        return kbName;
    }
    /**
     * @param kbName the kbName to set
     */
    public void setKbName(String kbName) {
        this.kbName = kbName;
    }
    
    /**
     * @return the rulesInKB
     */
    public Set<Rule> getRulesInKB() {
        return rulesInKB;
    }
    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
//      hashCodeBuilder.append(version);
        hashCodeBuilder.append(kbBlob);
        hashCodeBuilder.append(creationDt);
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
        if (!(obj instanceof KnowledgeBase)) {
            return false;
        }
        KnowledgeBase other = (KnowledgeBase) obj;
        EqualsBuilder equalsBuilder = new EqualsBuilder();
//      equalsBuilder.append(version, other.version);
        equalsBuilder.append(kbBlob, other.kbBlob);
        equalsBuilder.append(creationDt, other.creationDt);
        return equalsBuilder.isEquals();
    }

}
