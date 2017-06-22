/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder.pnr;

import gov.gtas.enumtype.EntityEnum;
import gov.gtas.rule.builder.EntityConditionBuilder;

public class PnrConditionBuilder extends EntityConditionBuilder {

    public PnrConditionBuilder(final String drlVariableName) {
        super(drlVariableName, EntityEnum.PNR.getEntityName());
    }

    @Override
    protected void addSpecialConditions(StringBuilder bldr) {
    }
}
