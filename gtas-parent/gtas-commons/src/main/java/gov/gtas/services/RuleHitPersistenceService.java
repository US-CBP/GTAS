/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import gov.gtas.model.HitDetail;

import java.util.Set;

public interface RuleHitPersistenceService {

	Iterable<HitDetail> persistToDatabase(Set<HitDetail> hitDetailSet);

	void updateFlightHitCounts(Set<Long> flights);
}
