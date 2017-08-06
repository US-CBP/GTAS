/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import gov.gtas.services.CaseDispositionService;
import gov.gtas.services.dto.CasePageDto;
import gov.gtas.services.dto.CaseRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CaseDispositionController {

    @Autowired
    private CaseDispositionService caseDispositionService;

    //getAll
//    @RequestMapping(method = RequestMethod.POST, value = "/getAllCaseDispositions")
//    public Map<String, Object> getAll(
//            @RequestParam(value = "startDate", required = false) String startDate,
//            @RequestParam(value = "endDate", required = false) String endDate)
//            throws ParseException {
//        HashMap _tempMap = new HashMap();
//
//        return _tempMap;
//    }

    @RequestMapping(value = "/getAllCaseDispositions", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    CasePageDto getAll(@RequestBody CaseRequestDto request, HttpServletRequest hsr) {
        hsr.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT",
                SecurityContextHolder.getContext());
        return caseDispositionService.findAll(request);
    }

    //getOneHistDisp
    @RequestMapping(method = RequestMethod.GET, value = "/getOneHistDisp")
    public Map<String, Object> getOneHistDisp(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate)
            throws ParseException {
        HashMap _tempMap = new HashMap();

        return _tempMap;
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

    //updateHistDisp
    @RequestMapping(method = RequestMethod.POST, value = "/updateHistDisp")
    public Map<String, Object> updateHistDisp(
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate)
            throws ParseException {
        HashMap _tempMap = new HashMap();

        return _tempMap;
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

    }
