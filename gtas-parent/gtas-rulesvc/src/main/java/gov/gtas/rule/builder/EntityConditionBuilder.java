/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder;

import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.TypeEnum;
import gov.gtas.util.DateCalendarUtils;

import java.text.ParseException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Common functionality for building Rule criteria for entities such as
 * Passenger, Flight and Document.
 */
public abstract class EntityConditionBuilder {
    /*
     * list of criteria for the entity that will be combined to create an LHS
     * term for the rule. Normally the criteria will be separated by ','
     * signifying an AND connector.
     */
    private List<String> conditionList;
    /*
     * when one or more criteria contain a connector such as '||' then the comma
     * connector cannot be used. In that case andConnectorIsComma is false, and
     * '&&' is used as connector. (an example is the NOT_BETWEEN operator, which
     * contains a '||' connector.)
     */
    private boolean andConnectorIsComma;
    /*
     * The entity Java class name used to form the Drools LHS terms.
     */
    private String entityClassName;
    /*
     * The binding variable name for this entity.
     */
    private String drlVariableName;

    private Set<String> attributesWithNotEmptyCondition;

    /*
     * If this flag is true then an empty DRL condition is generated
     * even if the condition list is empty.
     * For example: $p:Passenger()
     */
    private boolean generateEmptyClause;
    /**
     * The protected constructor to be invoked by derived classes.
     * 
     * @param varName
     *            the binding variable name.
     * @param entityClassName
     *            the entity class name without the package part of the name.
     */
    protected EntityConditionBuilder(final String varName,
            final String entityClassName) {
        this.entityClassName = entityClassName;
        this.drlVariableName = varName;
        this.andConnectorIsComma = true;
        this.conditionList = new LinkedList<String>();
        attributesWithNotEmptyCondition = new HashSet<String>();
    }

    /**
     * This builder can be reused after calling reset.
     */
    public void reset() {
        this.andConnectorIsComma = true;
        this.conditionList.clear();
        attributesWithNotEmptyCondition.clear();
    }

    /**
     * Checks whether criteria have been added.
     * 
     * @return true if no criteria have been added.
     */
    public boolean isEmpty() {
        return conditionList.isEmpty();
    }

    public String build() {
        final StringBuilder bldr = new StringBuilder();
        if (!conditionList.isEmpty()) {
            bldr.append(drlVariableName).append(":").append(entityClassName)
                    .append("(");
            if (andConnectorIsComma) {
                bldr.append(String.join(", ", conditionList));
            } else {
                bldr.append(String.join(" && ", conditionList));
            }
            bldr.append(")\n");
            addSpecialConditions(bldr);
        } else if(generateEmptyClause){
            bldr.append(drlVariableName).append(":").append(entityClassName)
            .append("()\n");
        }
        return bldr.toString();
    }

    protected abstract void addSpecialConditions(final StringBuilder bldr);

