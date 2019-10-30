package gov.gtas.controller;

import java.util.ArrayList;
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

import gov.gtas.common.UserLocationSetting;
import gov.gtas.services.security.UserService;
import gov.gtas.vo.passenger.OneDayLookoutVo;

@RestController
public class OneDayLookoutController {

	private static final Logger logger = LoggerFactory.getLogger(OneDayLookoutController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private UserLocationSetting userLocationSetting;

	@RequestMapping(value = "/onedaylookout", method = RequestMethod.GET)
	public @ResponseBody List<OneDayLookoutVo> getOneDayLookout(HttpServletRequest httpServletRequest,
			@RequestParam(value = "flightDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String flightDate) {

		return new ArrayList<>();
	}

	@RequestMapping(value = "/addonedaylookout", method = RequestMethod.GET)
	public @ResponseBody boolean addToOneDayLookout(@RequestParam(value = "caseId", required = true) String caseId) {

		return true;
	}

	@RequestMapping(value = "/removeonedaylookout", method = RequestMethod.GET)
	public @ResponseBody boolean removeFromonedaylookout(
			@RequestParam(value = "caseId", required = true) String caseId) {

		return true;
	}

	@RequestMapping(value = "/encounteredstatus", method = RequestMethod.POST)
	public @ResponseBody void updateEncounteredStatus(@RequestParam(value = "caseId", required = true) Long caseId,
			@RequestParam(value = "newStatus", required = true) String newStatus) {
	}

}
