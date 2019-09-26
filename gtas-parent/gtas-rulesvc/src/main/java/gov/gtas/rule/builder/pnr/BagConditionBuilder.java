/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder.pnr;

import static gov.gtas.rule.builder.RuleTemplateConstants.LINK_VARIABLE_SUFFIX;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.querybuilder.mappings.BagMapping;
import gov.gtas.rule.builder.EntityConditionBuilder;

public class BagConditionBuilder extends EntityConditionBuilder {

	public BagConditionBuilder(final String drlVariableName) {
		super(drlVariableName, EntityEnum.BAG.getEntityName());
	}

	@Override
	protected void addSpecialConditions(StringBuilder bldr) {
	}

	public String getLinkVariableName() {
		return getDrlVariableName() + LINK_VARIABLE_SUFFIX;
	}

	public String getPassengerIdLinkExpression() {
		return getDrlVariableName() + "." + BagMapping.BAG_PAX_OWNER_ID.getFieldName();
	}

	public String getFlightIdLinkExpression() {
		return getDrlVariableName() + "." + BagMapping.BAG_FLIGHT_OWNER_ID.getFieldName();
	}
}
