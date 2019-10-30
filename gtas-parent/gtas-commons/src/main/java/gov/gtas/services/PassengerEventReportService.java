package gov.gtas.services;

import gov.gtas.services.dto.PaxDetailPdfDocResponse;

public interface PassengerEventReportService {
	
	public PaxDetailPdfDocResponse createPassengerEventReport(Long paxId, Long flightId);
	
	
	

}
