/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder;

import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.TypeEnum;

import java.text.ParseException;

public class SeatConditionBuilder extends EntityConditionBuilder {

	private boolean apis;

	public SeatConditionBuilder(final String drlVariableName) {
		super(drlVariableName, RuleTemplateConstants.SEAT_ENTITY_NAME);
		this.apis = apis;
	}

	public void addApisCondition(boolean isApis) {
		if (isApis) {
			super.addConditionAsString("apis == true");
		} else {
			super.addConditionAsString("apis == false");
		}
	}

	@Override
	protected void addSpecialConditions(StringBuilder bldr) {
	}

	/**
	 * @return the apis
	 */
	public boolean isApis() {
		return apis;
	}

	@Override
	public void addCondition(final CriteriaOperatorEnum opCode, final String attributeName,
			final TypeEnum attributeType, String[] values) throws ParseException {
		if (this.isEmpty()) {
			this.addConditionAsString("passengerId == $p.id");
			this.addConditionAsString("flightId == " + "$f.id");
		}
		super.addCondition(opCode, attributeName, attributeType, values);
	}

}
