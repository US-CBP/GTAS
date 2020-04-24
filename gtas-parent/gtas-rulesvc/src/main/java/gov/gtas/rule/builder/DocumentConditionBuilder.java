/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder;

import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.TypeEnum;
import gov.gtas.querybuilder.mappings.DocumentMapping;

import java.text.ParseException;

public class DocumentConditionBuilder extends EntityConditionBuilder {
	/*
	 * The logger for the DocumentConditionBuilder.
	 */
	DocumentConditionBuilder(final String drlVariableName) {
		super(drlVariableName, EntityEnum.DOCUMENT.getEntityName());
	}

	@Override
	protected void addSpecialConditions(StringBuilder bldr) {
	}

	String getPassengerIdLinkExpression() {
		return getDrlVariableName() + "." + DocumentMapping.DOCUMENT_OWNER_ID.getFieldName();
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
