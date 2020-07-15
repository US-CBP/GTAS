/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.HitMaker;
import gov.gtas.model.ManualHit;
import gov.gtas.model.PendingHitDetails;
import gov.gtas.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Set;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGES_ADMIN_AND_MANAGE_RULES;

@Service
public interface PendingHitDetailsService {

    @PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_RULES)
    void createManualPendingHitDetail(Long paxId, Long flightId, String userId, Long hitCategoryId, String desc);

    @PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_RULES)
    void saveAllPendingHitDetails(Set<PendingHitDetails> phdSet);

    @PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_RULES)
    PendingHitDetails createPendingHitDetails(Long paxId, Long flightId, String userId, String title,String desc,
                                              String ruleConditions, Float percentageMatch, HitMaker hm);

    @PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_RULES)
    void createManualHitMaker(String desc, User user, Long hitCategoryId);

}
