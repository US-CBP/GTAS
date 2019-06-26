/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.lookup.RuleCat;

public interface RuleCatService {

    RuleCat findRuleCatByID(Long id);

    RuleCat findRuleCatByCatId(Long catId);

    Iterable<RuleCat> findAll();

    Long fetchRuleCatPriorityIdFromRuleId(Long ruleId) throws Exception;

    Long fetchRuleCatIdFromRuleId(Long ruleId) throws Exception;
    
    Long fetchRuleCatIdFromNonUdrRuleId(Long ruleId) throws Exception;

}
