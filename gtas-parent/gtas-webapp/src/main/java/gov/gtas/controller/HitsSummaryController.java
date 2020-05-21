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
import gov.gtas.util.PaxDetailVoUtil;
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
			HitDetailVo hitDetailVo = new HitDetailVo();
			PaxDetailVoUtil.populateHitDetailVo(hitDetailVo, htd, user);
			hitDetailVoList.add(hitDetailVo);
		}
		return hitDetailVoList;
	}

}
