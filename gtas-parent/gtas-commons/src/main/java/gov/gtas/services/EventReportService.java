package gov.gtas.services;

import gov.gtas.services.dto.PaxDetailPdfDocResponse;

public interface EventReportService {
	
	public PaxDetailPdfDocResponse createPassengerEventReport(Long paxId, Long flightId, String language);
	
	
	

}
