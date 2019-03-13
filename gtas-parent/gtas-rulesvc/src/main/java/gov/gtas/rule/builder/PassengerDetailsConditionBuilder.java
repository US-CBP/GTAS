package gov.gtas.rule.builder;


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
}
