/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.udr.json;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The base query condition term.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "type")
public class QueryTerm implements QueryEntity {
    private static final long serialVersionUID = 6558396573006515297L;
    
     private String entity;
     private String field;
     private String type;
     private String operator;
     private String[] value;
    private UUID uuid;

    // private String[] values;
     
     public QueryTerm(){
         
     }
     public QueryTerm(String entity, String field, String type, String op, String[] val){
         this.entity = entity;
         this.field = field;
         this.type = type;
         this.operator = op;
         this.value = val;
     }
     
    @Override
    public List<List<QueryTerm>> createFlattenedList() {
        final List<QueryTerm> mintermList = new LinkedList<QueryTerm>();
        mintermList.add(this);
        final List<List<QueryTerm>> ret = new LinkedList<List<QueryTerm>>();
        ret.add(mintermList);
        return ret;
    }
    
    /**
     * @return the entity
     */
    public String getEntity() {
        return entity;
    }
    /**
     * @param entity the entity to set
     */
    public void setEntity(String entity) {
        this.entity = entity;
    }

    /**
     * @return the field
     */
    public String getField() {
        return field;
    }
    /**
     * @param field the field to set
     */
    public void setField(String field) {
        this.field = field;
    }
        
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }
    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * @return the operator
     */
    public String getOperator() {
        return operator;
    }
    /**
     * @param operator the operator to set
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * @return the value
     */
    public String[] getValue() {
        return value;
    }
    /**
     * @param value the value to set
     */
    public void setValue(String[] value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

}
