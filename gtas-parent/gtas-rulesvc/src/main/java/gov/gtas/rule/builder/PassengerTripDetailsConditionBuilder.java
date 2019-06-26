package gov.gtas.rule.builder;

public class PassengerTripDetailsConditionBuilder extends EntityConditionBuilder{

    PassengerTripDetailsConditionBuilder(final String drlVariableName) {
        super(drlVariableName, RuleTemplateConstants.PASSENGER_TRIP_DETAILS_NAME);
    }

    @Override
    protected void addSpecialConditions(StringBuilder bldr) {

    }

    String getPassengerIdLinkExpression() {
        return getDrlVariableName()+".paxId";
    }

}
