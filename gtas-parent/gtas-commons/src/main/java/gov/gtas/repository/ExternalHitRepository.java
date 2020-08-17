/*
 *
 *  * All Application code is Copyright 2020, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.repository;

import gov.gtas.model.ExternalHit;
import org.springframework.data.repository.CrudRepository;

public interface ExternalHitRepository extends CrudRepository<ExternalHit, Long> {
}
