package gov.gtas.controller;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import gov.gtas.services.PassengerEventPdfReportService;
import gov.gtas.services.dto.PaxDetailPdfDocResponse;

@Controller
public class PaxEventReportController {

	private static final Logger logger = LoggerFactory.getLogger(PaxEventReportController.class);

	private PassengerEventPdfReportService passengerEventReportService;

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/paxdetailreport", method = RequestMethod.GET)
	public @ResponseBody byte[] getPaxDetailReportByPaxId(
			@RequestParam(value = "flightId", required = true) String flightId,
			@RequestParam(value = "paxId", required = true) String paxId) throws IOException {

		PaxDetailPdfDocResponse paxDetailPdfDocResponse = null;

		try {
			logger.info("Generating Event Report for : Flight Id: " + flightId + ", PassengerId: " + paxId + "  "
					+ this.getClass().getName());
			Long pax_id = Long.parseLong(paxId);
			Long flight_id = Long.parseLong(flightId);
			paxDetailPdfDocResponse = passengerEventReportService.createPassengerEventReport(pax_id, flight_id);
		} catch (NumberFormatException e) {
			logger.error("The Flight Id or Passenger Id is not a number");
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("An Error has occurred when generating GTAS Event Report");
			e.printStackTrace();
		}
		return paxDetailPdfDocResponse.getFileByteArray();
	}

	@Autowired
	public void setPassengerEventReportService(PassengerEventPdfReportService passengerEventReportService) {
		this.passengerEventReportService = passengerEventReportService;
	}

}
