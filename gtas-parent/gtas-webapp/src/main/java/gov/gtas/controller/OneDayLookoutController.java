package gov.gtas.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import gov.gtas.constants.Constants;
import gov.gtas.model.Case;
import gov.gtas.security.service.GtasSecurityUtils;
import gov.gtas.services.CaseDispositionService;
import gov.gtas.services.security.UserService;
import gov.gtas.vo.OneDayLookoutVo;

@RestController
public class OneDayLookoutController {

	private static final Logger logger = LoggerFactory.getLogger(OneDayLookoutController.class);

	@Autowired
	private CaseDispositionService caseDispositionService;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/onedaylookout", method = RequestMethod.GET)
	public @ResponseBody List<OneDayLookoutVo> getOneDayLookout(HttpServletRequest httpServletRequest,
			@RequestParam(value = "flightDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String flightDate) {

		List<OneDayLookoutVo> OneDayLookoutVoList = new ArrayList<OneDayLookoutVo>();

		String userId = GtasSecurityUtils.fetchLoggedInUserId();
		boolean isAdmin = userService.isAdminUser(userId);

		if (isAdmin) {

			try {
				if (flightDate != null) {

					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					Date etaEtdDate = dateFormat.parse(flightDate);
					OneDayLookoutVoList = caseDispositionService.getOneDayLookoutByDate(etaEtdDate);
				} else {
					Date today = Calendar.getInstance().getTime();
					OneDayLookoutVoList = caseDispositionService.getOneDayLookoutByDate(today);
				}
			} catch (Exception e) {
				logger.error("An Exception has occurred when reading one day lookout information", e);
			}

		} else {

		
			String userLocation = (String) httpServletRequest.getSession(true)
					.getAttribute(Constants.USER_PRIMARY_LOCATION);

			if (userLocation != null && !userLocation.trim().isEmpty()) {
				try {

					if (flightDate != null) {

						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						Date etaEtdDate = dateFormat.parse(flightDate);
						OneDayLookoutVoList = caseDispositionService.getOneDayLookoutByDateAndAirport(etaEtdDate,
								userLocation);

					} else {
						Date today = Calendar.getInstance().getTime();
						OneDayLookoutVoList = caseDispositionService.getOneDayLookoutByDateAndAirport(today,
								userLocation);
					}

				} catch (Exception e) {
					logger.error(
							"An Exception has occurred when reading one day lookout information by date and airport",
							e);
				}

			}

			else {
				logger.error(
						"A User Location could not be found in the session therefore One Day Lookoutlist can not be displayed for this user."
								+ this.getClass().getName());
			}

		}

		return OneDayLookoutVoList;
	}

	@RequestMapping(value = "/addonedaylookout", method = RequestMethod.GET)
	public @ResponseBody boolean addToOneDayLookout(@RequestParam(value = "caseId", required = true) String caseId) {

		logger.debug("..Case ID to be added:  " + caseId);
		boolean result = false;

		if (caseId != null) {
			try {
				Long caseIdAsLong = Long.valueOf(caseId);
				result = caseDispositionService.updateDayLookoutFlag(caseIdAsLong, Boolean.TRUE);

			} catch (Exception e) {
				logger.error("An Exception has occurred when adding flagging a case as a one day lookout data", e);
				result = false;

			}

		}

		return result;
	}

	@RequestMapping(value = "/removeonedaylookout", method = RequestMethod.GET)
	public @ResponseBody boolean removeFromonedaylookout(
			@RequestParam(value = "caseId", required = true) String caseId) {

		boolean result = false;
		logger.info("..Case ID to be removed: " + caseId);

		if (caseId != null) {
			try {
				Long caseIdAsLong = Long.valueOf(caseId);

				result = caseDispositionService.updateDayLookoutFlag(caseIdAsLong, null);

			} catch (Exception e) {
				logger.error("An Exception has occurred when removing flagging a case as a one day lookout data", e);
				result = false;

			}

		}

		return result;
	}

}
