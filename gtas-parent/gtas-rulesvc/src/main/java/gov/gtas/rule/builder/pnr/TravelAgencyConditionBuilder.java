/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder.pnr;

import static gov.gtas.rule.builder.RuleTemplateConstants.LINK_VARIABLE_SUFFIX;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.rule.builder.EntityConditionBuilder;

public class TravelAgencyConditionBuilder extends EntityConditionBuilder {

    public TravelAgencyConditionBuilder(final String drlVariableName) {
        super(drlVariableName, EntityEnum.TRAVEL_AGENCY.getEntityName());
    }

    @Override
    protected void addSpecialConditions(StringBuilder bldr) {
    }
    
    public String getLinkVariableName(){
        return getDrlVariableName() + LINK_VARIABLE_SUFFIX;
    }
}
