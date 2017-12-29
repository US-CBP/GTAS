/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import gov.gtas.constant.AuditLogConstants;
import gov.gtas.enumtype.AuditActionType;
import gov.gtas.error.ErrorDetailInfo;
import gov.gtas.model.AuditRecord;
import gov.gtas.model.lookup.AppConfiguration;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.services.AuditLogPersistenceService;
import gov.gtas.services.ErrorPersistenceService;
import gov.gtas.util.DateCalendarUtils;
import gov.gtas.vo.AuditRecordVo;
import gov.gtas.vo.SettingsVo;

import static org.mockito.Matchers.anyBoolean;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Back-end REST service interface to support audit/error log viewing and
 * configuration management.
 */
@RestController
public class AdminController {

	private static final Logger logger = LoggerFactory
			.getLogger(AdminController.class);

	@Autowired
	private AppConfigurationRepository appConfigRepository;
	@Autowired
	private AuditLogPersistenceService auditService;

	@Autowired
	private ErrorPersistenceService errorService;

	@RequestMapping(method = RequestMethod.GET, value = "/auditlog")
	public List<AuditRecordVo> getAuditlog(
			@RequestParam(value = "user", required = false) String user,
			@RequestParam(value = "action", required = false) String action,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate)
			throws ParseException {
		Date st = null;
		Date nd = null;
		if (startDate != null) {
			st = DateCalendarUtils.parseJsonDate(startDate);
		}
		if (endDate != null) {
			nd = DateCalendarUtils.parseJsonDate(endDate);
			nd = DateCalendarUtils.addOneDayToDate(nd);
		}
		AuditActionType actionType = null;
		if (action != null && !action.equals(AuditLogConstants.SHOW_ALL_ACTION)) {
			try {
				actionType = AuditActionType.valueOf(action);
			} catch (Exception ex) {
				logger.error("AdminController.getAuditlog - invalid action type:"
						+ action);
			}
		}
		return fetchAuditLogData(user, actionType, st, nd);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/errorlog")
	public List<ErrorDetailInfo> getErrorlog(
			@RequestParam(value = "code", required = false) String code,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate)
			throws ParseException {
		Date st = null;
		Date nd = null;
		if (startDate != null) {
			st = DateCalendarUtils.parseJsonDate(startDate);
		}
		if (endDate != null) {
			nd = DateCalendarUtils.parseJsonDate(endDate);
			nd = DateCalendarUtils.addOneDayToDate(nd);
		}
		return fetchErrorLogData(code, st, nd);
	}
	@RequestMapping(method = RequestMethod.GET, value = "/settingsinfo")
	public SettingsVo getSettings() {
		final String MATCHING_THRESHOLD = "MATCHING_THRESHOLD";
		final String FLIGHT_RANGE ="FLIGHT_RANGE";
		SettingsVo settingsVo = new SettingsVo();
		settingsVo.setMatchingThreshold(Double.parseDouble(appConfigRepository.findByOption(MATCHING_THRESHOLD).getValue()));
		settingsVo.setFlightRange(Double.parseDouble(appConfigRepository.findByOption(FLIGHT_RANGE).getValue()));
		return settingsVo; 
	}
	@RequestMapping(method = RequestMethod.PUT, value = "/settingsinfo")
	public ResponseEntity setSettings(@Valid SettingsVo settings, BindingResult result, Model model) {
		if(result.hasErrors()) {
			List<String> errors = result.getAllErrors().stream()
					.map(DefaultMessageSourceResolvable:: getDefaultMessage)
					.collect(Collectors.toList());
			return new ResponseEntity<>(errors, HttpStatus.EXPECTATION_FAILED);
		} else {
			final String MATCHING = "MATCHING_THRESHOLD";
			final String FLIGHT_RANGE ="FLIGHT_RANGE";
			AppConfiguration appConfig;
			
			appConfig = appConfigRepository.findByOption(MATCHING);
			appConfig.setValue(String.valueOf(settings.getMatchingThreshold()));
			appConfigRepository.save(appConfig);
			
			appConfig = appConfigRepository.findByOption(FLIGHT_RANGE);
			appConfig.setValue(String.valueOf(settings.getFlightRange()));
			appConfigRepository.save(appConfig);
			return new ResponseEntity<>(HttpStatus.CREATED);
		}
	}
	@RequestMapping(method = RequestMethod.GET, value = "/auditlog/{startDate}/{endDate}")
	public List<AuditRecordVo> getAuditlog(@PathVariable String startDate,
			@PathVariable String endDate) throws ParseException {
		Date stdt = StringUtils.isEmpty(startDate) ? null : DateCalendarUtils
				.parseJsonDate(startDate);
		Date endt = StringUtils.isEmpty(endDate) ? null : DateCalendarUtils
				.parseJsonDate(endDate);
		return fetchAuditLogData(stdt, endt);
	}

	private List<AuditRecordVo> fetchAuditLogData(Date startDate, Date endDate) {
		return fetchAuditLogData(null, null, startDate, endDate);
	}

	private List<AuditRecordVo> fetchAuditLogData(String userId,
			AuditActionType action, Date startDate, Date endDate) {
		Date from = startDate;
		Date to = endDate;
		if (from == null && StringUtils.isEmpty(userId) && action == null) {
			logger.debug("AdminController: fetchAuditLogData - start date is null, using current date.");
			from = DateCalendarUtils.stripTime(new Date());
		}

		if (from != null && to == null) {
			logger.debug("AdminController: fetchAuditLogData - end date is null, using current date.");
			to = DateCalendarUtils.addOneDayToDate(DateCalendarUtils
					.stripTime(new Date()));
		}
		List<AuditRecord> res = auditService.findByUserActionDateRange(userId,
				action, from, to);
		List<AuditRecordVo> ret = new LinkedList<AuditRecordVo>();
		if (res != null) {
			res.forEach(ar -> ret.add(new AuditRecordVo(ar)));
		}
		return ret;
	}

	private List<ErrorDetailInfo> fetchErrorLogData(String code,
			Date startDate, Date endDate) {
		Date from = startDate;
		Date to = endDate;
		if (from == null && StringUtils.isEmpty(code)) {
			logger.debug("AdminController: fetchErrorLogData - start date is null, using current date.");
			from = DateCalendarUtils.stripTime(new Date());
		}

		if (from != null && to == null) {
			logger.debug("AdminController: fetchErrorLogData - end date is null, using current date.");
			to = DateCalendarUtils.addOneDayToDate(DateCalendarUtils
					.stripTime(new Date()));
		}
		List<ErrorDetailInfo> res = null;
		if (StringUtils.isEmpty(code)) {
			res = errorService.findByDateRange(from, to);
		} else {
			res = errorService.findByCode(code);
		}
		return res;
	}

}
