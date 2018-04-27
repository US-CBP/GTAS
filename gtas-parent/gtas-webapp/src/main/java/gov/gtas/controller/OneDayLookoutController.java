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

		logger.info("In One Day Lookout");

		List<Case> oneDayLookoutList = null;
		List<OneDayLookoutVo> OneDayLookoutVoList = new ArrayList<OneDayLookoutVo>();

		if (flightDate != null) {
			try {
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date etaEtdDate = dateFormat.parse(flightDate);
				oneDayLookoutList = caseDispositionService.getOneDayLookoutByDate(etaEtdDate);
			} catch (Exception e) {

			}
		} else {
			Date today = Calendar.getInstance().getTime();
			oneDayLookoutList = caseDispositionService.getOneDayLookoutByDate(today);
		}
		
		if (oneDayLookoutList != null) {
			OneDayLookoutVoList = getOneDaylookoutMapped(oneDayLookoutList);
		}

		return OneDayLookoutVoList;
	}

	private List<OneDayLookoutVo> getOneDaylookoutMapped(List<Case> oneDayLookoutList) {

		List<OneDayLookoutVo> oneDayLookoutVoList = new ArrayList<OneDayLookoutVo>();

		OneDayLookoutVo oneDayLookoutVo = new OneDayLookoutVo();
		String etaEtdTime = null;
		Date etaEtdDate = null;
		Calendar calendar = null;
		for (Case oneDayLookoutCase : oneDayLookoutList) {
			etaEtdTime = null;
			calendar = null;
			oneDayLookoutVo = new OneDayLookoutVo();
			oneDayLookoutVo.setDocument(oneDayLookoutCase.getDocument());
			oneDayLookoutVo.setFirstName(oneDayLookoutCase.getFirstName());
			oneDayLookoutVo.setLastName(oneDayLookoutCase.getLastName());
			oneDayLookoutVo.setName(oneDayLookoutCase.getLastName() + ", " + oneDayLookoutCase.getFirstName());

			// set flight information
			if (oneDayLookoutCase.getFlight() != null) {
				oneDayLookoutVo.setFlightNumber(oneDayLookoutCase.getFlight().getFlightNumber());
				oneDayLookoutVo.setFullFlightNumber(oneDayLookoutCase.getFlight().getFullFlightNumber());

				if (oneDayLookoutCase.getFlight().getDirection() != null) {

					// set eta/etd time and direction
					if (oneDayLookoutCase.getFlight().getDirection()
							.equalsIgnoreCase(Constants.FLIGHT_DIRECTION_INCOMING)) {
						oneDayLookoutVo.setDirection(Constants.FLIGHT_DIRECTION_INCOMING_DESC);
						etaEtdDate = oneDayLookoutCase.getFlight().getEta();
						if (etaEtdDate != null) {
							calendar = Calendar.getInstance();
							calendar.setTime(etaEtdDate);
							etaEtdTime = String.format("%02d", Integer.valueOf(calendar.get(Calendar.HOUR_OF_DAY)))
									+ String.format("%02d", Integer.valueOf(calendar.get(Calendar.MINUTE)));
							oneDayLookoutVo.setEtaEtdTime(etaEtdTime);
						}

					} else if (oneDayLookoutCase.getFlight().getDirection()
							.equalsIgnoreCase(Constants.FLIGHT_DIRECTION_OUTGOING)) {
						oneDayLookoutVo.setDirection(Constants.FLIGHT_DIRECTION_OUTGOING_DESC);
						etaEtdDate = oneDayLookoutCase.getFlight().getEtd();
						calendar = Calendar.getInstance();
						calendar.setTime(etaEtdDate);
						etaEtdTime = String.format("%02d", Integer.valueOf(calendar.get(Calendar.HOUR_OF_DAY)))
								+ String.format("%02d", Integer.valueOf(calendar.get(Calendar.MINUTE)));
						oneDayLookoutVo.setEtaEtdTime(etaEtdTime);
					}

				}
			}
			oneDayLookoutVoList.add(oneDayLookoutVo);
		}

		return oneDayLookoutVoList;
	}

}
