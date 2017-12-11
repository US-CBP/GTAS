/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import gov.gtas.model.Case;
import gov.gtas.model.lookup.HitDispositionStatus;
import gov.gtas.model.lookup.RuleCat;
import gov.gtas.model.udr.Rule;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.CaseDispositionService;
import gov.gtas.services.RuleCatService;
import gov.gtas.services.dto.CasePageDto;
import gov.gtas.services.dto.CaseRequestDto;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
public class CaseDispositionController {

    @Autowired
    private CaseDispositionService caseDispositionService;

    @Autowired
    private RuleCatService ruleCatService;

    @RequestMapping(value = "/getAllCaseDispositions", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    CasePageDto getAll(@RequestBody CaseRequestDto request, HttpServletRequest hsr) {
        hsr.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT",
                SecurityContextHolder.getContext());
        return caseDispositionService.findAll(request);
    }

    //getOneHistDisp
    @RequestMapping(method = RequestMethod.POST, value = "/getOneHistDisp",
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    Case getOneHistDisp(@RequestBody CaseRequestDto request, HttpServletRequest hsr)
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
            _tempRuleCat.setHitsDispositions(null);
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
                                                            request.getMultipartFile());
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
                    multipartFile);
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
                    multipartFile);
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

}
