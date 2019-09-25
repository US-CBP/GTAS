/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder.pnr;

import static gov.gtas.rule.builder.RuleTemplateConstants.LINK_VARIABLE_SUFFIX;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.rule.builder.EntityConditionBuilder;

public class EmailConditionBuilder extends EntityConditionBuilder {

	public EmailConditionBuilder(final String drlVariableName) {
		super(drlVariableName, EntityEnum.EMAIL.getEntityName());
	}

	@Override
	protected void addSpecialConditions(StringBuilder bldr) {
	}

	public String getLinkVariableName() {
		return getDrlVariableName() + LINK_VARIABLE_SUFFIX;
	}
}
