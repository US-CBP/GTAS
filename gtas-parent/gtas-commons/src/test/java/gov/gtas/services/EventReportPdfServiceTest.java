package gov.gtas.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;

@RunWith(MockitoJUnitRunner.class)
public class EventReportPdfServiceTest {

	@InjectMocks
	EventReportPdfServiceImpl eventReportPdfService;

	PDPage coverPage;
	PDPage passengerDetailPage;
	PDDocument document;

	@Before
	public void before() {

		document= new PDDocument();
		//cover page
		coverPage = new PDPage(PDRectangle.A4);
		//passenger detail page
		passengerDetailPage = new PDPage(PDRectangle.A4);
	
	}

	/** Tests cover page table creation */
	@Test
	public void createReportCoverlTableTest() {

		try {
			document.addPage(coverPage);
			BaseTable table = eventReportPdfService.createReportCoverlTable(document, coverPage);
			// check table margin size
			float expected[] = { eventReportPdfService.REPORT_COVER_MARGIN };
			float actual[] = { table.getMargin() };
			Assert.assertArrayEquals(expected, actual, 0);
			// check table width
			float expected2[] = { coverPage.getMediaBox().getWidth() - (2 * eventReportPdfService.REPORT_COVER_MARGIN) };
			float actual2[] = { table.getWidth() };
			Assert.assertArrayEquals(expected2, actual2, 0);
			// check that the cover page exists
			Assert.assertEquals(1,document.getNumberOfPages());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/** Tests passenger detail table creation */
	@Test
	public void createPassengerDetailTableTest() {

		try {
			document.addPage(passengerDetailPage);
			BaseTable table = eventReportPdfService.createReportCoverlTable(document, coverPage);
			// check table margin size
			float expected[] = { eventReportPdfService.DEFAULT_MARGIN };
			float actual[] = { table.getMargin() };
			Assert.assertArrayEquals(expected, actual, 0);
			// check table width
			float expected2[] = { passengerDetailPage.getMediaBox().getWidth() - (2 * eventReportPdfService.DEFAULT_MARGIN) };
			float actual2[] = { table.getWidth() };
			Assert.assertArrayEquals(expected2, actual2, 0);
			// check that the  passenger detail page exists
			Assert.assertEquals(1,document.getNumberOfPages());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/** NOTE:
	 * The following methods test the inherited methods from 
	 * the Abstract PassengerPdfTemplateService class.
	 * **/
	
	@Test
	public void createCellTest() {
		
		try {
			document.addPage(passengerDetailPage);
			BaseTable table = eventReportPdfService.createReportCoverlTable(document, coverPage);
			Row<PDPage> row = eventReportPdfService.getRow(table);
			Cell<PDPage> cell = eventReportPdfService.createCell(row, 10, "RowValue1");
			float expected[] = { eventReportPdfService.DEFAULT_CONTENT_FONT_SIZE };
			float actual[] = { cell.getFontSize()};
			Assert.assertArrayEquals(expected,actual, 0);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	@Test
	public void createPassengerTopBorderCellTest() {
		
		try {
			document.addPage(passengerDetailPage);
			BaseTable table = eventReportPdfService.createReportCoverlTable(document, coverPage);
			Row<PDPage> row = eventReportPdfService.getRow(table);
			Cell<PDPage> cell = eventReportPdfService.createPassengerTopBorderCell(row, 10, "RowValue1");
			
			Assert.assertEquals(HorizontalAlignment.CENTER.name(),cell.getAlign().name());
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	
	
	
	
	

}
