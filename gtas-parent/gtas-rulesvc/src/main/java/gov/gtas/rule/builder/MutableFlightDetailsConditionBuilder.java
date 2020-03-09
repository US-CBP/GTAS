package gov.gtas.rule.builder;

import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.TypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

public class MutableFlightDetailsConditionBuilder extends EntityConditionBuilder {

	private static final Logger logger = LoggerFactory.getLogger(MutableFlightDetailsConditionBuilder.class);

	public MutableFlightDetailsConditionBuilder(final String drlVariableName) {
		super(drlVariableName, "MutableFlightDetails");
	}

	@Override
	public void reset() {
		super.reset();
	}

	// previous code here has been replaced by new code in generateLinkConditions()
	// in RuleConditionBuilder
	@Override
	protected void addSpecialConditions(StringBuilder bldr) {

	}

	public String getFlightIdLinkExpression() {
		return getDrlVariableName() + ".flightId";
	}

	@Override
	public void addCondition(final CriteriaOperatorEnum opCode, final String attributeName,
			final TypeEnum attributeType, String[] values) throws ParseException {
		if (this.isEmpty()) {
			this.addConditionAsString("flightId == $f.id");
		}
		super.addCondition(opCode, attributeName, attributeType, values);
	}
}
