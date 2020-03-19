/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder.pnr;

import static gov.gtas.rule.builder.RuleTemplateConstants.LINK_VARIABLE_SUFFIX;

import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.TypeEnum;
import gov.gtas.querybuilder.mappings.BagMapping;
import gov.gtas.rule.builder.EntityConditionBuilder;
import gov.gtas.rule.builder.RuleTemplateConstants;

import java.text.ParseException;

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

	public void addCondition(final CriteriaOperatorEnum opCode, final String attributeName,
			final TypeEnum attributeType, String[] values) throws ParseException {
		if (this.isEmpty()) {
			addPassenger();
			addFlight();
		}
		super.addCondition(opCode, attributeName, attributeType, values);
	}

	public void addPassenger() {
		super.addConditionAsString("passengerId == " + RuleTemplateConstants.PASSENGER_VARIABLE_NAME + ".id");
	}

	public void addFlight() {
		super.addConditionAsString("flightId == $f.id");
	}
}
