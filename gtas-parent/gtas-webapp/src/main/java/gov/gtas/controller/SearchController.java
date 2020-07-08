/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.gtas.constants.Constants;
import gov.gtas.enumtype.Status;
import gov.gtas.json.JsonServiceResponse;
import gov.gtas.model.Passenger;
import gov.gtas.querybuilder.exceptions.InvalidQueryException;
import gov.gtas.services.PassengerService;
import gov.gtas.services.dto.AdhocQueryDto;
import gov.gtas.services.dto.LinkAnalysisDto;
import gov.gtas.services.search.SearchService;

@RestController
@RequestMapping(Constants.SEARCH_SERVICE)
public class SearchController {
	private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

	@Autowired
	SearchService searchService;

	@Autowired
	PassengerService paxService;

	@RequestMapping(value = Constants.RUN_SEARCH_PASSENGER_URI, method = RequestMethod.GET)
	public JsonServiceResponse runPassengerQuery(@RequestParam(value = "query") String query,
			@RequestParam(value = "pageNumber") Integer pageNumber, @RequestParam(value = "pageSize") Integer pageSize,
			@RequestParam(value = "column") String column, @RequestParam(value = "dir") String dir)
			throws InvalidQueryException {

		AdhocQueryDto queryResults = searchService.findPassengers(query, pageNumber, pageSize, column, dir);
		return new JsonServiceResponse(Status.SUCCESS, "success", queryResults);
	}

	@RequestMapping(value = "/queryLinks", method = RequestMethod.GET)
	public JsonServiceResponse runLinksQuery(@RequestParam(value = "paxId") Long paxId,
			@RequestParam(value = "pageNumber") Integer pageNumber, @RequestParam(value = "pageSize") Integer pageSize,
			@RequestParam(value = "column") String column, @RequestParam(value = "dir") String dir)
			throws InvalidQueryException {

		Passenger pax = paxService.findByIdWithFlightAndDocumentsAndMessageDetails(paxId);
		LinkAnalysisDto queryResults = searchService.findPaxLinks(pax, pageNumber, pageSize, column, dir);
		return new JsonServiceResponse(Status.SUCCESS, "success", queryResults);
	}
}
