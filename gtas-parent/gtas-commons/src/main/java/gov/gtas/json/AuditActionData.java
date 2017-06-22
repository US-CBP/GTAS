/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.json;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
/**
 * JSON object class to convey audit log details.
 */
public class AuditActionData implements Serializable {
    private static final long serialVersionUID = -4390502518498037033L;

    private List<ActionProperty> actionProperties;
    public AuditActionData(){
        this.actionProperties = new LinkedList<ActionProperty>();
    }
    public AuditActionData(List<ActionProperty> actionProperties){
        this.actionProperties = actionProperties;
    }
    public void addProperty(final String pname, final String pvalue){
        actionProperties.add(new ActionProperty(pname, pvalue));
    }

    /**
     * @return the actionProperties
     */
    public List<ActionProperty> getActionProperties() {
        return Collections.unmodifiableList(actionProperties);
    }
    
    /**
     * @param actionProperties the actionProperties to set
     */
    public void setActionProperties(List<ActionProperty> actionProperties) {
        this.actionProperties = actionProperties;
    }

    @Override
    public String toString() {
        StringBuilder bldr = new StringBuilder("[");
        for(ActionProperty prop: this.actionProperties){
            bldr.append("{\"name\":\"").append(prop.getName()).append("\",")
            .append("\"value\":\"").append(prop.getValue()).append("\"},");
        }
        if(this.actionProperties.size() > 0){
            //remove the last comma
            bldr.deleteCharAt(bldr.length()-1);
        }
        bldr.append("]");
        return bldr.toString();
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    public static class ActionProperty{
        private String name;
        private String value;
        public ActionProperty(String propName, String propValue){
            name = propName;
            value = propValue;
        }
        /**
         * @return the name
         */
        public String getName() {
            return name;
        }
        /**
         * @return the value
         */
        public String getValue() {
            return value;
        }
        
    }

}
