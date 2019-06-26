/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import gov.gtas.constant.AuditLogConstants;
import gov.gtas.enumtype.AuditActionType;
import gov.gtas.error.ErrorDetailInfo;
import gov.gtas.model.ApiAccess;
import gov.gtas.model.AuditRecord;
import gov.gtas.model.lookup.Carrier;
import gov.gtas.model.lookup.Country;
import gov.gtas.model.lookup.Airport;
import gov.gtas.model.lookup.AppConfiguration;
import gov.gtas.services.AppConfigurationService;
import gov.gtas.services.ApiAccessService;
import gov.gtas.services.AuditLogPersistenceService;
import gov.gtas.services.ErrorPersistenceService;
import gov.gtas.services.CarrierService;
import gov.gtas.services.AirportService;
import gov.gtas.services.CountryService;
import gov.gtas.util.DateCalendarUtils;
import gov.gtas.vo.AuditRecordVo;
import gov.gtas.vo.SettingsVo;

import java.text.ParseException;
import java.util.Date;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static gov.gtas.repository.AppConfigurationRepository.MAX_FLIGHT_QUERY_RESULT;
import static gov.gtas.repository.AppConfigurationRepository.MAX_PASSENGER_QUERY_RESULT;

/**
 * Back-end REST service interface to support audit/error log viewing and
 * configuration management.
 */
@RestController
public class AdminController {

	private static final Logger logger = LoggerFactory
			.getLogger(AdminController.class);

        public static final String MATCHING_THRESHOLD = "MATCHING_THRESHOLD";
        public static final String FLIGHT_RANGE ="FLIGHT_RANGE";
        public static final String APIS_ONLY_FLAG = "APIS_ONLY_FLAG";
        public static final String APIS_VERSION = "APIS_VERSION";
        public static final String MAX_RULE_HITS = "MAX_RULE_HITS";

  @Autowired
  AppConfigurationService appConfigurationService;

	@Autowired
	private AuditLogPersistenceService auditService;

	@Autowired
	private ErrorPersistenceService errorService;

	@Autowired
  private ApiAccessService apiAccessService;
  
	@Autowired
  private CarrierService carrierService;
  
	@Autowired
  private CountryService countryService;
  
	@Autowired
  private AirportService airportService;

