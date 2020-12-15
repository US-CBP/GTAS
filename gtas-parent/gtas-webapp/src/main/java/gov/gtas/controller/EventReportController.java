/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import gov.gtas.services.EventReportService;
import gov.gtas.services.dto.PaxDetailPdfDocResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
public class EventReportController {

	private static final Logger logger = LoggerFactory.getLogger(EventReportController.class);

	private EventReportService passengerEventReportService;

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/paxdetailreport", method = RequestMethod.GET)
	public @ResponseBody byte[] getPaxDetailReportByPaxId(
			@RequestParam(value = "flightId", required = true) String flightId,
			@RequestParam(value = "paxId", required = true) String paxId) throws IOException {

		PaxDetailPdfDocResponse paxDetailPdfDocResponse = new PaxDetailPdfDocResponse();
		try {
			logger.info("Generating Event Report for : Flight Id: " + flightId + ", PassengerId: " + paxId + "  "
					+ this.getClass().getName());
			Long pax_id = Long.parseLong(paxId);
			Long flight_id = Long.parseLong(flightId);
			paxDetailPdfDocResponse = passengerEventReportService.createPassengerEventReport(pax_id, flight_id);
		} catch (NumberFormatException e) {
			logger.error("The Flight Id or Passenger Id is not a number", e);
		} catch (Exception e) {
			logger.error("An Error has occurred when generating GTAS Event Report",e );
		}
		return paxDetailPdfDocResponse.getFileByteArray();
	}

	@Autowired
	public void setPassengerEventReportService(EventReportService passengerEventReportService) {
		this.passengerEventReportService = passengerEventReportService;
	}

}
