/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.svc;

import gov.gtas.model.RuleHitDetail;
import gov.gtas.config.Neo4JConfig;
import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.model.*;
import gov.gtas.model.lookup.PassengerTypeCode;
import gov.gtas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class GraphRulesServiceImpl implements GraphRulesService {

	private final GraphRuleRepository graphRuleRepository;

	private final Neo4JClient neo4JClient;

	@Autowired
	public GraphRulesServiceImpl(GraphRuleRepository graphRuleRepository, Neo4JConfig neo4JConfig) {
		this.graphRuleRepository = graphRuleRepository;
		if (neo4JConfig.getNeo4JRuleEngineEnabled() && neo4JConfig.enabled()) {
			this.neo4JClient = new Neo4JClient(neo4JConfig.getNeo4JGraphDbUrl(), neo4JConfig.neoUserName(), neo4JConfig.neoPassword());
		} else {
			this.neo4JClient = null;
		}
	}

	@Override
	@Transactional
	public Set<HitDetail> generateHitDetails(List<RuleHitDetail> ruleHitDetails) {
		if (ruleHitDetails.isEmpty()) {
			return new HashSet<>();
		}
		Set<HitDetail> hitDetailSet = new HashSet<>();
		for (RuleHitDetail ruleHitDetail : ruleHitDetails) {
			HitDetail hitDetail = HitDetail.from(ruleHitDetail);
			hitDetailSet.add(hitDetail);
		}
		return hitDetailSet;
	}

	@Override
	@Transactional
	public Set<RuleHitDetail> graphResults(Set<Passenger> passengers) {
		Iterable<GraphRule> graphRules = getGraphRules();
		Set<RuleHitDetail> ruleHitDetails = new HashSet<>();
		Map<String, List<Passenger>> paxMap = new HashMap<>();
		for (Passenger p : passengers) {
			if (p.getPassengerIDTag() != null && p.getPassengerIDTag().getIdTag() != null) {
				if (paxMap.containsKey(p.getPassengerIDTag().getIdTag())) {
					paxMap.get(p.getPassengerIDTag().getIdTag()).add(p);
				} else {
					List<Passenger> passengerList = new ArrayList<>();
					passengerList.add(p);
					paxMap.put(p.getPassengerIDTag().getIdTag(), passengerList);
				}
			}
		}

		for (GraphRule graphRule : graphRules) {
			Set<String> passengerHitIds = getPassengerHitIds(graphRule, paxMap.keySet()); // This command runs the
																							// graph rules!
			for (String idTag : passengerHitIds) {
				for (Passenger passenger : paxMap.get(idTag)) {
					RuleHitDetail rhd = new RuleHitDetail(HitTypeEnum.GRAPH_HIT);
					rhd.setFlightId(passenger.getFlight().getId());
					rhd.setPassenger(passenger);
					rhd.setPassengerName(passenger.getPassengerDetails().getFirstName() + " "
							+ passenger.getPassengerDetails().getLastName());
					rhd.setTitle(graphRule.getTitle());
					rhd.setDescription(graphRule.getDescription());
					rhd.setHitRule(graphRule.getDescription() + ":" + graphRule.getId());
					rhd.setHitCount(1);
					rhd.setRuleId(graphRule.getId());
					rhd.setPassengerId(passenger.getId());
					rhd.setPassengerType(PassengerTypeCode.P);
					rhd.setFlightId(passenger.getFlight().getId());
					rhd.setHitMakerId(graphRule.getId());
					rhd.setCipherQuery(graphRule.getCipherQuery());
					rhd.setGraphHitDisplay(graphRule.getDisplayCondition());
					ruleHitDetails.add(rhd);
				}
			}
		}

		return ruleHitDetails;
	}

	private Set<String> getPassengerHitIds(GraphRule graphRule, Set<String> paxIds) {
		return neo4JClient.runQueryAndReturnPassengerIdHits(graphRule, paxIds);
	}

	private Iterable<GraphRule> getGraphRules() {
		return graphRuleRepository.findAll();
	}

}
