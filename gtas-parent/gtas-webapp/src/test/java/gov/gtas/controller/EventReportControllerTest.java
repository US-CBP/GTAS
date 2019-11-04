package gov.gtas.controller;

import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import gov.gtas.services.EventReportService;
import gov.gtas.services.dto.PaxDetailPdfDocResponse;

@RunWith(MockitoJUnitRunner.class)
public class EventReportControllerTest {

	@Mock
	EventReportService eventReportService;

	@InjectMocks
	EventReportController eventReportController;

	@Before
	public void before() {
		initMocks(this);
		PaxDetailPdfDocResponse paxDetailPdfDocResponse = new PaxDetailPdfDocResponse();
		paxDetailPdfDocResponse.setFileByteArray(new byte[1]);
		long paxId = 1L, flightId =2L;
		long paxId2= 10L, flightId2= 20L;
		Mockito.when(eventReportService.createPassengerEventReport(paxId, flightId)).thenReturn(paxDetailPdfDocResponse);
		Mockito.when(eventReportService.createPassengerEventReport(paxId2, flightId2)).thenReturn(new PaxDetailPdfDocResponse());
	
	}

	@Test
	public void getPaxDetailReportByPaxIdTest() {
		
		String paxIdStr = "1";
		String flightIdStr = "2";
		try {
			byte[] resultByte = eventReportController.getPaxDetailReportByPaxId(flightIdStr,paxIdStr);
			Assert.assertEquals(1,resultByte.length);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	@Test
	public void getPaxDetailReportByPaxId2Test() {
		
		String paxIdStr = "10";
		String flightIdStr = "20";
		try {
			byte[] resultByte = eventReportController.getPaxDetailReportByPaxId(flightIdStr,paxIdStr);
			Assert.assertNull(resultByte);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	

}
