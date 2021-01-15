package gov.gtas.services;

import gov.gtas.services.dto.PaxDetailPdfDocRequest;
import gov.gtas.services.dto.PaxDetailPdfDocResponse;
import gov.gtas.vo.HitDetailVo;
import gov.gtas.vo.NoteTypeVo;
import gov.gtas.vo.NoteVo;
import gov.gtas.vo.passenger.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;

import java.util.*;

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

	@Test
	public void createPaxDetailReportTest() throws Exception {
		PaxDetailPdfDocRequest paxDetailPdfDocRequest = new PaxDetailPdfDocRequest();
		PassengerVo wally = TestData.getPassengerVo();
		paxDetailPdfDocRequest.setPassengerVo(wally);
		LinkedHashSet<NoteVo> noteVoList = new LinkedHashSet<>();

		HitDetailVo hitDetailVo = TestData.getHitDetailVo();

		LinkedHashSet<HitDetailVo> hitDetailVoList = new LinkedHashSet<>();
		hitDetailVoList.add(hitDetailVo);
		paxDetailPdfDocRequest.setHitDetailVoList(hitDetailVoList);

		List<HitDetailVo> hitDetailVos = new ArrayList<>(hitDetailVoList);
		paxDetailPdfDocRequest.setHitDetailHistoryVoList(hitDetailVos);

		List<FlightVoForFlightHistory> flightHistoryVos = TestData.getFlightVoForFlightHistories();

		paxDetailPdfDocRequest.setFlightHistoryVoList(flightHistoryVos);

		List<EmailVo> emailVos = TestData.getEmailVos();

		List<PhoneVo> phoneVos = TestData.getPhoneVos();

		PnrVo pnrVo = new PnrVo();

		pnrVo.setRaw("Fooey");
		pnrVo.setPhoneNumbers(phoneVos);
		pnrVo.setEmails(emailVos);

		List<DocumentVo> documentVos = new ArrayList<>();
		DocumentVo fakeDoc = TestData.getDocumentVo();
		documentVos.add(fakeDoc);

		List<PassengerVo> passengerVos = new ArrayList<>();
		passengerVos.add(wally);
		pnrVo.setPassengers(passengerVos);

		paxDetailPdfDocRequest.setAlert("This is a bad dog!");

		List<CreditCardVo> creditCardVos = TestData.getCreditCardVos();

		wally.setDocuments(documentVos);

		pnrVo.setCreditCards(creditCardVos);
		wally.setPnrVo(pnrVo);

		getNoteVoHashSet(noteVoList);

		paxDetailPdfDocRequest.setEventNotesSet(noteVoList);
		
		Map<String,String> translationValues = new HashMap<String,String>();

		paxDetailPdfDocRequest.setTranslationValues(translationValues);
		paxDetailPdfDocRequest.setEventHistoricalNotesSet(noteVoList);
		PaxDetailPdfDocResponse pdPDFDocR = eventReportPdfService.createPaxDetailReport(paxDetailPdfDocRequest);
		Assert.assertNotNull(pdPDFDocR);
	}

	protected void getNoteVoHashSet(LinkedHashSet<NoteVo> noteVoList) {
		NoteVo noteVo = new NoteVo();
		noteVo.setPlainTextNote("Silly FooBar!");
		Set<NoteTypeVo> noteTypeVos = new HashSet<>();
		NoteTypeVo noteTypeVo = new NoteTypeVo();
		noteTypeVo.setId(1L);
		noteTypeVo.setNoteType("Tester");
		noteTypeVos.add(noteTypeVo);
		noteVo.setNoteTypeVoSet(noteTypeVos);
		noteVoList.add(noteVo);
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
	

	@Test
	public void createPnrValueCellTest() {
		
		try {
			String pnrTestString = "UNA:+.?*'UNB+IATB";
			document.addPage(passengerDetailPage);
			BaseTable table = eventReportPdfService.createReportCoverlTable(document, coverPage);
			Row<PDPage> row = eventReportPdfService.getRow(table);
			Cell<PDPage> cell = eventReportPdfService.createRawPnrValueCell(row, 100, pnrTestString);
			float expected[] = { eventReportPdfService.DEFAULT_CONTENT_FONT_SIZE };
			float actual[] = { cell.getFontSize()};
			Assert.assertArrayEquals(expected,actual, 0);
			Assert.assertEquals(cell.getText(), pnrTestString);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void createValueCellWithRighBottomtBorderTest() {
		
		try {
			
			int THIRD_COLUMN_WIDTH = 30;
			PassengerVo passengerVo = new PassengerVo();
			passengerVo.setFirstName("First");
			passengerVo.setMiddleName("Middle");
			passengerVo.setLastName("Last");
			document.addPage(passengerDetailPage);
			BaseTable table = eventReportPdfService.createReportCoverlTable(document, coverPage);
			Row<PDPage> row = eventReportPdfService.getRow(table);
			Cell<PDPage> cell = eventReportPdfService.createValueCellWithRighBottomtBorder(row, THIRD_COLUMN_WIDTH,
					passengerVo.getLastName() + ", " + passengerVo.getFirstName() + ", " + passengerVo.getMiddleName());
			String cellText = cell.getText();
			String []name = cellText.split(",");
			Assert.assertEquals(name.length,3);
			Assert.assertEquals(name[2].trim(),"Middle");
			
			passengerVo.setFirstName("First");
			passengerVo.setLastName("Last");
			cell = eventReportPdfService.createValueCellWithRighBottomtBorder(row, THIRD_COLUMN_WIDTH,
					passengerVo.getLastName() + ", " + passengerVo.getFirstName() );
			String cellText2 = cell.getText();
			String []name2 = cellText2.split(",");
			Assert.assertEquals(name2.length,2);
		
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void createPassengerFirstVerticalColumnValueCellTest()
	{
		
		try {
			
			int THIRD_COLUMN_WIDTH = 30;
			PassengerVo passengerVo = new PassengerVo();
			passengerVo.setFirstName("First");
			passengerVo.setMiddleName("Middle");
			passengerVo.setLastName("Last");
			document.addPage(passengerDetailPage);
			
			BaseTable table = eventReportPdfService.createReportCoverlTable(document, coverPage);
			Row<PDPage> row = eventReportPdfService.getRow(table);
			Cell<PDPage> cell = eventReportPdfService.createPassengerFirstVerticalColumnValueCell(row, THIRD_COLUMN_WIDTH,
					passengerVo.getLastName() + ", " + passengerVo.getFirstName() + ", " + passengerVo.getMiddleName());
			String cellText = cell.getText();
			String []name = cellText.split(",");
			Assert.assertEquals(name.length,3);
			Assert.assertEquals(name[2].trim(),"Middle");
			
			passengerVo.setFirstName("First");
			passengerVo.setLastName("Last");
			cell = eventReportPdfService.createValueCellWithRighBottomtBorder(row, THIRD_COLUMN_WIDTH,
					passengerVo.getLastName() + ", " + passengerVo.getFirstName() );
			String cellText2 = cell.getText();
			String []name2 = cellText2.split(",");
			Assert.assertEquals(name2.length,2);
		
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	@Test
	public void setLanguageTranslationValuesTest() {
		
		try {
			PaxDetailPdfDocRequest paxDetailPdfDocRequest = new PaxDetailPdfDocRequest();
			Map<String,String> languageTransValues = new HashMap<String,String>();
			languageTransValues.put("en001", "Hello");
			paxDetailPdfDocRequest.setTranslationValues(languageTransValues);
			Assert.assertNotNull(paxDetailPdfDocRequest.getTranslationValues());
			Assert.assertEquals("Hello", paxDetailPdfDocRequest.getTranslationValues().get("en001") );
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	
	

}
