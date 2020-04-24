package gov.gtas.rule.builder;

import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.TypeEnum;

import java.text.ParseException;

public class PassengerDetailsConditionBuilder extends EntityConditionBuilder {

	PassengerDetailsConditionBuilder(final String drlVariableName) {
		super(drlVariableName, RuleTemplateConstants.PASSENGER_DETAILS_NAME);
	}

	@Override
	protected void addSpecialConditions(StringBuilder bldr) {

	}

	String getPassengerIdLinkExpression() {
		return getDrlVariableName() + ".passengerId";
	}

	@Override
	public void addCondition(final CriteriaOperatorEnum opCode, final String attributeName,
			final TypeEnum attributeType, String[] values) throws ParseException {
		if (this.isEmpty()) {
			this.addConditionAsString("passengerId == $p.id");
		}
		super.addCondition(opCode, attributeName, attributeType, values);
	}

}
