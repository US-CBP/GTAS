/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import gov.gtas.model.Lookout;
import gov.gtas.model.lookup.HitCategory;
import gov.gtas.services.HitDetailService;
import gov.gtas.vo.HitDetailVo;
import gov.gtas.model.HitDetail;

import java.util.*;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HitsSummaryController {
	private static final Logger logger = LoggerFactory.getLogger(HitsSummaryController.class);

	private final HitDetailService hitsDetailsService;

	public HitsSummaryController(HitDetailService hitsDetailsService) {
		this.hitsDetailsService = hitsDetailsService;
	}

	@RequestMapping(value = "/hit/passenger", method = RequestMethod.GET)
	@Transactional
	public @ResponseBody Set<HitDetailVo> getRules(@RequestParam(value = "passengerId", required = false) String id) {
		return getHitDetailsMapped(hitsDetailsService.getByPassengerId(Long.parseLong(id)));
	}

	@RequestMapping(value = "/hit/flightpassenger", method = RequestMethod.GET)
	@Transactional
	public @ResponseBody LinkedHashSet<HitDetailVo> getRulesByPassengerAndFlight(
			@RequestParam(value = "passengerId") String passengerId,
			@RequestParam(value = "flightId") String flightId) {

		Set<HitDetail> hitDetailSet = hitsDetailsService.getByPassengerId(Long.parseLong(passengerId));
		return getHitDetailsMapped(hitDetailSet);
	};

	@Transactional
	public LinkedHashSet<HitDetailVo> getHitDetailsMapped(Set<HitDetail> tempHitDetailList) {

		LinkedHashSet<HitDetailVo> hitDetailVoList = new LinkedHashSet<>();
		for (HitDetail htd : tempHitDetailList) {
			HitDetailVo hitDetailVo = new HitDetailVo();
			hitDetailVo.setRuleId(htd.getRuleId());
			hitDetailVo.setRuleTitle(htd.getTitle());
			hitDetailVo.setRuleDesc(htd.getDescription());
			Lookout lookout = htd.getLookout();
			HitCategory hitCategory = lookout.getHitCategory();
			hitDetailVo.setCategory(hitCategory.getName());
			htd.setLookoutId(null);
			htd.setLookout(null);
			hitDetailVo.getHitsDetailsList().add(htd);
			hitDetailVoList.add(hitDetailVo);
		}
		return hitDetailVoList;
	}

}
