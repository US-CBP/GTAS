/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services.dto;

import gov.gtas.enumtype.HitTypeEnum;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class PriorityVettingListRuleTypesTest {

	@Test
	public void testAllCheckboxes() {
		PriorityVettingListRuleTypes pvlrt = new PriorityVettingListRuleTypes();
		pvlrt.setGraphRule(true);
		pvlrt.setManual(true);
		pvlrt.setPartialWatchlist(true);
		pvlrt.setUserRule(true);
		pvlrt.setWatchlist(true); // adds wl, p, and d.
		Set<HitTypeEnum> hitSet = pvlrt.hitTypeEnums();
		Assert.assertEquals(7, hitSet.size());
	}
}
