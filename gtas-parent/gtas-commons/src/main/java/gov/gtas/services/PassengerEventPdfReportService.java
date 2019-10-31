package gov.gtas.services;

import gov.gtas.services.dto.PaxDetailPdfDocResponse;

public interface PassengerEventPdfReportService {
	
	public PaxDetailPdfDocResponse createPassengerEventReport(Long paxId, Long flightId);
	
	
	

}
