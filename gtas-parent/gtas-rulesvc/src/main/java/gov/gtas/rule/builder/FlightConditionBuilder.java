/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder;

import gov.gtas.enumtype.EntityEnum;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlightConditionBuilder extends EntityConditionBuilder {
    /*
     * The logger for the FlightConditionBuilder.
     */
    private static final Logger logger = LoggerFactory
            .getLogger(FlightConditionBuilder.class);
    
    private String defaultPassengerVariableName;
    private List<String> linkedPassengerList;
    public FlightConditionBuilder(final String drlVariableName, final String defaultPassengerVarName){
        super(drlVariableName, EntityEnum.FLIGHT.getEntityName());
        this.linkedPassengerList = new LinkedList<String>();
        this.defaultPassengerVariableName = defaultPassengerVarName;
    }
    public void addLinkedPassenger(final String passengerVariable){
        this.linkedPassengerList.add(passengerVariable);
    }
    
    @Override
    public void reset() {
        super.reset();
        linkedPassengerList.clear();
    }

    // previous code here has been replaced by new code in generateLinkConditions() in RuleConditionBuilder
    @Override
    protected void addSpecialConditions(StringBuilder bldr) {

    }
}
