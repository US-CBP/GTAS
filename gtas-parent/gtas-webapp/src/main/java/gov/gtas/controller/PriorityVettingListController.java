/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.gtas.model.dto.ViewUpdateDTo;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.PriorityVettingListService;
import gov.gtas.services.dto.PriorityVettingListDTO;
import gov.gtas.services.dto.PriorityVettingListRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;

@RestController
public class PriorityVettingListController {
	private final PriorityVettingListService priorityVettingListService;
	private final Logger logger = LoggerFactory.getLogger(PriorityVettingListController.class);
	private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PriorityVettingListRequest.DATE_FORMAT);
	private final ObjectMapper objectMapper = new ObjectMapper().setDateFormat(simpleDateFormat);

	public PriorityVettingListController(PriorityVettingListService priorityVettingListService) {
		this.priorityVettingListService = priorityVettingListService;
	}

	@RequestMapping(value = "/hits", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody PriorityVettingListDTO getAll(@RequestParam(value = "requestDto", required = false) String requestDto)
			throws IOException {
		final PriorityVettingListRequest request = objectMapper.readValue(requestDto, PriorityVettingListRequest.class);
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		return priorityVettingListService.generateDtoFromRequest(request, userId);
	}

	@RequestMapping(value = "/hits", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void update(@RequestBody ViewUpdateDTo requestDto) {
		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		priorityVettingListService.update(requestDto, userId);
	}

}
