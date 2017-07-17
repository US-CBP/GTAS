/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import gov.gtas.vo.HitDetailVo;
import gov.gtas.model.HitDetail;
import gov.gtas.model.HitsSummary;
import gov.gtas.services.HitsSummaryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

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
    private static final Logger logger = LoggerFactory
            .getLogger(HitsSummaryController.class);

    @Autowired
    private HitsSummaryService hitsSummaryService;

    @RequestMapping(value = "/hit/passenger", method = RequestMethod.GET)
    @Transactional
    public @ResponseBody List<HitDetailVo> getRules(
            @RequestParam(value = "passengerId", required = false) String id) {

        return getHitDetailsMapped(hitsSummaryService.findByPassengerId(Long
                .parseLong(id)));
    }
    
    @RequestMapping(value = "/hit/flightpassenger", method = RequestMethod.GET)
	@Transactional
	public @ResponseBody List<HitDetailVo> getRulesByPassengerAndFlight(
			@RequestParam(value = "passengerId") String passengerId,
			@RequestParam(value = "flightId") String flightId){
    	
    	List<HitsSummary> tempSumList = hitsSummaryService.findByFlightIdAndPassengerId(Long.parseLong(flightId), Long.parseLong(passengerId));
    	List<HitDetail> tempDetList = new ArrayList<HitDetail>();
    	
    	//Multiple summaries can exist for the same flight/pax combination. We will break open the summaries to get the hit details,
    	//then combine those lists into a singular list in order to convert it into our dto list
    	
    	for(HitsSummary h : tempSumList){
    		tempDetList.addAll(h.getHitdetails());
    	};
		
		return getHitDetailsMapped(tempDetList);
	};

    @Transactional
    public List<HitDetailVo> getHitDetailsMapped(
            List<HitDetail> tempHitDetailList) {

        int i = 0;
        List<HitDetailVo> tempList = new ArrayList<HitDetailVo>();
        HitDetailVo hdetailVo = new HitDetailVo();

        HashMap<Integer, HitDetailVo> _tempMap = new HashMap<Integer, HitDetailVo>();
        HashSet<HitDetailVo> tempSet = new HashSet<HitDetailVo>();

        for (HitDetail htd : tempHitDetailList) {

            if ((i != htd.getRuleId().intValue())
                    && (!_tempMap.containsKey(Integer.valueOf(htd.getRuleId()
                            .intValue())))) {
                // get Rule Desc
                i = htd.getRuleId().intValue();
                hdetailVo = new HitDetailVo();
                hdetailVo.setRuleId(htd.getRuleId());
                hdetailVo.setRuleTitle(htd.getTitle());
                hdetailVo.setRuleDesc(htd.getDescription());
                hdetailVo.getHitsDetailsList().add(htd);
                hdetailVo.setRuleType(htd.getParent().getHitType());
                _tempMap.put(Integer.valueOf(i), hdetailVo);
            } else {
                hdetailVo = _tempMap.get(Integer.valueOf(i));
                hdetailVo.getHitsDetailsList().add(htd);
            }
            tempSet.add(hdetailVo);
        }

        if (!tempSet.isEmpty()) {
            Iterator iter = tempSet.iterator();
            while (iter.hasNext()) {
                tempList.add((HitDetailVo) iter.next());
            }
        }

        return tempList;
    }

}
