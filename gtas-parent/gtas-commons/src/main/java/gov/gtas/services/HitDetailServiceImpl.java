/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;

import gov.gtas.model.HitDetail;
import gov.gtas.model.HitMaker;
import gov.gtas.model.Passenger;
import gov.gtas.repository.HitDetailRepository;
import gov.gtas.services.dto.MappedGroups;
import gov.gtas.util.PaxDetailVoUtil;
import gov.gtas.vo.HitDetailVo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

	public MappedGroups getHitDetailsWithGroups(Set<HitDetail> hitDetailList) {
		Set<Long> hdIds = hitDetailList.stream().map(HitDetail::getId).collect(Collectors.toSet());
		Set<Long> flightIds = hitDetailList.stream().map(HitDetail::getFlightId).collect(Collectors.toSet());
		Set<HitDetail> hitDetails = hitDetailRepository.getHitDetailsWithCountryGroups(hdIds, flightIds);
		MappedGroups mappedGroups = new MappedGroups();
		Map<String, Set<HitDetail>> stringHitDetailMap = new HashMap<>();
		for (HitDetail hd : hitDetails) {
			HitMaker hm = hd.getHitMaker();
			//Check sharable hit and sort into map based on labels.
			if (hm.getCountryGroup() != null) {
				String label = hm.getCountryGroup().getCountryGroupLabel();
				if (stringHitDetailMap.containsKey(label)) {
					stringHitDetailMap.get(label).add(hd);
				} else {
					Set<HitDetail> hitDetailSet = new HashSet<>();
					hitDetailSet.add(hd);
					stringHitDetailMap.put(label, hitDetailSet);
				}
			}
		}

		mappedGroups.setCountryMap(stringHitDetailMap);

		return mappedGroups;
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
