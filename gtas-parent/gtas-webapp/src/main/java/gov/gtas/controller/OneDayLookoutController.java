package gov.gtas.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import gov.gtas.services.CaseDispositionService;
import gov.gtas.vo.OneDayLookoutVo;

@RestController
public class OneDayLookoutController {

	private static final Logger logger = LoggerFactory.getLogger(OneDayLookoutController.class);

	@Autowired
	private CaseDispositionService caseDispositionService;

	@RequestMapping(value = "/onedaylookout", method = RequestMethod.GET)
	public @ResponseBody List<OneDayLookoutVo> getOneDayLookout(
			@RequestParam(value = "flightDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String flightDate) {

		logger.debug("In One Day Lookout");

		List<OneDayLookoutVo> OneDayLookoutVoList = new ArrayList<OneDayLookoutVo>();

		if (flightDate != null) {
			try {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date etaEtdDate = dateFormat.parse(flightDate);
				OneDayLookoutVoList = caseDispositionService.getOneDayLookoutByDate(etaEtdDate);
			} catch (Exception e) {

			}
		} else {
			Date today = Calendar.getInstance().getTime();
			OneDayLookoutVoList = caseDispositionService.getOneDayLookoutByDate(today);
		}
		
		return OneDayLookoutVoList;
	}



}