    public void addCondition(final CriteriaOperatorEnum opCode,
            final String attributeName, final TypeEnum attributeType,
            String[] values) throws ParseException {
        final StringBuilder bldr = new StringBuilder();
        switch (opCode) {
        case EQUAL:
            if(attributeType == TypeEnum.DATETIME){
                //special 'between-like' treatment for date-time equality check
                //since Drools only supports date comparisons only
                bldr.append(attributeName).append(" >= ");
                RuleConditionBuilderHelper.addConditionValue(TypeEnum.DATE,
                        values[0], bldr);
                bldr.append(", ").append(attributeName).append(" < ");
                RuleConditionBuilderHelper.addConditionValue(TypeEnum.DATE,
                        DateCalendarUtils.addOneDayToJsondateString(values[0]), bldr);
                break;              
            }
        case NOT_EQUAL:
            if(attributeType == TypeEnum.DATETIME){
                //special 'between-like' treatment for date-time inequality check
                //since Drools only supports date comparisons only
                bldr.append("(").append(attributeName).append(" < ");
                RuleConditionBuilderHelper.addConditionValue(attributeType,
                        values[0], bldr);
                bldr.append(" || ").append(attributeName).append(" >= ");
                RuleConditionBuilderHelper.addConditionValue(attributeType,
                        DateCalendarUtils.addOneDayToJsondateString(values[0]), bldr);
                bldr.append(")");
                // convert all commas to &&
                andConnectorIsComma = false;
                break;              
            }
        case GREATER:
        case GREATER_OR_EQUAL:
        case LESS:
        case LESS_OR_EQUAL:
            bldr.append(attributeName).append(" ")
                    .append(opCode.getOperatorString()).append(" ");
            //TODO - if email strings are not to be made upper case
            //then handle here
            RuleConditionBuilderHelper.addConditionValue(attributeType,
                    values[0], bldr);
            break;
        case IN:
        case NOT_IN:
            bldr.append(attributeName).append(" ")
                    .append(opCode.getOperatorString()).append(" ");
            RuleConditionBuilderHelper.addConditionValues(attributeType,
                    values, bldr);
            break;
        case IS_EMPTY:
        case IS_NULL:
        case IS_NOT_NULL:
            // no values!
            bldr.append(attributeName).append(" ")
                    .append(opCode.getOperatorString()).append(" ");
            break;
        case IS_NOT_EMPTY:
            addNotEmptyCondition(bldr, attributeName, " ");
            break;
        case BEGINS_WITH:
        case ENDS_WITH:
            addNotEmptyCondition(bldr, attributeName, ", ");
            bldr.append(attributeName).append(" ")
                    .append(opCode.getOperatorString()).append(" ");
            RuleConditionBuilderHelper.addConditionValue(attributeType,
                    values[0], bldr);
            break;
        case CONTAINS:
            addNotEmptyCondition(bldr, attributeName, ", ");
            bldr.append(attributeName).append(" ")
                    .append(opCode.getOperatorString()).append(" ");
            RuleConditionBuilderHelper.addConditionValue(attributeType,
                    values[0], bldr, true, true);// begins and ends with
                                                    // wildcard
            break;
        case NOT_BEGINS_WITH:
            addNotEmptyCondition(bldr, attributeName, ", ");
            bldr.append(attributeName).append(" ")
                    .append(opCode.getOperatorString()).append(" ");
            RuleConditionBuilderHelper.addConditionValue(attributeType,
                    values[0], bldr, false, true);// begins and ends with
                                                    // wildcard
            break;
        case NOT_ENDS_WITH:
            addNotEmptyCondition(bldr, attributeName, ", ");
            bldr.append(attributeName).append(" ")
                    .append(opCode.getOperatorString()).append(" ");
            RuleConditionBuilderHelper.addConditionValue(attributeType,
                    values[0], bldr, true, false);// begins and ends with
                                                    // wildcard
            break;
        case NOT_CONTAINS:
            addNotEmptyCondition(bldr, attributeName, ", ");
            bldr.append(attributeName).append(" ")
                    .append(opCode.getOperatorString()).append(" ");
            RuleConditionBuilderHelper.addConditionValue(attributeType,
                    values[0], bldr, true, true);// begins and ends with
                                                    // wildcard
            break;
        case BETWEEN:
            bldr.append(attributeName).append(" >= ");
            RuleConditionBuilderHelper.addConditionValue(attributeType,
                    values[0], bldr);
            bldr.append(", ").append(attributeName).append(" <= ");
            RuleConditionBuilderHelper.addConditionValue(attributeType,
                    values[1], bldr);
            break;
        case NOT_BETWEEN:
            bldr.append("(").append(attributeName).append(" < ");
            RuleConditionBuilderHelper.addConditionValue(attributeType,
                    values[0], bldr);
            bldr.append(" || ").append(attributeName).append(" > ");
            RuleConditionBuilderHelper.addConditionValue(attributeType,
                    values[1], bldr);
            bldr.append(")");
            // convert all commas to &&
            andConnectorIsComma = false;
            break;
        case MEMBER_OF:
            bldr.append(attributeName).append(" memberOf ");
            RuleConditionBuilderHelper.addConditionValue(attributeType,
                    values[0], bldr);
            break;
        case NOT_MEMBER_OF:
            bldr.append(attributeName).append(" not memberOf ");
            RuleConditionBuilderHelper.addConditionValue(attributeType,
                    values[0], bldr);
            break;
        }
        this.conditionList.add(bldr.toString());
    }

    /**
     * Creates the NOT_EMPTY condition if required. Since some criteria like
     * CONTAINS require automatic insertion of the NOT_EMPTY condition, this
     * method checks that such a condition has not been already added for this
     * attribute.
     * 
     * @param bldr
     * @param attributeName
     */
    private void addNotEmptyCondition(StringBuilder bldr, String attributeName, String separatorString) {
        if (!this.attributesWithNotEmptyCondition.contains(attributeName)) {
            this.attributesWithNotEmptyCondition.add(attributeName);
            bldr.append(attributeName).append(" ")
                    .append(CriteriaOperatorEnum.IS_NOT_EMPTY.getOperatorString());
            bldr.append(", ");
        }

    }

    public void addConditionAsString(final String condStr) {
        this.conditionList.add(condStr);
    }

    /**
     * @return the drlVariableName
     */
    public String getDrlVariableName() {
        return drlVariableName;
    }

    /**
     * @param generateEmptyClause the generateEmptyClause to set
     */
    public void setGenerateEmptyClause(boolean generateEmptyClause) {
        this.generateEmptyClause = generateEmptyClause;
    }

}