  //carrier
	@RequestMapping(method = RequestMethod.GET, value = "/api/carrier")
	public List<Carrier> getAllCarrier(){
		return carrierService.findAll();
	}
	@RequestMapping(method = RequestMethod.POST, value = "/api/carrier", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Carrier createCarrier(@RequestBody @Valid Carrier carrier) {
		return carrierService.create(carrier);
	}
	@RequestMapping(method = RequestMethod.PUT, value = "/api/carrier", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Carrier updateCarrier(@RequestBody @Valid Carrier carrier) {
		return carrierService.update(carrier);
	}
	@RequestMapping(method = RequestMethod.DELETE, value = "/api/carrier/{id}")
	public Carrier deleteCarrier(@PathVariable Long id) {
		return carrierService.delete(id);
	}
	@RequestMapping(method = RequestMethod.PUT, value = "/api/carrier/restore")
	public Carrier restoreCarrier(@RequestBody @Valid Carrier carrier) {
		return carrierService.restore(carrier);
	}
	@RequestMapping(method = RequestMethod.PUT, value = "/api/carrier/restoreAll")
	public int restoreAllCarrier() {
		return carrierService.restoreAll();
	}

  //country
	@RequestMapping(method = RequestMethod.GET, value = "/api/country")
	public List<Country> getAllCountry(){
		return countryService.findAll();
	}
	@RequestMapping(method = RequestMethod.POST, value = "/api/country", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Country createCountry(@RequestBody @Valid Country country) {
		return countryService.create(country);
	}
	@RequestMapping(method = RequestMethod.PUT, value = "/api/country", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Country updateCountry(@RequestBody @Valid Country country) {
		return countryService.update(country);
	}
	@RequestMapping(method = RequestMethod.DELETE, value = "/api/country/{id}")
	public Country deleteCountry(@PathVariable Long id) {
		return countryService.delete(id);
	}
	@RequestMapping(method = RequestMethod.PUT, value = "/api/country/restore")
	public Country restoreCountry(@RequestBody @Valid Country country) { return countryService.restore(country); }
	@RequestMapping(method = RequestMethod.PUT, value = "/api/country/restoreAll")
	public int restoreAllCountry() {
		return countryService.restoreAll();
	}

  //airport
	@RequestMapping(method = RequestMethod.GET, value = "/api/airport")
	public List<Airport> getAllAirport(){
		return airportService.findAll();
	}
	@RequestMapping(method = RequestMethod.POST, value = "/api/airport", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Airport createAirport(@RequestBody @Valid Airport airport) {
		return airportService.create(airport);
	}
	@RequestMapping(method = RequestMethod.PUT, value = "/api/airport", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Airport updateAirport(@RequestBody @Valid Airport airport) {
		return airportService.update(airport);
	}
	@RequestMapping(method = RequestMethod.DELETE, value = "/api/airport/{id}")
	public Airport deleteAirport(@PathVariable Long id) {
		return airportService.delete(id);
	}
	@RequestMapping(method = RequestMethod.PUT, value = "/api/airport/restore")
	public Airport restoreAirport(@RequestBody @Valid Airport airport) {
		return airportService.restore(airport);
	}
	@RequestMapping(method = RequestMethod.PUT, value = "/api/airport/restoreAll")
	public int restoreAllAirport() {
		return airportService.restoreAll();
	}

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

	@RequestMapping(method = RequestMethod.GET, value = "/apiAccess")
	public List<ApiAccess> getAllApiAccess(){
		return apiAccessService.findAll();
	}
	@RequestMapping(method = RequestMethod.POST, value = "/apiAccess", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ApiAccess createApiAccess(@RequestBody @Valid ApiAccess apiAccess) {
		return apiAccessService.create(apiAccess);
	}
	@RequestMapping(method = RequestMethod.PUT, value = "/apiAccess", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ApiAccess updateApiAccess(@RequestBody @Valid ApiAccess apiAccess) {
		return apiAccessService.update(apiAccess);
	}
	@RequestMapping(method = RequestMethod.DELETE, value = "/apiAccess/{id}")
	public ApiAccess deleteApiAccess(@PathVariable Long id) {
		return apiAccessService.delete(id);
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

		SettingsVo settingsVo = new SettingsVo();
		settingsVo.setMatchingThreshold(Double.parseDouble(appConfigurationService.findByOption(MATCHING_THRESHOLD).getValue()));
		settingsVo.setFlightRange(Double.parseDouble(appConfigurationService.findByOption(FLIGHT_RANGE).getValue()));
		settingsVo.setMaxPassengerQueryResult(Integer.parseInt(appConfigurationService.findByOption(MAX_PASSENGER_QUERY_RESULT).getValue()));
		settingsVo.setMaxFlightQueryResult(Integer.parseInt(appConfigurationService.findByOption(MAX_FLIGHT_QUERY_RESULT).getValue()));
		settingsVo.setMaxRuleHit(Integer.parseInt(appConfigurationService.findByOption(MAX_RULE_HITS).getValue()));

                AppConfiguration appConfigApisFlag = appConfigurationService.findByOption(APIS_ONLY_FLAG);
                if (appConfigApisFlag != null)
                {
                  settingsVo.setApisOnlyFlag(appConfigApisFlag.getValue());
                }
                AppConfiguration appConfigApisVersion = appConfigurationService.findByOption(APIS_VERSION);
                if (appConfigApisVersion != null)
                {
                  settingsVo.setApisVersion(appConfigApisVersion.getValue());
                }

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

			AppConfiguration appConfig;

			appConfig = appConfigurationService.findByOption(MATCHING_THRESHOLD);
			appConfig.setValue(String.valueOf(settings.getMatchingThreshold()));
			appConfigurationService.save(appConfig);

			appConfig = appConfigurationService.findByOption(FLIGHT_RANGE);
			appConfig.setValue(String.valueOf(settings.getFlightRange()));
			appConfigurationService.save(appConfig);

			appConfig = appConfigurationService.findByOption(MAX_PASSENGER_QUERY_RESULT);
			appConfig.setValue(String.valueOf(settings.getMaxPassengerQueryResult()));
			appConfigurationService.save(appConfig);

			appConfig = appConfigurationService.findByOption(MAX_FLIGHT_QUERY_RESULT);
			appConfig.setValue(String.valueOf(settings.getMaxFlightQueryResult()));
			appConfigurationService.save(appConfig);

			appConfig = appConfigurationService.findByOption(MAX_RULE_HITS);
			appConfig.setValue(String.valueOf(settings.getMaxRuleHit()));
			appConfigurationService.save(appConfig);

			if (settings.getApisOnlyFlag() != null && !settings.getApisOnlyFlag().isEmpty())
                        {
			    appConfig = appConfigurationService.findByOption(APIS_ONLY_FLAG);
                            if (appConfig != null)
                            {
			        appConfig.setValue(String.valueOf(settings.getApisOnlyFlag()));
                                appConfigurationService.save(appConfig);
                            }
                            else
                            {
                              AppConfiguration newAppConfig = new AppConfiguration();
                              newAppConfig.setDescription("Is APIS the only message source.");
                              newAppConfig.setOption(APIS_ONLY_FLAG);
                              newAppConfig.setValue(settings.getApisOnlyFlag());
                              appConfigurationService.save(newAppConfig);
                            }
                        }

                        if (settings.getApisVersion() != null && !settings.getApisVersion().isEmpty())
                        {
			    appConfig = appConfigurationService.findByOption(APIS_VERSION);
                            if (appConfig != null)
                            {
			        appConfig.setValue(String.valueOf(settings.getApisVersion()));
                                appConfigurationService.save(appConfig);
                            }
                            else
                            {
                              AppConfiguration newAppConfig = new AppConfiguration();
                              newAppConfig.setDescription("Latest APIS version being used.");
                              newAppConfig.setOption(APIS_VERSION);
                              newAppConfig.setValue(settings.getApisVersion());
                              appConfigurationService.save(newAppConfig);
                            }
                        }


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
