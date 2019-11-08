package gov.gtas.services.dto;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



public class PaxDetailPdfDocRequestTest {
	
	
	PaxDetailPdfDocRequest paxDetailPdfDocRequest;
	
	@Before
	public void before() {

		paxDetailPdfDocRequest = new PaxDetailPdfDocRequest();
		paxDetailPdfDocRequest.setAlert("ArmedAndDangerous");
		paxDetailPdfDocRequest.setPdfFileName("file1");
		paxDetailPdfDocRequest.setHighestSeverity("Normal");
	}

	@Test
	public void methodTest()
	{
		Assert.assertNotNull(paxDetailPdfDocRequest);
		Assert.assertEquals("ArmedAndDangerous", paxDetailPdfDocRequest.getAlert());
		Assert.assertEquals("file1", paxDetailPdfDocRequest.getPdfFileName());
		Assert.assertEquals("Normal", paxDetailPdfDocRequest.getHighestSeverity());
	}

}
