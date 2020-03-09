/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.gtas.rule.builder;

import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.TypeEnum;

import java.text.ParseException;

import static gov.gtas.rule.builder.RuleTemplateConstants.LINK_VARIABLE_SUFFIX;

/**
 *
 * @author gbays
 */
public class PaymentFormConditionBuilder extends EntityConditionBuilder {

	public PaymentFormConditionBuilder(final String drlVariableName) {
		super(drlVariableName, RuleTemplateConstants.PAYMENT_FORM_ENTITY_NAME);
	}

	public String getLinkVariableName() {
		return getDrlVariableName() + LINK_VARIABLE_SUFFIX;
	}

	@Override
	protected void addSpecialConditions(StringBuilder bldr) {

	}

	@Override
	public void addCondition(final CriteriaOperatorEnum opCode, final String attributeName,
			final TypeEnum attributeType, String[] values) throws ParseException {
		if (this.isEmpty()) {
			this.addConditionAsString("id == " + this.getLinkVariableName() + ".linkAttributeId");
		}
		super.addCondition(opCode, attributeName, attributeType, values);
	}

}
