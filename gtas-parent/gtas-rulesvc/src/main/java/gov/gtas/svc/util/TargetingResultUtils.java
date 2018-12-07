/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.util;

import gov.gtas.bo.BasicRuleServiceResult;
import gov.gtas.bo.RuleHitDetail;
import gov.gtas.bo.RuleServiceResult;
import gov.gtas.bo.TargetDetailVo;
import gov.gtas.bo.TargetSummaryVo;
import gov.gtas.enumtype.HitTypeEnum;
import gov.gtas.model.Flight;
import gov.gtas.model.FlightPax;
import gov.gtas.model.Passenger;
import gov.gtas.services.FlightService;
import gov.gtas.services.PassengerService;
import gov.gtas.util.Bench;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class TargetingResultUtils{
	private static final Logger logger = LoggerFactory
			.getLogger(TargetingResultUtils.class);
        
        public static Map<Long, Flight> createFlightMap(List<RuleHitDetail> ruleHitDetailList, PassengerService passengerService)
        {
            List<Long> flightIdList = new ArrayList<>();
            List<Flight> flightResultList = null;
            Map<Long, Flight> flightMap = new HashMap<>();
            
            for (RuleHitDetail rhd : ruleHitDetailList)
            {
               Long flightId = rhd.getFlightId();
               flightIdList.add(flightId);
            }  
            
            if (!flightIdList.isEmpty())
            {
                flightResultList = passengerService.getFlightsByIdList(flightIdList);

                for (Flight flight : flightResultList)
                {
                    flightMap.put(flight.getId(), flight);
                }
            }

            return flightMap;
        }

        public static Map<Long, Passenger> createPassengerMap(List<RuleHitDetail> ruleHitDetailList, PassengerService passengerService)
        {
            List<Long> passengerIdList = new ArrayList<>();
            List<Passenger> passengerResultList = null;
            Map<Long, Passenger> passengerMap = new HashMap<>();
            
            for (RuleHitDetail rhd : ruleHitDetailList)
            {
               Long passengerId = rhd.getPassengerId();
               passengerIdList.add(passengerId);
                
            }
            
            if (!passengerIdList.isEmpty())
            {
                passengerResultList = passengerService.getPaxByPaxIdList(passengerIdList);

                for (Passenger passenger :  passengerResultList)
                {
                   passengerMap.put(passenger.getId(), passenger);

                }
            }

            return passengerMap;

        }
        
        
        public static Map<Long, List<Flight> > createPassengerFlightMap(List<RuleHitDetail> ruleHitDetailList, PassengerService passengerService)
        {
            List<Long> ruleHitDetailPassengerIdList = new ArrayList<>();
            Map<Long, List<Flight> > passengerFlightMap =  new HashMap<>();  
            
                for (RuleHitDetail rhd : ruleHitDetailList)
                {
                   Long passengerId = rhd.getPassengerId();
                   ruleHitDetailPassengerIdList.add(passengerId);
                }

                // TODO: check for no rule hits, empty ruleHitDetailPassengerIdList
                if (!ruleHitDetailPassengerIdList.isEmpty())
                {
                    List<FlightPax> allFlightPaxByPassengerId = passengerService.getFlightPaxByPassengerIdList(ruleHitDetailPassengerIdList);

                    for (FlightPax flightPax : allFlightPaxByPassengerId)
                    {
                        Long passengerId = flightPax.getPassenger().getId();
                        Flight flight = flightPax.getFlight();
                        if (passengerFlightMap.containsKey(passengerId))
                        {
                            passengerFlightMap.get(passengerId).add(flight);
                        }
                        else
                        {
                           List<Flight> newFlightList = new ArrayList<>();
                           newFlightList.add(flight);
                           passengerFlightMap.put(passengerId,newFlightList); 
                        }
                    } 
                }

            return passengerFlightMap;
        }
	
	/**
	 * Eliminates duplicates and adds flight id, if missing.
	 * 
	 * @param result
	 * @return
	 */
	public static RuleServiceResult ruleResultPostProcesssing(
			RuleServiceResult result, PassengerService passengerService) {
		//logger.info("Entering ruleResultPostProcesssing().");
		// get the list of RuleHitDetail objects returned by the Rule Engine
		List<RuleHitDetail> resultList = result.getResultList();

		// create a Map to eliminate duplicates
		Map<RuleHitDetail, RuleHitDetail> resultMap = new HashMap<>();
                
		if (logger.isInfoEnabled()) {
			logger.info("Number of rule hits --> " + resultList.size());
		}
                
                Bench.start("qwerty1", "Before for RuleHitDetail loop in TargetingResultUtils.");
                
                Map<Long, List<Flight> > passengerFlightMap = createPassengerFlightMap(resultList, passengerService);
                
		for (RuleHitDetail rhd : resultList) {
			if (rhd.getFlightId() == null) {
				// get all the flights for the passenger
				// and replicate the RuleHitDetail object, for each flight id
				// Note that the RuleHitDetail key is (UdrId, EngineRuleId,
				// PassengerId, FlightId)

                                List<Flight> flights = passengerFlightMap.get(rhd.getPassengerId());
                                
				if (flights != null && !CollectionUtils.isEmpty(flights)) {
					try {
                                            Bench.start("qwerty2", "Before for Flight loop in TargetingResultUtils.");
						for (Flight flight : flights) {
							RuleHitDetail newrhd = rhd.clone();
							processPassengerFlight(newrhd, flight.getId(),
									resultMap);
						}
                                                Bench.end("qwerty2", "Before for Flight loop in TargetingResultUtils.");
					} catch (CloneNotSupportedException cnse) {
						logger.error("error - clone not supported:", cnse);
					}
				} else {
					// ERROR we do not have flights for this passenger
					logger.error("TargetingServiceUtils.ruleResultPostProcesssing() no flight information for passenger  with ID:"
							+ rhd.getPassenger().getId());
				}
			} else {
				processPassengerFlight(rhd, rhd.getFlightId(), resultMap);
			}
			rhd.setPassenger(null);
		}
                Bench.end("qwerty1", "After for RuleHitDetail loop in TargetingResultUtils.");
		// Now create the return list from the set, thus eliminating duplicates.
		RuleServiceResult ret = new BasicRuleServiceResult(
				new LinkedList<RuleHitDetail>(resultMap.values()),
				result.getExecutionStatistics());
		//logger.info("Exiting ruleResultPostProcesssing().");
		return ret;
	}

	private static void processPassengerFlight(RuleHitDetail rhd,
			Long flightId, Map<RuleHitDetail, RuleHitDetail> resultMap) {

		//logger.info("Entering processPassengerFlight().");
		rhd.setFlightId(flightId);

		// set the passenger object to null
		// since its only purpose was to provide flight
		// details.
		rhd.setPassenger(null);
		RuleHitDetail resrhd = resultMap.get(rhd);
		if (resrhd != null && resrhd.getRuleId() != rhd.getRuleId()) {
			resrhd.incrementHitCount();
			if (resrhd.getUdrRuleId() != null) {
				logger.info("This is a rule hit so increment the rule hit count.");
				// this is a rule hit
				resrhd.incrementRuleHitCount();
			} else {
				logger.info("This is a watch list hit.");
				// this is a watch list hit
				if (resrhd.getHitType() != rhd.getHitType()) {
					resrhd.setHitType(HitTypeEnum.PD);
				}
			}
		} else if (resrhd == null) {
			resultMap.put(rhd, rhd);
		}
		//logger.info("Exiting processPassengerFlight().");
	}

	public static void updateRuleExecutionContext(RuleExecutionContext ctx,
			RuleServiceResult res) {
		logger.info("Entering updateRuleExecutionContext().");
		ctx.setRuleExecutionStatistics(res.getExecutionStatistics());
		final Map<String, TargetSummaryVo> hitSummaryMap = new HashMap<>();
		for (RuleHitDetail rhd : res.getResultList()) {
			String key = rhd.getFlightId() + "/" + rhd.getPassengerId();
			TargetSummaryVo hitSummmary = hitSummaryMap.get(key);
			if (hitSummmary == null) {
				hitSummmary = new TargetSummaryVo(rhd.getHitType(),
						rhd.getFlightId(), rhd.getPassengerType(),
						rhd.getPassengerId(), rhd.getPassengerName());
				hitSummaryMap.put(key, hitSummmary);
			}
			hitSummmary.addHitDetail(new TargetDetailVo(rhd.getUdrRuleId(), rhd
					.getRuleId(), rhd.getHitType(), rhd.getTitle(), rhd
					.getHitReasons()));
		}
		ctx.setTargetingResult(hitSummaryMap.values());
		logger.info("Exiting updateRuleExecutionContext().");
	}
}
