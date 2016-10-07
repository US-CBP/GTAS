/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import gov.gtas.constants.Constants;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.WhitelistService;
import gov.gtas.vo.WhitelistVo;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Class Whitelist Contoller.
 */
@RestController
public class WhitelistContoller {
	private static final Logger logger = LoggerFactory
			.getLogger(WhitelistContoller.class);

	@Autowired
	private WhitelistService whitelistService;

	/**
	 * Gets the all whitelists.
	 *
	 * @return the all whitelists
	 */
	@RequestMapping(value = Constants.WHITELIST_GETALL, method = RequestMethod.GET)
	@ResponseBody
	public List<WhitelistVo> getAllWhitelists() {
		logger.info("Get All Whitelists");
		return whitelistService.getAllWhitelists();
	}

	/**
	 * Delete whitelist.
	 *
	 * @param id
	 *            the whitelist id
	 */
	@RequestMapping(value = Constants.WHITELIST_DELETE, method = RequestMethod.DELETE)
	public void deleteWhitelist(@PathVariable(value = "id") String id) {
		logger.info("delete a whitelist.");
		whitelistService.delete(Long.valueOf(id),
				GtasSecurityUtils.fetchLoggedInUserId());
	}

	/**
	 * Creates the whitelist.
	 *
	 * @param wlv
	 *            the Whitelist value object.
	 */
	@RequestMapping(value = Constants.WHITELIST_CREATE, method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	public void createWhitelist(@RequestBody WhitelistVo wlv) {
		logger.info("create a new whitelist.");
		whitelistService.create(wlv, GtasSecurityUtils.fetchLoggedInUserId());
	}

	/**
	 * Update whitelist.
	 *
	 * @param wlv
	 *            the Whitelist value object.
	 * @param id
	 *            the whitelist id
	 */
	@RequestMapping(value = Constants.WHITELIST_UPDATE, method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.OK)
	public void updateWhitelist(@RequestBody WhitelistVo wlv) {
		logger.info("update an existing whitelist.");
		whitelistService.update(wlv, GtasSecurityUtils.fetchLoggedInUserId());
	}
}
