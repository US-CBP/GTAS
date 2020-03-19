/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.rule.builder;

import gov.gtas.enumtype.EntityEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PassengerConditionBuilder extends EntityConditionBuilder {
	private static final Logger logger = LoggerFactory.getLogger(PassengerConditionBuilder.class);

	public PassengerConditionBuilder(final String drlVariableName) {
		super(drlVariableName, EntityEnum.PASSENGER.getEntityName());
		this.generateEmptyClause = true;
	}

	@Override
	protected void addSpecialConditions(StringBuilder bldr) {
		logger.trace("No linked conditions");
	}

	/**
	 * Adds a condition to link the passenger to another entity, such as document.
	 * 
	 * @param matchExpression
	 */
	public void addLinkByIdCondition(final String matchExpression) {
		super.addConditionAsString("id == " + matchExpression);
	}
}
