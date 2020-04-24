/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.rule.builder.util;

import gov.gtas.rule.builder.EntityConditionBuilder;

import java.util.List;

/*
* Utility class to handle like objects. E.G. this != $bag1
$bag1:Bag(passengerId == $p.id, flightId == $f.id, primeFlight == false, bagMeasurements.weight <= 10, bagMeasurements.bagCount <= 1, data_source == "PNR")
$bag2:Bag(passengerId == $p.id, flightId == $f.id, primeFlight == true, bagMeasurements.weight >= 40, bagMeasurements.bagCount >= 2, data_source == "PNR", this != $bag1)
* */
public class ConditionBuilderUtils {
	public static <T extends EntityConditionBuilder> void handleMultipleObjectTypeOnSameRule(
			List<T> entityConditionBuilderList) {
		for (int i = 0; i < entityConditionBuilderList.size(); i++) {
			EntityConditionBuilder ecb = entityConditionBuilderList.get(i);
			for (EntityConditionBuilder otherEcb : entityConditionBuilderList) {
				if (ecb.getGroupNumber() > otherEcb.getGroupNumber()) {
					ecb.addConditionAsString("this != " + otherEcb.getDrlVariableName());
				}
			}
		}
	}
}
