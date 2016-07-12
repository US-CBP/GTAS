/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;
import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@MappedSuperclass  
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;  
  
    @Id  
    @GeneratedValue(strategy = GenerationType.AUTO)  
    @Basic(optional = false)  
    @Column(name = "id", nullable = false, columnDefinition = "bigint unsigned")  
    protected Long id;  
  
    public Long getId() {  
        return id;  
    }
    
    /**
     * This protected setter is provided for derived classes.<br>
     * (Example of usage is to set up one to one mappings with identical keys.)
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    @Override  
    public int hashCode() {  
        int hash = 0;  
        hash += (this.getId() != null ? this.getId().hashCode() : 0);  
  
        return hash;  
    }  
  
    @Override  
    public boolean equals(Object object) {  
    if (this == object)  
            return true;  
        if (object == null)  
            return false;  
        if (getClass() != object.getClass())  
            return false;  
  
        BaseEntity other = (BaseEntity) object;  
        if (this.getId() != other.getId() && (this.getId() == null || !this.id.equals(other.id))) {  
            return false;  
        }  
        return true;  
    }  

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE); 
    }        
}  