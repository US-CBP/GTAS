/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import gov.gtas.constants.Constants;
import gov.gtas.enumtype.Status;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.querybuilder.exceptions.InvalidQueryException;
import gov.gtas.querybuilder.model.QueryRequest;
import gov.gtas.search.SearchService;

@RestController
@RequestMapping(Constants.SEARCH_SERVICE)
public class SearchController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
    
    @Autowired
    SearchService searchService;

    @RequestMapping(value = Constants.RUN_SEARCH_PASSENGER_URI, method=RequestMethod.POST)
    public JsonServiceResponse runPassengerQuery(@RequestBody QueryRequest queryRequest) throws InvalidQueryException {
        logger.info("Executing search query: ");
        return new JsonServiceResponse(Status.SUCCESS, "success" , null);
    }       
}
