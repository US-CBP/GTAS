/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services;

import gov.gtas.model.PendingHitDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Set;

import static gov.gtas.constant.GtasSecurityConstants.PRIVILEGES_ADMIN_AND_MANAGE_HITS;

@Service
public interface PendingHitDetailsService {

    @PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_HITS)
    void createPendingHitDetail(Long paxId, Long flightId, String userId);

    @PreAuthorize(PRIVILEGES_ADMIN_AND_MANAGE_HITS)
    void saveAllPendingHitDetails(Set<PendingHitDetails> phdSet);
}
