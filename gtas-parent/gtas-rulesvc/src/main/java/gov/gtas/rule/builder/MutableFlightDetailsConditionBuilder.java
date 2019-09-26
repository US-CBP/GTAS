package gov.gtas.rule.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
