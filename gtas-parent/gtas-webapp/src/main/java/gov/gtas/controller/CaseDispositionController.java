/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import gov.gtas.model.Case;
import gov.gtas.model.lookup.HitDispositionStatus;
import gov.gtas.model.lookup.RuleCat;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.CaseDispositionService;
import gov.gtas.services.CaseDispositionServiceImpl;
import gov.gtas.services.RuleCatService;
import gov.gtas.services.dto.CasePageDto;
import gov.gtas.services.dto.CaseRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import gov.gtas.vo.passenger.CaseVo;


@RestController
public class CaseDispositionController {

    @Autowired
    private CaseDispositionService caseDispositionService;

    @Autowired
    private RuleCatService ruleCatService;
    
    private final Logger logger = LoggerFactory.getLogger(CaseDispositionController.class);

    @RequestMapping(value = "/getAllCaseDispositions", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    CasePageDto getAll(@RequestBody CaseRequestDto request, HttpServletRequest hsr) {
        
        hsr.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT",SecurityContextHolder.getContext());

        CasePageDto casePageDto = caseDispositionService.findAll(request);
        return casePageDto;
    }

    //getOneHistDisp
    @RequestMapping(method = RequestMethod.POST, value = "/getOneHistDisp",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    CasePageDto getOneHistDisp(@RequestBody CaseRequestDto request, HttpServletRequest hsr)
            throws ParseException {
        HashMap _tempMap = new HashMap();

        return caseDispositionService.findHitsDispositionByCriteria(request);
    }


    //getHistDispComments
    @RequestMapping(method = RequestMethod.GET, value = "/getHistDispComments")
    public Map<String, Object> getHistDispComments(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate)
            throws ParseException {
        HashMap _tempMap = new HashMap();

        return _tempMap;
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/getCurrentServerTime")
    @ResponseBody
    public long getCurrentServerTime() {
        
        Date currentServerTime = caseDispositionService.getCurrentServerTime();
        long currentServerTimeMillis = currentServerTime.getTime();
        
        return currentServerTimeMillis;
    }

    //getRuleCats
    @RequestMapping(method = RequestMethod.GET, value = "/getRuleCats")
    public List<RuleCat> getRuleCats()
            throws Exception {

        List<RuleCat> _tempRuleCatList = new ArrayList<RuleCat>();
        Iterable<RuleCat> _tempIterable =  ruleCatService.findAll();
        if(_tempIterable!=null){
            _tempRuleCatList = StreamSupport.stream(_tempIterable.spliterator(),false).collect(Collectors.toList());
        }
        for( RuleCat _tempRuleCat : _tempRuleCatList){
            //_tempRuleCat.setHitsDispositions(null);
        }
        return _tempRuleCatList;
    }

    //updateHistDisp
    @RequestMapping(method = RequestMethod.POST, value = "/updateHistDisp")
    public
    @ResponseBody
    Case updateHistDisp(@RequestBody CaseRequestDto request, HttpServletRequest hsr) {
        Case aCase = new Case();
        try {

            aCase = caseDispositionService.addCaseComments(request.getFlightId(), request.getPaxId(),
                                                            request.getHitId(), request.getCaseComments(),
                                                            request.getStatus(), request.getValidHit(),
                                                            request.getMultipartFile(),  GtasSecurityUtils.fetchLoggedInUserId());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return aCase;
    }

    //updateHistDispAttachments
    @RequestMapping(method = RequestMethod.POST, value = "/updateHistDispAttachments")
    public
    @ResponseBody
    Case updateHistDispAttachments(@RequestParam("file") MultipartFile file, @RequestParam("flightId") String flightId, @RequestParam("paxId") String paxId,
                                   @RequestParam("hitId") String hitId, @RequestParam("caseComments")String caseComments,
                                   @RequestParam("status")String status,
                                   @RequestParam("validHit")String validHit) {
        Case aCase = new Case();
        try {
            MultipartFile multipartFile = file;

            aCase = caseDispositionService.addCaseComments(Long.parseLong(flightId), Long.parseLong(paxId),
                    Long.parseLong(hitId), caseComments,
                    status, validHit,
                    multipartFile, GtasSecurityUtils.fetchLoggedInUserId());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return aCase;
    }

    //createManualCaseAttachments
    @RequestMapping(method = RequestMethod.POST, value = "/createManualCaseAttachments")
    public
    @ResponseBody
    Case createManualCaseAttachments(@RequestParam("file") MultipartFile file, @RequestParam("flightId") String flightId, @RequestParam("paxId") String paxId,
                                   @RequestParam("hitId") String hitId, @RequestParam("caseComments")String caseComments,
                                   @RequestParam("status")String status,
                                   @RequestParam("validHit")String validHit) {
        Case aCase = new Case();
        try {
            MultipartFile multipartFile = file;

            aCase = caseDispositionService.addCaseComments(Long.parseLong(flightId), Long.parseLong(paxId),
                    Long.parseLong(hitId), caseComments,
                    status, validHit,
                    multipartFile,  GtasSecurityUtils.fetchLoggedInUserId());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return aCase;
    }

    //createManualCase
    @RequestMapping(method = RequestMethod.POST, value = "/createManualCase")
    public
    @ResponseBody
    Case createManualCase(@RequestBody CaseRequestDto request, HttpServletRequest hsr) {
        Case aCase = new Case();
        try {

            aCase = caseDispositionService.createManualCase(request.getFlightId(), request.getPaxId(),
                    request.getRuleCatId(), request.getCaseComments(),  GtasSecurityUtils.fetchLoggedInUserId()
                    );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return aCase;
    }

    //updateCase
    @RequestMapping(method = RequestMethod.POST, value = "/updateCase")
    public Map<String, Object> updateCase(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate)
            throws ParseException {
        HashMap _tempMap = new HashMap();

        return _tempMap;
    }

    @RequestMapping(value = "/hitdispositionstatuses", method = RequestMethod.GET)
    public @ResponseBody List<HitDispositionStatus> getHitDispositionStatuses() {
        return caseDispositionService.getHitDispositionStatuses();
    }


//    @RequestMapping(value = "/countdownAPISFlag", method = RequestMethod.GET, produces="text/plain")
//    public @ResponseBody String getCountdownAPISFlag() {
//        return caseDispositionService.getCountdownAPISFlag();
//    }

    
    @ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/passenger/caseHistory/{paxId}", method = RequestMethod.GET)
	public List<CaseVo> getPassengerCaseHistory(
			@PathVariable(value = "paxId") Long paxId) {

    	List<CaseVo> vos =new ArrayList<CaseVo>();
    	
    	List<Case> cases = caseDispositionService.getCaseHistoryByPaxId(paxId);
    	
    	
    	for(Case _case : cases) {
    		CaseVo vo = new CaseVo();
    		CaseDispositionServiceImpl.copyIgnoringNullValues(_case, vo);
    		vos.add(vo);
    	}
    	
    	return vos;
    }

}
