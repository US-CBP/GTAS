/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import gov.gtas.model.HitDetail;
import gov.gtas.model.Passenger;
import gov.gtas.services.dto.MappedGroups;
import gov.gtas.vo.HitDetailVo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public interface HitDetailService {
	Set<HitDetail> getByPassengerId(Long passengerId);

	List<HitDetailVo> getLast10RecentHits(Set<Passenger> passengerSet, Passenger p);

	MappedGroups getHitDetailsWithGroups(Set<HitDetail> hitDetailList);
}
