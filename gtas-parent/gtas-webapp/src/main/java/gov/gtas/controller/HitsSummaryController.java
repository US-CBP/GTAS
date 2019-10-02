/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import gov.gtas.vo.HitDetailVo;
import gov.gtas.model.HitDetail;
import gov.gtas.model.HitsSummary;
import gov.gtas.model.lookup.RuleCat;
import gov.gtas.model.lookup.WatchlistCategory;
import gov.gtas.services.HitsSummaryService;
import gov.gtas.services.RuleCatService;
import gov.gtas.services.watchlist.WatchlistCatService;

import java.util.*;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HitsSummaryController {
	private static final Logger logger = LoggerFactory.getLogger(HitsSummaryController.class);

	@Autowired
	private HitsSummaryService hitsSummaryService;

	@Autowired
	private RuleCatService ruleCatService;

	@Autowired
	private WatchlistCatService watchlistCatService;

	@RequestMapping(value = "/hit/passenger", method = RequestMethod.GET)
	@Transactional
	public @ResponseBody Set<HitDetailVo> getRules(@RequestParam(value = "passengerId", required = false) String id) {

		return getHitDetailsMapped(hitsSummaryService.findByPassengerId(Long.parseLong(id)).getHitdetails());
	}

	@RequestMapping(value = "/hit/flightpassenger", method = RequestMethod.GET)
	@Transactional
	public @ResponseBody Set<HitDetailVo> getRulesByPassengerAndFlight(
			@RequestParam(value = "passengerId") String passengerId,
			@RequestParam(value = "flightId") String flightId) {

		List<HitsSummary> tempSumList = hitsSummaryService.findByFlightIdAndPassengerId(Long.parseLong(flightId),
				Long.parseLong(passengerId));
		Set<HitDetail> tempDetList = new HashSet<>();

		// Multiple summaries can exist for the same flight/pax combination. We will
		// break open the summaries to get the hit details,
		// then combine those lists into a singular list in order to convert it into our
		// dto list

		for (HitsSummary h : tempSumList) {
			tempDetList.addAll(h.getHitdetails());
		}
		;

		return getHitDetailsMapped(tempDetList);
	};

	@Transactional
	public Set<HitDetailVo> getHitDetailsMapped(Set<HitDetail> tempHitDetailList) {

		Set<HitDetailVo> hitDetailVoList = new HashSet<>();
		for (HitDetail htd : tempHitDetailList) {
			HitDetailVo hitDetailVo = new HitDetailVo();
			hitDetailVo.setRuleId(htd.getRuleId());
			hitDetailVo.setRuleTitle(htd.getTitle());
			hitDetailVo.setRuleDesc(htd.getDescription());
			hitDetailVo.getHitsDetailsList().add(htd);
			hitDetailVo.setRuleType(htd.getParent().getHitType());
			try {
				String category = "";
				if (htd.getHitType() != null && htd.getHitType().equals("R")) {
					RuleCat r = this.ruleCatService
							.findRuleCatByCatId(this.ruleCatService.fetchRuleCatIdFromRuleId(htd.getRuleId()));
					if (r != null)
						category = r.getCategory();
				} else if (htd.getHitType() != null && (htd.getHitType().equals("P") || htd.getHitType().equals("D"))) {
					WatchlistCategory c = this.watchlistCatService.findCatByWatchlistItemId(htd.getRuleId());
					if (c != null)
						category = c.getName();
				}
				hitDetailVo.setCategory(category);
				hitDetailVoList.add(hitDetailVo);
			} catch (Exception e) {
				logger.error("Failed to make a hit detail vo!" + e);
			}
		}
		return hitDetailVoList;
	}

}
