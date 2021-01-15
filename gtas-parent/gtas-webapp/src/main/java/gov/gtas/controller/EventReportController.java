/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import java.io.IOException;
import java.util.Objects;

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
import gov.gtas.services.EventReportService;
import gov.gtas.services.dto.PaxDetailPdfDocResponse;

@Controller
public class EventReportController {

	private static final Logger logger = LoggerFactory.getLogger(EventReportController.class);

	private EventReportService passengerEventReportService;
	private static final String DEFAULT_LANGUAGE = "en";

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/paxdetailreport", method = RequestMethod.GET)
	public @ResponseBody byte[] getPaxDetailReportByPaxId(
			@RequestParam(value = "flightId", required = true) String flightId,
			@RequestParam(value = "paxId", required = true) String paxId,
			@RequestParam(value = "language", required = false) String language)throws IOException {

		PaxDetailPdfDocResponse paxDetailPdfDocResponse = new PaxDetailPdfDocResponse();
		try {
			logger.info("Generating Event Report for : Flight Id: " + flightId + ", PassengerId: " + paxId + "  "
					+ this.getClass().getName());
			Long pax_id = Long.parseLong(paxId);
			Long flight_id = Long.parseLong(flightId);
			
			String selectedLanguage = language;
			if(selectedLanguage==null || selectedLanguage.isEmpty())
			{
				selectedLanguage = DEFAULT_LANGUAGE;
			}
			logger.info("The language setting for the Passenger Event Report is " + selectedLanguage);
			paxDetailPdfDocResponse = passengerEventReportService.createPassengerEventReport(pax_id, flight_id,selectedLanguage);
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
