/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.RuleCat;

public interface RuleCatService {

    public RuleCat findRuleCatByID(Long id);

    public Iterable<RuleCat> findAll();

    public Long fetchRuleCatPriorityIdFromRuleId(Long ruleId) throws Exception;

}
