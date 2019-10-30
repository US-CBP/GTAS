package gov.gtas.controller;

import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import gov.gtas.services.PassengerEventReportService;
import gov.gtas.services.dto.PaxDetailPdfDocResponse;

@Controller
public class PaxDetailReportController {
	
	private static final Logger logger = LoggerFactory.getLogger(PaxDetailReportController.class);
	
	private PassengerEventReportService passengerEventReportService;
	
	
	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/paxdetailreport", method = RequestMethod.GET)
	public @ResponseBody PaxDetailPdfDocResponse getPaxDetailReportByPaxId(@RequestParam(value = "flightId", required = true) String flightId, @RequestParam(value = "paxId", required = true) String paxId) throws IOException {
		
		logger.info("####################### IN getPaxDetailReportByPaxId, paxId = "+ paxId);
		logger.info("####################### IN getPaxDetailReportByPaxId, flightId= " + flightId);
		
		Long pax_id= Long.parseLong(paxId);
		Long flight_id=  Long.parseLong(flightId);
		
		PaxDetailPdfDocResponse paxDetailPdfDocResponse = passengerEventReportService.createPassengerEventReport(pax_id,flight_id);
		
		return paxDetailPdfDocResponse;
	}

	@Autowired
	public void setPassengerEventReportService(PassengerEventReportService passengerEventReportService) {
		this.passengerEventReportService = passengerEventReportService;
	}


	


}
