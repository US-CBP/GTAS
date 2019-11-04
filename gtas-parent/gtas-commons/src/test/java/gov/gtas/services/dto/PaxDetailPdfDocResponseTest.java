package gov.gtas.services.dto;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PaxDetailPdfDocResponseTest {
	
	PaxDetailPdfDocResponse paxDetailPdfDocResponse;
	
	@Before
	public void before() {

		paxDetailPdfDocResponse = new PaxDetailPdfDocResponse();
		paxDetailPdfDocResponse.setReportFileName("FileName1");
		paxDetailPdfDocResponse.setPdfDocumentPath("/tmp");
		paxDetailPdfDocResponse.setFileByteArray(new byte[1]);
	}

	@Test
	public void methodTest()
	{
		Assert.assertNotNull(paxDetailPdfDocResponse);
		Assert.assertEquals("/tmp", paxDetailPdfDocResponse.getPdfDocumentPath());
		Assert.assertEquals("FileName1", paxDetailPdfDocResponse.getReportFileName());
		Assert.assertEquals(1, paxDetailPdfDocResponse.getFileByteArray().length);
	}
	

}
