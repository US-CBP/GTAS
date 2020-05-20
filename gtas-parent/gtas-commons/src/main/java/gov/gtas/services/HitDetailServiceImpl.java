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
import gov.gtas.repository.HitDetailRepository;
import gov.gtas.util.PaxDetailVoUtil;
import gov.gtas.vo.HitDetailVo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class HitDetailServiceImpl implements HitDetailService {

	private final HitDetailRepository hitDetailRepository;

	public HitDetailServiceImpl(HitDetailRepository hitDetailRepository) {
		this.hitDetailRepository = hitDetailRepository;
	}

	@Override
	public Set<HitDetail> getByPassengerId(Long passengerId) {
		return hitDetailRepository.getSetFromPassengerId(passengerId);
	}

	@Transactional
	public List<HitDetailVo> getLast10RecentHits(Set<Passenger> passengerSet, Passenger p) {
		Set<HitDetail> hitDetailSet = hitDetailRepository.findFirst10ByPassengerInOrderByCreatedDateDesc(passengerSet);
		Set<HitDetailVo> hitDetailVoSet = new LinkedHashSet<>();

		for (HitDetail hitDetail : hitDetailSet) {
			HitDetailVo hitDetailVo = HitDetailVo.from(hitDetail);
			hitDetailVoSet.add(hitDetailVo);
			PaxDetailVoUtil.deleteAndMaskPIIFromHitDetailVo(hitDetailVo, hitDetail.getPassenger());
		}

		List<HitDetailVo> hitDetails = new ArrayList<>(hitDetailVoSet);
		if (!hitDetails.isEmpty()) {
			hitDetails.sort((hd1, hd2) -> {
				Date createDate = hd1.getCreateDate();
				Date otherDate = hd2.getCreateDate();
				if (createDate == null || otherDate == null) {
					return 0;
				} else if (createDate.after(otherDate)) {
					return -1;
				} else if (otherDate.after(createDate)) {
					return 1;
				} else {
					return 0;
				}
			});
		}
		return hitDetails;
	}

}
