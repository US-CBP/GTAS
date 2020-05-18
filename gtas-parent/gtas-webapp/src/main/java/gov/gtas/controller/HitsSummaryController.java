/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import gov.gtas.model.*;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.repository.UserRepository;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.HitDetailService;
import gov.gtas.vo.HitDetailVo;

import java.util.*;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class HitsSummaryController {
	private static final Logger logger = LoggerFactory.getLogger(HitsSummaryController.class);

	private final HitDetailService hitsDetailsService;

	private final UserRepository userRepository;

	public HitsSummaryController(HitDetailService hitsDetailsService, UserRepository userRepository) {
		this.hitsDetailsService = hitsDetailsService;
		this.userRepository = userRepository;
	}

	@RequestMapping(value = "/hit/passenger", method = RequestMethod.GET)
	@Transactional
	public @ResponseBody Set<HitDetailVo> getRules(@RequestParam(value = "passengerId", required = false) String id) {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		User user = userRepository.userAndGroups(userId).orElseThrow(RuntimeException::new);
		Set<HitDetail> hitDetailSet = hitsDetailsService.getByPassengerId(Long.parseLong(id));
		return getHitDetailsMapped(hitDetailSet, user);
	}

	@RequestMapping(value = "/hit/flightpassenger", method = RequestMethod.GET)
	@Transactional
	public @ResponseBody LinkedHashSet<HitDetailVo> getRulesByPassengerAndFlight(
			@RequestParam(value = "passengerId") String passengerId,
			@RequestParam(value = "flightId") String flightId) {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		User user = userRepository.userAndGroups(userId).orElseThrow(RuntimeException::new);
		Set<HitDetail> hitDetailSet = hitsDetailsService.getByPassengerId(Long.parseLong(passengerId));
		return getHitDetailsMapped(hitDetailSet, user);
	};

	@Transactional
	public LinkedHashSet<HitDetailVo> getHitDetailsMapped(Set<HitDetail> passengerHitDetails, User user) {

		LinkedHashSet<HitDetailVo> hitDetailVoList = new LinkedHashSet<>();
		for (HitDetail htd : passengerHitDetails) {
			Passenger p = htd.getPassenger();
			HitDetailVo hitDetailVo = new HitDetailVo();
			hitDetailVo.setRuleId(htd.getRuleId());
			hitDetailVo.setRuleTitle(htd.getTitle());
			hitDetailVo.setRuleDesc(htd.getDescription());
			hitDetailVo.setSeverity(htd.getHitMaker().getHitCategory().getSeverity().toString());
			HitMaker lookout = htd.getHitMaker();
			HitCategory hitCategory = lookout.getHitCategory();
			hitDetailVo.setCategory(hitCategory.getName() + "(" + htd.getHitEnum().getDisplayName() + ")");
			hitDetailVo.setRuleAuthor(htd.getHitMaker().getAuthor().getUserId());
			hitDetailVo.setRuleConditions(htd.getRuleConditions());
			hitDetailVo.setRuleTitle(htd.getTitle());
			StringJoiner stringJoiner = new StringJoiner(", ");
			Set<UserGroup> userGroups = user.getUserGroups();
			for (HitViewStatus hitViewStatus : htd.getHitViewStatus()) {
				if (userGroups.contains(hitViewStatus.getUserGroup())) {
					stringJoiner.add(hitViewStatus.getHitViewStatusEnum().toString());
				}
			}
			hitDetailVo.setFlightDate(htd.getFlight().getMutableFlightDetails().getEtd());
			hitDetailVo.setStatus(stringJoiner.toString());
			if (!(!p.getDataRetentionStatus().isDeletedAPIS()
					&& p.getDataRetentionStatus().isHasApisMessage()
					|| (!p.getDataRetentionStatus().isDeletedPNR() && p.getDataRetentionStatus().isHasPnrMessage()))) {
				hitDetailVo.deletePII();
			} else if (!(!p.getDataRetentionStatus().isMaskedAPIS()
					&& p.getDataRetentionStatus().isHasApisMessage()
					|| (!p.getDataRetentionStatus().isMaskedPNR() && p.getDataRetentionStatus().isHasPnrMessage()))) {
					hitDetailVo.maskPII();
					}
			hitDetailVoList.add(hitDetailVo);
		}
		return hitDetailVoList;
	}

}
