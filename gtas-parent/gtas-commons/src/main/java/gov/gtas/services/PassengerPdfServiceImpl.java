package gov.gtas.services;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.VerticalAlignment;
import be.quodlibet.boxable.line.LineStyle;
import gov.gtas.services.dto.PaxDetailPdfDocRequest;
import gov.gtas.services.dto.PaxDetailPdfDocResponse;
import gov.gtas.vo.HitDetailVo;
import gov.gtas.vo.NoteTypeVo;
import gov.gtas.vo.NoteVo;
import gov.gtas.vo.passenger.CreditCardVo;
import gov.gtas.vo.passenger.DocumentVo;
import gov.gtas.vo.passenger.EmailVo;
import gov.gtas.vo.passenger.FlightVo;
import gov.gtas.vo.passenger.FlightVoForFlightHistory;
import gov.gtas.vo.passenger.PassengerVo;
import gov.gtas.vo.passenger.PhoneVo;

@Service
public class PassengerPdfServiceImpl extends PassengerPdfTemplateService
		implements PassengerPdfService<PaxDetailPdfDocRequest, PaxDetailPdfDocResponse> {

	private final String REPORT_NAME = "GTAS Event Report";
	private final String PASSENGER_DETAIL_HEADER_TEXT = "Event ID";
	private final String PASSENGER_DETAIL_HEADER_TEXT_2 = "Highest Hit Severity";
	private final String DOCUMENTS_TITLE = "Documents";
	private final String EVENT_HIT_INFORMATION_TITLE = "Event Hit Information";
	private final String HIT_HISTORY_TITLE = "Hit History";
	private final String COTRAVELER_TITLE = "Co-travelers";
	private final String FLIGHT_HISTORY_TITLE = "Flight History";
	private final String CREDIT_CARD_TITLE = "Credit Cards";
	private final String EMAIL_TITLE = "Email Addresses";
	private final String PHONE_TITLE = "Phone Numbers";
	private final String EVENT_NOTES_TITLE = "Event Notes";
	private final String EVENT_NOTE_HISTORY_TITLE = "Event Notes History";
	private BaseTable passengerDetailTable;
	private BaseTable passengerDetailDataTable;
	private BaseTable documentTable;
	private BaseTable eventHitTable;
	private BaseTable hitHistoryTable;
	private BaseTable cotravelerTable;
	private BaseTable flightHistoryTable;
	private BaseTable reportCoverTable;
	private BaseTable creditCardTable;
	private BaseTable emailTable;
	private BaseTable phoneTable;
	private BaseTable eventNotesTable;
	private BaseTable eventNoteHistoryTable;
	private static final Logger logger = LoggerFactory.getLogger(PassengerPdfServiceImpl.class);

	public PaxDetailPdfDocResponse createPaxDetailReport(PaxDetailPdfDocRequest paxDetailPdfDocRequest) throws Exception {


		
      
        
		this.fileName = "GTAS_Event_Report_";
		final String fileExtension = ".pdf";
		//final String fileName
		String fileSeparator = System.getProperty("file.separator");
		String tempDir = System.getProperty("java.io.tmpdir");
		String fileIdentifier = String.valueOf(Calendar.getInstance().getTimeInMillis());
		String reportFileName = fileName + fileIdentifier + fileExtension;
		String pdfPath = tempDir + fileSeparator + reportFileName;
		
		logger.info("############# PDF PATH: " + pdfPath);
		PaxDetailPdfDocResponse paxDetailPdfDocResponse = new PaxDetailPdfDocResponse();

		//COVER PAGE
		PDPage coverPage = new PDPage(PDRectangle.A4);
		PDDocument document = this.getDefaultA4ReportDocument(coverPage);
		createPdfCoverPage(document, coverPage,paxDetailPdfDocRequest);
		
		//2ND PAGE - PASSENGER DETAIL SECTION
		PDPage passengerDetailPage = new PDPage(PDRectangle.A4);
		document.addPage(passengerDetailPage);
		PDPageContentStream passengerDetailContentStream = this.getDefaultContentStream(passengerDetailPage);
		this.createPdfPassengerDetailHeader(document, passengerDetailPage, paxDetailPdfDocRequest);
		this.createPdfPassengerDetailSection(document, passengerDetailPage, paxDetailPdfDocRequest);
		//DOCUMENT TABLE
		this.createDocumentsSection(document,passengerDetailPage, paxDetailPdfDocRequest);
		//EVENT HIT INFORMATION SECTION
		this.createEventHitInfoSection(document,passengerDetailPage, paxDetailPdfDocRequest);
		// HIT HISTORY SECTION
		this.createHitHistorySection(document, passengerDetailPage, paxDetailPdfDocRequest);
		//CO-TRAVELER SECTION
		this.createCotravelerSection(document, passengerDetailPage, paxDetailPdfDocRequest);
		//close secondpage's content stream
		passengerDetailContentStream.close();
		//THIRD PAGE
		PDPage thirdPage = new PDPage(PDRectangle.A4);
		document.addPage(thirdPage);
		PDPageContentStream thirdPageContentStream = this.getDefaultContentStream(thirdPage);
		this.createCreditCardSection(document,thirdPage, paxDetailPdfDocRequest);
		//EMAIL
		this.createEmailSection(document,thirdPage, paxDetailPdfDocRequest);
		//PHONE
		this.createPhoneSection(document,thirdPage, paxDetailPdfDocRequest);
		//FLIGHT HISTORY SECTION
		this.createFlightHistorySection(document, thirdPage, paxDetailPdfDocRequest);
		//EVENT NOTES
		this.createEventNotesSection(document, thirdPage, paxDetailPdfDocRequest);
		//EVENT NOTES HISTORY
		this.createEventNoteHistorySection(document, passengerDetailPage, paxDetailPdfDocRequest);
	
		thirdPageContentStream.close();
		document.save(pdfPath);
		document.close();
		Path path = Paths.get(pdfPath);
		byte[] fileByteArray = Files.readAllBytes(path);
		paxDetailPdfDocResponse.setPdfDocumentPath(pdfPath);
		paxDetailPdfDocResponse.setReportFileName(reportFileName);
		
		paxDetailPdfDocResponse.setFileByteArray(fileByteArray);
		
		return paxDetailPdfDocResponse;

	}
	
	public void createPdfCoverPage(PDDocument document,PDPage coverPage, PaxDetailPdfDocRequest paxDetailPdfDocRequest) throws Exception
	{
				PDPageContentStream coverPageContentStream = this.getDefaultContentStream(coverPage);
				reportCoverTable = createReportCoverlTable(document, coverPage);
				// Main Header Row
				final float TITLE_COLUMN_WIDTH_1 = 25;
				final float TITLE_COLUMN_WIDTH_2 = 50;
				final float TITLE_COLUMN_WIDTH_3 = 25;
				
				final float FIRST_COLUMN_WIDTH = 25;
				final float SECOND_COLUMN_WIDTH = 20;
				final float  THIRD_COLUMN_WIDTH = 30;
				final float FOUR_COLUMN_WIDTH = 25;
				final float LABEL_CELL_PADDING = 10;
						
				Row<PDPage> headerRow = reportCoverTable.createRow(this.COVER_HEADER_ROW_HEIGHT);
				Cell<PDPage> cell = this.createEmptyWhiteCell( headerRow, TITLE_COLUMN_WIDTH_1);
				cell =  this.createCell( headerRow, TITLE_COLUMN_WIDTH_2, this.BOLD_TIMES_ROMAN, DEFAULT_LABEL_FONT_SIZE, new LineStyle(Color.LIGHT_GRAY,0), Color.WHITE, this.DEFAULT_LABEL_BACKGROUND_COLOR, REPORT_NAME);
				cell.setAlign(HorizontalAlignment.CENTER);
				cell =  this.createEmptyWhiteCell(headerRow, TITLE_COLUMN_WIDTH_3);
				reportCoverTable.addHeaderRow(headerRow);
				
				
				PassengerVo passengerVo = paxDetailPdfDocRequest.getPassengerVo();
				Calendar calendar = Calendar.getInstance();
				String reportDate = reportDateTimeFormatter(calendar.getTime());
				
				//Passenger Name row
				Row<PDPage> row = this.getRow(reportCoverTable);
				this.createEmptyWhiteCell( row, FIRST_COLUMN_WIDTH);
				this.createReportCoverLabelCell( row,SECOND_COLUMN_WIDTH , "Name");
				this.createValueCellWithRighBottomtBorder( row,THIRD_COLUMN_WIDTH , passengerVo.getLastName()+ ", "+  passengerVo.getFirstName() + ", " +  passengerVo.getMiddleName());
				this.createEmptyWhiteCell(row, FOUR_COLUMN_WIDTH);
				//Flight row
				row = this.getRow(reportCoverTable);
				this.createEmptyWhiteCell( row,FIRST_COLUMN_WIDTH );
				this.createReportCoverLabelCell( row,SECOND_COLUMN_WIDTH , "Flight");
				cell = this.createValueCellWithRighBottomtBorder( row,THIRD_COLUMN_WIDTH , passengerVo.getCarrier() + passengerVo.getFlightNumber());
				this.createEmptyWhiteCell(row, FOUR_COLUMN_WIDTH);
				//Flight Origin
				row = this.getRow(reportCoverTable);
				this.createEmptyWhiteCell( row,FIRST_COLUMN_WIDTH );
				cell = this.createReportCoverLabelCell( row,SECOND_COLUMN_WIDTH , "Origin");
				cell.setLeftPadding(LABEL_CELL_PADDING);
				this.createValueCellWithRighBottomtBorder( row,THIRD_COLUMN_WIDTH , passengerVo.getFlightOrigin());
				this.createEmptyWhiteCell(row, FOUR_COLUMN_WIDTH);
				//Flight Destination
				row = this.getRow(reportCoverTable);
				this.createEmptyWhiteCell( row,FIRST_COLUMN_WIDTH);
				cell = this.createReportCoverLabelCell( row,SECOND_COLUMN_WIDTH , "ETA");
				cell.setLeftPadding(LABEL_CELL_PADDING);
				cell = this.createValueCellWithRighBottomtBorder( row,THIRD_COLUMN_WIDTH , passengerVo.getFlightETA());
				this.createEmptyWhiteCell(row, FOUR_COLUMN_WIDTH);
				//Flight Destination
				row = this.getRow(reportCoverTable);
				this.createEmptyWhiteCell( row,FIRST_COLUMN_WIDTH );
				cell = this.createReportCoverLabelCell( row,SECOND_COLUMN_WIDTH , "ETD");
				cell.setLeftPadding(LABEL_CELL_PADDING);
				this.createValueCellWithRighBottomtBorder( row,THIRD_COLUMN_WIDTH , passengerVo.getFlightETD());
				this.createEmptyWhiteCell(row, FOUR_COLUMN_WIDTH);
				//Report Date
				row = this.getRow(reportCoverTable);
				this.createEmptyWhiteCell( row,FIRST_COLUMN_WIDTH );
				cell = this.createReportCoverLabelCell( row,SECOND_COLUMN_WIDTH , "Report Date");
				cell.setBottomBorderStyle(new LineStyle(this.DEFAULT_GRAY_ROW_COLOR,1));
				cell = this.createValueCellWithRighBottomtBorder( row,THIRD_COLUMN_WIDTH , reportDate);
				this.createEmptyWhiteCell(row, FOUR_COLUMN_WIDTH);
			
				reportCoverTable.draw();
				coverPageContentStream.close();
	}
	
	public void createPdfPassengerDetailHeader(PDDocument document,PDPage passengerDetailPage, PaxDetailPdfDocRequest paxDetailPdfDocRequest) throws Exception
	{
				
				/* PASSENGER DETAIL HEADER TABLE */
				passengerDetailTable = createPassengerDetailTable(document, passengerDetailPage);
				this.addPassengerPageHeader(passengerDetailTable,  paxDetailPdfDocRequest);
				passengerDetailTable.draw();
	}
	
	/** PASSENGER DETAIL SECTION **/
	public void createPdfPassengerDetailSection(PDDocument document,PDPage passengerDetailPage , PaxDetailPdfDocRequest paxDetailPdfDocRequest) throws Exception
	{
		final float FIRST_COLUMN_WIDTH = 15;
		final float SECOND_COLUMN_WIDTH = 40;
		final float THIRD_COLUMN_WIDTH = 20;
		final float FOURTH_COLUMN_WIDTH = 20;
		final float COLUMN_SEPARATOR_WIDTH = 5;
		final float PASSENGER_FLIGHT_BOX_HEADER_SIZE = 7;
		
		passengerDetailDataTable = createPassengerDetailDataTable(document, passengerDetailPage);
		
		PassengerVo passengerVo = paxDetailPdfDocRequest.getPassengerVo();
		
		//The table should still be displayed if there is no data
		if(passengerVo == null)
		{
			passengerVo = new PassengerVo();
		}
		
		Row<PDPage> row = this.getRow(passengerDetailDataTable);
		Cell<PDPage> cell = this.creatVerticalColumnLabelCell(row, FIRST_COLUMN_WIDTH, "Name:");
		cell.setTopBorderStyle(new LineStyle(this.DEFAULT_LABEL_BACKGROUND_COLOR, PASSENGER_FLIGHT_BOX_HEADER_SIZE));
		cell.setRightBorderStyle(new LineStyle(DEFAULT_LABEL_BACKGROUND_COLOR,0));
		cell.setRightBorderStyle(new LineStyle(this.DEFAULT_LABEL_BACKGROUND_COLOR,0));
		cell = this.createFirstVerticalColumnValueCell( row, SECOND_COLUMN_WIDTH, passengerVo.getLastName() + ", " + passengerVo.getFirstName() + " " + passengerVo.getMiddleName());
		cell.setTopBorderStyle(new LineStyle(this.DEFAULT_LABEL_BACKGROUND_COLOR, PASSENGER_FLIGHT_BOX_HEADER_SIZE));
		this.createFieldValueCell( row, COLUMN_SEPARATOR_WIDTH, "");
		cell = this.creatVerticalColumnLabelCell(row, THIRD_COLUMN_WIDTH, "Flight Number:");
		cell.setTopBorderStyle(new LineStyle(this.DEFAULT_LABEL_BACKGROUND_COLOR, PASSENGER_FLIGHT_BOX_HEADER_SIZE));
		cell.setRightBorderStyle(new LineStyle(DEFAULT_LABEL_BACKGROUND_COLOR,0));
		cell = this.createFirstVerticalColumnValueCell( row, FOURTH_COLUMN_WIDTH, passengerVo.getFlightNumber());
		cell.setTopBorderStyle(new LineStyle(this.DEFAULT_LABEL_BACKGROUND_COLOR, PASSENGER_FLIGHT_BOX_HEADER_SIZE));
		
		row = this.getRow(passengerDetailDataTable);
		this.creatVerticalColumnLabelCell( row, FIRST_COLUMN_WIDTH, "DOB:");
		this.createVerticalColumnValueCell( row, SECOND_COLUMN_WIDTH, reportDateFormatter(passengerVo.getDob()));
		this.createFieldValueCell( row, COLUMN_SEPARATOR_WIDTH, "");
		this.creatVerticalColumnLabelCell( row, THIRD_COLUMN_WIDTH, "Carrier:");
		cell = this.createVerticalColumnValueCell( row, FOURTH_COLUMN_WIDTH, passengerVo.getCarrier());
		
		
		
		row = this.getRow(passengerDetailDataTable);
		this.creatVerticalColumnLabelCell( row, FIRST_COLUMN_WIDTH, "Gender:");
		this.createVerticalColumnValueCell( row, SECOND_COLUMN_WIDTH, passengerVo.getGender());
		this.createFieldValueCell( row, COLUMN_SEPARATOR_WIDTH, "");
		this.creatVerticalColumnLabelCell( row, THIRD_COLUMN_WIDTH, "Origin Airport:");
		this.createVerticalColumnValueCell( row, FOURTH_COLUMN_WIDTH, passengerVo.getFlightOrigin());
		
		row = this.getRow(passengerDetailDataTable);
		this.creatVerticalColumnLabelCell( row, FIRST_COLUMN_WIDTH, "Nationality:");
		this.createVerticalColumnValueCell(row, SECOND_COLUMN_WIDTH, passengerVo.getNationality());
		this.createFieldValueCell( row, COLUMN_SEPARATOR_WIDTH, "");
		this.creatVerticalColumnLabelCell( row, THIRD_COLUMN_WIDTH, "Destination Airport:");
		cell = this.createVerticalColumnValueCell( row, FOURTH_COLUMN_WIDTH, passengerVo.getFlightDestination());
		
		
		row = this.getRow(passengerDetailDataTable);
		cell = this.creatVerticalColumnLabelCell( row, FIRST_COLUMN_WIDTH, "");
		cell.setBottomBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,0));
		this.createLastVerticalColumnValueCell( row, SECOND_COLUMN_WIDTH, "");
		this.createFieldValueCell( row, COLUMN_SEPARATOR_WIDTH, "");
		cell = this.creatVerticalColumnLabelCell( row, THIRD_COLUMN_WIDTH, "Trip Type:");
		cell.setBottomBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,0));
		this.createLastVerticalColumnValueCell( row, FOURTH_COLUMN_WIDTH, "");
		
		
		passengerDetailDataTable.draw();
				
	}
	
	/** DOCUMENT SECTION **/
	public void createDocumentsSection(PDDocument document,PDPage passengerDetailPage, PaxDetailPdfDocRequest paxDetailPdfDocRequest)throws Exception
	{
		
		final float DOCUMENT_COLUMN_1 = 25;
		final float DOCUMENT_COLUMN_2 = 25;
		final float DOCUMENT_COLUMN_3 = 25;
		final float DOCUMENT_COLUMN_4 = 25;
		
		List<DocumentVo> documentList = paxDetailPdfDocRequest.getPassengerVo().getDocuments();

		documentTable = createDocumentsTable(document, passengerDetailPage);
		this.addHorizontalReportSectionHeader(documentTable, DOCUMENTS_TITLE);
		// Document headers
		Row<PDPage> documentHeaderRow = this.getRow(documentTable);
		this.createHorizontalColumnLabelCell(documentHeaderRow, DOCUMENT_COLUMN_1,
				"Doc#");
		this.createHorizontalColumnLabelCell( documentHeaderRow, DOCUMENT_COLUMN_2,
				"Type");
		this.createHorizontalColumnLabelCell( documentHeaderRow, DOCUMENT_COLUMN_3,
				"Issue Country");
		this.createHorizontalColumnLabelCell( documentHeaderRow, DOCUMENT_COLUMN_4,
				"Exp. Date");

		// Document data
		Row<PDPage> documentDataRow;
		DocumentVo documentVo;
		if (documentList!=null && documentList.size()>0)
		{
			for(int i=0; i<documentList.size(); i++)
			{
				documentVo = documentList.get(i);
			
					documentDataRow = this.getRow(documentTable);
					this.createFirstColoredCell( documentDataRow, DOCUMENT_COLUMN_1, documentVo.getDocumentNumber(), i);
					this.createColoredCell( documentDataRow, DOCUMENT_COLUMN_2, documentVo.getDocumentType(), i);
					this.createColoredCell( documentDataRow, DOCUMENT_COLUMN_3, documentVo.getIssuanceCountry(), i);
					this.createLastColoredCell( documentDataRow, DOCUMENT_COLUMN_4, reportDateFormatter(documentVo.getExpirationDate()), i);
		
			}
		}
		
		documentTable.draw();
	}
	/** EVENT HIT INFORMATION SECTION **/
	public void createEventHitInfoSection(PDDocument document,PDPage passengerDetailPage, PaxDetailPdfDocRequest paxDetailPdfDocRequest)throws Exception
	{
		
		final float EVENT_HIT_INFO_COLUMN_1 = 25;
		final float EVENT_HIT_INFO_COLUMN_2 = 25;
		final float EVENT_HIT_INFO_COLUMN_3 = 25;
		final float EVENT_HIT_INFO_COLUMN_4 = 25;
		
		LinkedHashSet<HitDetailVo> hitDetailVoList = paxDetailPdfDocRequest.getHitDetailVoList(); 

		eventHitTable = createEventHitTable(document, passengerDetailPage);
		this.addHorizontalReportSectionHeader(eventHitTable, EVENT_HIT_INFORMATION_TITLE);
		// Event Hit Information headers
		Row<PDPage> eventHitRow = this.getRow(eventHitTable);
		this.createHorizontalColumnLabelCell( eventHitRow, EVENT_HIT_INFO_COLUMN_1,
				"Hit Severity:");
		this.createHorizontalColumnLabelCell( eventHitRow, EVENT_HIT_INFO_COLUMN_2,
				"Hit Category");
		this.createHorizontalColumnLabelCell( eventHitRow, EVENT_HIT_INFO_COLUMN_3,
				"HIt Title");
		this.createHorizontalColumnLabelCell( eventHitRow, EVENT_HIT_INFO_COLUMN_4,
				"Hit Status");
		// Event Hit Information data
		if(hitDetailVoList!=null && !hitDetailVoList.isEmpty())
		{
			Row<PDPage> eventHitDataRow;
			int i=0;
	
			for(HitDetailVo hitDetailVo : hitDetailVoList)
			{	
					eventHitDataRow = this.getRow(eventHitTable);
					this.createFirstColoredCell( eventHitDataRow, EVENT_HIT_INFO_COLUMN_1, hitDetailVo.getSeverity(), i);
					this.createColoredCell( eventHitDataRow, EVENT_HIT_INFO_COLUMN_2, hitDetailVo.getCategory(), i);
					this.createColoredCell( eventHitDataRow, EVENT_HIT_INFO_COLUMN_3, hitDetailVo.getRuleTitle(), i);
					this.createLastColoredCell( eventHitDataRow, EVENT_HIT_INFO_COLUMN_4, hitDetailVo.getStatus(), i);
					
				
				i++;
			}
		
		
		
		
		}
		eventHitTable.draw();
	}
	
	public void createHitHistorySection(PDDocument document,PDPage passengerDetailPage, PaxDetailPdfDocRequest paxDetailPdfDocRequest)throws Exception
	{
		final float HIT_HISTORY_COLUMN_1 = 25;
		final float HIT_HISTORY_COLUMN_2 = 25;
		final float HIT_HISTORY_COLUMN_3 = 25;
		final float HIT_HISTORY_COLUMN_4 = 25;
		hitHistoryTable = createHitHistoryTable(document, passengerDetailPage);
		this.addHorizontalReportSectionHeader(hitHistoryTable, HIT_HISTORY_TITLE);
		Row<PDPage> hitHistoryRow = this.getRow(hitHistoryTable);
		// Hit History Header
		this.createHorizontalColumnLabelCell( hitHistoryRow, HIT_HISTORY_COLUMN_1, "Category:");
		this.createHorizontalColumnLabelCell( hitHistoryRow, HIT_HISTORY_COLUMN_2, "Document Number");
		this.createHorizontalColumnLabelCell( hitHistoryRow, HIT_HISTORY_COLUMN_3, "Conditions");
		this.createHorizontalColumnLabelCell( hitHistoryRow, HIT_HISTORY_COLUMN_4, "Flight Date");
		
		List<HitDetailVo> hitDetailHistoryList = paxDetailPdfDocRequest.getHitDetailHistoryVoList();

		if(hitDetailHistoryList!=null && !hitDetailHistoryList.isEmpty())
		{
			HitDetailVo hitDetailVo;
			Row<PDPage> eventHitHistoryRow;
			
			for(int i=0; i<hitDetailHistoryList.size();i++)
			{
				hitDetailVo = hitDetailHistoryList.get(i);
		
					eventHitHistoryRow = this.getRow(hitHistoryTable);
					this.createFirstColoredCell( eventHitHistoryRow, HIT_HISTORY_COLUMN_1,hitDetailVo.getCategory(), i);
					this.createColoredCell( eventHitHistoryRow, HIT_HISTORY_COLUMN_2, hitDetailVo.getPassengerDocNumber(), i);
					this.createColoredCell( eventHitHistoryRow, HIT_HISTORY_COLUMN_3,replaceTrimChars( hitDetailVo.getRuleConditions()), i);
					this.createLastColoredCell( eventHitHistoryRow, HIT_HISTORY_COLUMN_4, reportDateFormatter(hitDetailVo.getFlightDate()), i);
					
			}
			
		}
		hitHistoryTable.draw();

	}
	
	public void createCotravelerSection(PDDocument document,PDPage passengerDetailPage, PaxDetailPdfDocRequest paxDetailPdfDocRequest)throws Exception
	{
		final float HIT_HISTORY_COLUMN_1 = 28;
		final float HIT_HISTORY_COLUMN_2 = 25;
		final float HIT_HISTORY_COLUMN_3 = 27;
		final float HIT_HISTORY_COLUMN_4 = 10;
		final float HIT_HISTORY_COLUMN_5 = 10;
		this.cotravelerTable= this.createCotravelerTable(document, passengerDetailPage);
		
		this.addHorizontalReportSectionHeader(this.cotravelerTable, this.COTRAVELER_TITLE);
		Row<PDPage> hitHistoryRow = this.getRow(this.cotravelerTable);
		// Co-traveler Table Header
		this.createHorizontalColumnLabelCell(hitHistoryRow, HIT_HISTORY_COLUMN_1, "First Name");
		this.createHorizontalColumnLabelCell( hitHistoryRow, HIT_HISTORY_COLUMN_2, "Middle Name");
		this.createHorizontalColumnLabelCell( hitHistoryRow, HIT_HISTORY_COLUMN_3, "Last Name");
		this.createHorizontalColumnLabelCell( hitHistoryRow, HIT_HISTORY_COLUMN_4, "Gender");
		this.createHorizontalColumnLabelCell( hitHistoryRow, HIT_HISTORY_COLUMN_5, "Age");
		
		PassengerVo passengerVo = paxDetailPdfDocRequest.getPassengerVo();
		if(passengerVo!=null && passengerVo.getPnrVo()!=null)
		{
			List<PassengerVo> cotravelerList = passengerVo.getPnrVo().getPassengers();
			if(cotravelerList!=null)
			{
				PassengerVo cotraveler;
				Row<PDPage> cotravelerRow;
				
				for(int i=0; i<cotravelerList.size();i++)
				{
					cotraveler = cotravelerList.get(i);
					
						cotravelerRow = this.getRow(cotravelerTable);
						this.createFirstColoredCell(cotravelerRow, HIT_HISTORY_COLUMN_1,cotraveler.getFirstName(), i);
						this.createColoredCell( cotravelerRow, HIT_HISTORY_COLUMN_2, cotraveler.getMiddleName(), i);
						this.createColoredCell( cotravelerRow, HIT_HISTORY_COLUMN_3,cotraveler.getLastName(), i);
						this.createColoredCell( cotravelerRow, HIT_HISTORY_COLUMN_4, cotraveler.getGender(), i);
						this.createLastColoredCell( cotravelerRow, HIT_HISTORY_COLUMN_5,changeToString(cotraveler.getAge()), i);
				
				}
				
				
			}
			
			
		}
		
		cotravelerTable.draw();

	}
	

	public void createCreditCardSection(PDDocument document,PDPage thirdPage, PaxDetailPdfDocRequest paxDetailPdfDocRequest)throws Exception
	{
		
		final float CREDITCARD_COLUMN_1 = 25;
		final float CREDITCARD_COLUMN_2 = 25;
		final float CREDITCARD_COLUMN_3 = 25;
		final float CREDITCARD_COLUMN_4 = 25;
		
		this.creditCardTable = createCreditCardTable(document, thirdPage);
		this.addHorizontalReportSectionHeader(this.creditCardTable, CREDIT_CARD_TITLE);
		// Credit Card headers
		Row<PDPage> creditCardHeaderRow = this.getRow(this.creditCardTable);
		this.createHorizontalColumnLabelCell( creditCardHeaderRow, CREDITCARD_COLUMN_1,
				"Holder");
		this.createHorizontalColumnLabelCell( creditCardHeaderRow, CREDITCARD_COLUMN_2,
				"Type");
		this.createHorizontalColumnLabelCell( creditCardHeaderRow, CREDITCARD_COLUMN_3,
				"Number");
		this.createHorizontalColumnLabelCell( creditCardHeaderRow, CREDITCARD_COLUMN_4,
				"Exp. Date");
		
		PassengerVo passengerVo = paxDetailPdfDocRequest.getPassengerVo();
		if(passengerVo!=null && passengerVo.getPnrVo()!=null)
		{
			List<CreditCardVo> creditCardList = passengerVo.getPnrVo().getCreditCards();
			
			if(creditCardList!=null)
			{
				CreditCardVo creditCardVo;
				Row<PDPage> creditCardRow;
				
				for(int i=0; i<creditCardList.size();i++)
				{
					creditCardVo = creditCardList.get(i);
						creditCardRow = this.getRow(creditCardTable);
						this.createFirstColoredCell( creditCardRow, CREDITCARD_COLUMN_1,creditCardVo.getAccountHolder(), i);
						this.createColoredCell( creditCardRow, CREDITCARD_COLUMN_2, creditCardVo.getCardType(),i);
						this.createColoredCell( creditCardRow, CREDITCARD_COLUMN_3,creditCardVo.getNumber(),i);
						this.createLastColoredCell( creditCardRow, CREDITCARD_COLUMN_4,reportDateFormatter(creditCardVo.getExpiration()), i);
				}
				
			}
			
		}
		
		
		
		

		
		creditCardTable.draw();
	}
	
	public void createEmailSection(PDDocument document,PDPage thirdPage, PaxDetailPdfDocRequest paxDetailPdfDocRequest)throws Exception
	{
		String emailAddresses = "";
		final float EMAIL_COLUMN_1 = 25;
		final float EMAIL_COLUMN_2 = 75;
		
		PassengerVo passengerVo = paxDetailPdfDocRequest.getPassengerVo();
		
		if(passengerVo!=null && passengerVo.getPnrVo()!=null)
		{
			List<EmailVo> emailVoList = passengerVo.getPnrVo().getEmails();
			
			if(emailVoList!=null)
			{
				for(int i=0; i<emailVoList.size(); i++)
				{
					if(i==0)
					{
						emailAddresses = emailVoList.get(i).getAddress();
						
					}
					else
					{
						emailAddresses = emailAddresses + ", "  + emailVoList.get(i).getAddress();
						
					}
				}
			}
			
		}
		

		this.emailTable = createEmailTable(document, thirdPage);
		Row<PDPage> emailtHeaderRow = this.getRow(this.emailTable);
		this.createSingleRowLabelCell(emailtHeaderRow, EMAIL_COLUMN_1,
				EMAIL_TITLE);
		this.createSingleRowValueCell(emailtHeaderRow, EMAIL_COLUMN_2,
				emailAddresses.toLowerCase());
		
		
		this.emailTable.draw();
	}
	
	public void createPhoneSection(PDDocument document,PDPage thirdPage, PaxDetailPdfDocRequest paxDetailPdfDocRequest)throws Exception
	{
		String phoneNumbers = "";
		final float PHONE_COLUMN_1 = 25;
		final float PHONE_COLUMN_2 = 75;
		
		this.phoneTable = createPhoneTable(document, thirdPage);

		PassengerVo passengerVo = paxDetailPdfDocRequest.getPassengerVo();
		
		if(passengerVo!=null && passengerVo.getPnrVo()!=null)
		{
			List<PhoneVo> phoneVoist =	passengerVo.getPnrVo().getPhoneNumbers();
			

			if(phoneVoist!=null)
			{
				for(int i=0; i<phoneVoist.size(); i++)
				{
					if(i==0)
					{
						phoneNumbers = phoneVoist.get(i).getNumber();
					}
					else
					{
						phoneNumbers = phoneNumbers + ", "  + phoneVoist.get(i).getNumber();
					}
				}
			}
			
			
			
		}
		
		
		Row<PDPage> phoneHeaderRow = this.getRow(this.phoneTable);
		this.createSingleRowLabelCell( phoneHeaderRow, PHONE_COLUMN_1,
				PHONE_TITLE);
		this.createSingleRowValueCell( phoneHeaderRow, PHONE_COLUMN_2,
				phoneNumbers);
		
		this.phoneTable.draw();
	}
	
	public void createFlightHistorySection(PDDocument document,PDPage passengerDetailPage, PaxDetailPdfDocRequest paxDetailPdfDocRequest)throws Exception
	{
		final float FLIGHT_HISTORY_COLUMN_1 = 16;
		final float FLIGHT_HISTORY_COLUMN_2 = 16;
		final float FLIGHT_HISTORY_COLUMN_3 = 26;
		final float FLIGHT_HISTORY_COLUMN_4 = 16;
		final float FLIGHT_HISTORY_COLUMN_5 = 26;

		this.flightHistoryTable = createFlightHistoryTable(document, passengerDetailPage);
		this.addHorizontalReportSectionHeader(this.flightHistoryTable, FLIGHT_HISTORY_TITLE);
		Row<PDPage> flightHistoryHeaderRow = this.getRow(this.flightHistoryTable);
		// flight History Header
		this.createHorizontalColumnLabelCell( flightHistoryHeaderRow, FLIGHT_HISTORY_COLUMN_1,
				"Flight");
		this.createHorizontalColumnLabelCell( flightHistoryHeaderRow, FLIGHT_HISTORY_COLUMN_2,
				"Origin");
		this.createHorizontalColumnLabelCell( flightHistoryHeaderRow, FLIGHT_HISTORY_COLUMN_3,
				"Departure Time");
		this.createHorizontalColumnLabelCell( flightHistoryHeaderRow, FLIGHT_HISTORY_COLUMN_4,
				"Destination");
		this.createHorizontalColumnLabelCell( flightHistoryHeaderRow, FLIGHT_HISTORY_COLUMN_5,
				"Arrival Time");

		List<FlightVoForFlightHistory> flightHistoryVoList = paxDetailPdfDocRequest.getFlightHistoryVoList();
		Row<PDPage> flightHistoryDataRow;
		FlightVoForFlightHistory flightVoForFlightHistory;
		// FLIGHT HISTORY DATA
		if(flightHistoryVoList!=null && !flightHistoryVoList.isEmpty())
		{
			for(int i=0;i<flightHistoryVoList.size();i++)
			{
				flightVoForFlightHistory = flightHistoryVoList.get(i);
	
					flightHistoryDataRow = this.getRow(this.flightHistoryTable);
					this.createFirstColoredCell( flightHistoryDataRow, FLIGHT_HISTORY_COLUMN_1, flightVoForFlightHistory.getFullFlightNumber(), i);		
					this.createColoredCell( flightHistoryDataRow, FLIGHT_HISTORY_COLUMN_2, flightVoForFlightHistory.getOrigin(), i);
					this.createColoredCell( flightHistoryDataRow, FLIGHT_HISTORY_COLUMN_3, reportDateTimeFormatter(flightVoForFlightHistory.getEtd()), i);
					this.createColoredCell( flightHistoryDataRow, FLIGHT_HISTORY_COLUMN_4, flightVoForFlightHistory.getDestination(), i);
					this.createLastColoredCell( flightHistoryDataRow, FLIGHT_HISTORY_COLUMN_5,  reportDateTimeFormatter(flightVoForFlightHistory.getEta()), i);
		
					
			}
		}
		flightHistoryTable.draw();
		
	}
	
	//EVENT NOTES
	public void createEventNotesSection(PDDocument document,PDPage passengerDetailPage, PaxDetailPdfDocRequest paxDetailPdfDocRequest)throws Exception
	{
		final float EVENT_NOTES_COLUMN_1 = 15;
		final float EVENT_NOTES_COLUMN_2 = 85;
		final float EVENT_NOTES_COLUMN_3 = 15;
		final float EVENT_NOTES_COLUMN_4 = 85;
		final float EVENT_NOTES_COLUMN_5 = 15;
		final float EVENT_NOTES_COLUMN_6 = 85;
		final float EVENT_NOTES_CONTENT_SECTION_SIZE = 100;
		
		this.eventNotesTable = createEventNotesTable(document, passengerDetailPage);
		this.addHorizontalReportSectionHeader(this.eventNotesTable, this.EVENT_NOTES_TITLE);
		Row<PDPage> eventNotesHeaderRow;
		Row<PDPage> eventNotesContentRow ;
		LinkedHashSet<NoteVo> noteLinkedSet = paxDetailPdfDocRequest.getEventNotesSet();
		
		for(NoteVo noteVo : noteLinkedSet)
		{
			
			eventNotesHeaderRow = this.getRow(this.eventNotesTable);
	
			this.createFirstNoteColumnLabelCell( eventNotesHeaderRow, EVENT_NOTES_COLUMN_1,
					"Note Types:");
			this.createLastNoteColumnValueCell( eventNotesHeaderRow, EVENT_NOTES_COLUMN_2,
					this.getNoteTypeAsString(noteVo.getNoteTypeVoSet()));
			
			eventNotesHeaderRow = this.getRow(this.eventNotesTable);
			this.createFirstNoteColumnLabelCell( eventNotesHeaderRow, EVENT_NOTES_COLUMN_3,
					"Created By:");
			this.createLastNoteColumnValueCell( eventNotesHeaderRow, EVENT_NOTES_COLUMN_4,
					noteVo.getCreatedBy());
			
			eventNotesHeaderRow = this.getRow(this.eventNotesTable);
			this.createFirstNoteColumnLabelCell( eventNotesHeaderRow, EVENT_NOTES_COLUMN_5,
					"Created On:");
			this.createLastNoteColumnValueCell( eventNotesHeaderRow, EVENT_NOTES_COLUMN_6,
					reportDateTimeFormatter(noteVo.getCreatedAt()));
			
			eventNotesContentRow = this.getRow(this.eventNotesTable);
			createNoteContentCell( eventNotesContentRow, EVENT_NOTES_CONTENT_SECTION_SIZE,removeSpecialCharacters(noteVo.getPlainTextNote()));
			
			
		}
		
		eventNotesTable.draw();
		
	}
	
	//EVENT NOTES HISTORY
	public void createEventNoteHistorySection(PDDocument document,PDPage passengerDetailPage, PaxDetailPdfDocRequest paxDetailPdfDocRequest)throws Exception
	{
		final float EVENT_NOTES_COLUMN_1 = 15;
		final float EVENT_NOTES_COLUMN_2 = 85;
		final float EVENT_NOTES_COLUMN_3 = 15;
		final float EVENT_NOTES_COLUMN_4 = 85;
		final float EVENT_NOTES_COLUMN_5 = 15;
		final float EVENT_NOTES_COLUMN_6 = 85;
		final float EVENT_NOTES_CONTENT_SECTION_SIZE = 100;
		
		this.eventNoteHistoryTable = createEventNoteHistoryTable(document, passengerDetailPage);
		this.addHorizontalReportSectionHeader(this.eventNoteHistoryTable, this.EVENT_NOTE_HISTORY_TITLE);
		Row<PDPage> eventNoteHistoryHeaderRow;
		Row<PDPage> eventNoteHistoryContentRow ;
		LinkedHashSet<NoteVo> noteLinkedSet = paxDetailPdfDocRequest.getEventHistoricalNotesSet();
		
		for(NoteVo noteVo : noteLinkedSet)
		{
			
			eventNoteHistoryHeaderRow = this.getRow(this.eventNoteHistoryTable);
	
			this.createFirstNoteColumnLabelCell( eventNoteHistoryHeaderRow, EVENT_NOTES_COLUMN_1,
					"Note Types:");
			this.createLastNoteColumnValueCell( eventNoteHistoryHeaderRow, EVENT_NOTES_COLUMN_2,
					this.getNoteTypeAsString(noteVo.getNoteTypeVoSet()));
			
			eventNoteHistoryHeaderRow = this.getRow(this.eventNoteHistoryTable);
			this.createFirstNoteColumnLabelCell( eventNoteHistoryHeaderRow, EVENT_NOTES_COLUMN_3,
					"Created By:");
			this.createLastNoteColumnValueCell( eventNoteHistoryHeaderRow, EVENT_NOTES_COLUMN_4,
					noteVo.getCreatedBy());
			
			eventNoteHistoryHeaderRow = this.getRow(this.eventNoteHistoryTable);
			this.createFirstNoteColumnLabelCell( eventNoteHistoryHeaderRow, EVENT_NOTES_COLUMN_5,
					"Created On:");
			this.createLastNoteColumnValueCell( eventNoteHistoryHeaderRow, EVENT_NOTES_COLUMN_6,
					reportDateTimeFormatter(noteVo.getCreatedAt()));
			
			eventNoteHistoryContentRow = this.getRow(this.eventNoteHistoryTable);
			createNoteContentCell( eventNoteHistoryContentRow, EVENT_NOTES_CONTENT_SECTION_SIZE,removeSpecialCharacters(noteVo.getPlainTextNote()));
			
			
		}
		
		eventNoteHistoryTable.draw();
		
	}

	public void addPassengerPageHeader(BaseTable table,  PaxDetailPdfDocRequest paxDetailPdfDocRequest) {
		final float PASSENGER_DETAIL_HEADER_COLUMN_1_WIDTH = 50;
		final float PASSENGER_DETAIL_HEADER_COLUMN_2_WIDTH = 30;
		final float PASSENGER_DETAIL_HEADER_COLUMN_3_WIDTH = 20;
		final float PASSENGER_DETAIL_HEADER_COLUMN_HEIGHT = 15;

		String alert = paxDetailPdfDocRequest.getAlert();
		PassengerVo passengerVo = paxDetailPdfDocRequest.getPassengerVo();
		String eventId = passengerVo.getPaxId();
		
		// Main Header Row
		Row<PDPage> headerRow = table.createRow(PASSENGER_DETAIL_HEADER_COLUMN_HEIGHT);
		Cell<PDPage> cell = headerRow.createCell(PASSENGER_DETAIL_HEADER_COLUMN_1_WIDTH,
				PASSENGER_DETAIL_HEADER_TEXT + ":");
		this.setPassengerDetailHeaderLabelProperties(cell);

		cell = headerRow.createCell(PASSENGER_DETAIL_HEADER_COLUMN_2_WIDTH, eventId);
		this.setPassengerDetailHeaderValueProperties(cell);
		table.addHeaderRow(headerRow);

		// Alerts
		if (alert == null || alert.isEmpty()) {
			cell = headerRow.createCell(PASSENGER_DETAIL_HEADER_COLUMN_3_WIDTH, "");
			cell.setTextColor(Color.WHITE);
			cell.setTopBorderStyle(new LineStyle(Color.WHITE, 0));
			cell.setRightBorderStyle(new LineStyle(Color.WHITE, 0));
			cell.setBottomBorderStyle(new LineStyle(Color.WHITE, 0));

		} else {
			cell = headerRow.createCell(PASSENGER_DETAIL_HEADER_COLUMN_3_WIDTH, alert);
			cell.setFont(this.BOLD_TIMES_ROMAN);
			cell.setFontSize(14);
			cell.setValign(VerticalAlignment.MIDDLE);
			cell.setAlign(HorizontalAlignment.CENTER);
			cell.setTextColor(Color.WHITE);
			cell.setFillColor(Color.RED);
			cell.setTopBorderStyle(new LineStyle(Color.WHITE, 0));
			cell.setRightBorderStyle(new LineStyle(Color.WHITE, 0));
			cell.setBottomBorderStyle(new LineStyle(Color.WHITE, 0));
		}

		// severity
		headerRow = table.createRow(PASSENGER_DETAIL_HEADER_COLUMN_HEIGHT);
		cell = headerRow.createCell(PASSENGER_DETAIL_HEADER_COLUMN_1_WIDTH, PASSENGER_DETAIL_HEADER_TEXT_2 + ":");
		this.setBottomPassengerDetailHeaderLabelProperties(cell);

		cell = headerRow.createCell(PASSENGER_DETAIL_HEADER_COLUMN_2_WIDTH, paxDetailPdfDocRequest.getHighestSeverity());
		this.setBottomPassengerDetailHeaderValueProperties(cell);

		table.addHeaderRow(headerRow);

		// empty white cell
		cell = headerRow.createCell(PASSENGER_DETAIL_HEADER_COLUMN_3_WIDTH, "");
		cell.setTextColor(Color.WHITE);
		cell.setLeftBorderStyle(new LineStyle(Color.WHITE, 0));
		cell.setTopBorderStyle(new LineStyle(Color.WHITE, 0));
		cell.setRightBorderStyle(new LineStyle(Color.WHITE, 0));
		cell.setBottomBorderStyle(new LineStyle(Color.WHITE, 0));

		table.addHeaderRow(headerRow);

	}

	public void addReportSectionHeader(BaseTable table, String headerTitle) {
		final float REPORT_SECTION_HEADER_COLUMN_1_WIDTH = 100;
		final float REPORT_SECTION_HEADER_COLUMN_HEIGHT = 12;

		// Main Header Row
		Row<PDPage> headerRow = table.createRow(REPORT_SECTION_HEADER_COLUMN_HEIGHT);
		Cell<PDPage> cell = headerRow.createCell(REPORT_SECTION_HEADER_COLUMN_1_WIDTH, headerTitle);
		this.setPassengerDetailHeaderLabelProperties(cell);

		cell.setFont(this.BOLD_TIMES_ROMAN);
		cell.setFontSize(12);
		cell.setValign(VerticalAlignment.MIDDLE);
		cell.setAlign(HorizontalAlignment.CENTER);
		cell.setLeftBorderStyle(new LineStyle(Color.WHITE, 0));
		cell.setTopBorderStyle(new LineStyle(Color.WHITE, 10));
		cell.setRightBorderStyle(new LineStyle(Color.WHITE, 0));
		cell.setBottomBorderStyle(new LineStyle(Color.WHITE, 0));
		cell.setFillColor(Color.lightGray);

		table.addHeaderRow(headerRow);

	}

	public void addHorizontalReportSectionHeader(BaseTable table, String headerTitle) {
		final float REPORT_SECTION_HEADER_COLUMN_1_WIDTH = 100;
		final float REPORT_SECTION_HEADER_COLUMN_HEIGHT = 15;

		// Main Header Row
		Row<PDPage> headerRow = table.createRow(REPORT_SECTION_HEADER_COLUMN_HEIGHT);
		Cell<PDPage> cell = headerRow.createCell(REPORT_SECTION_HEADER_COLUMN_1_WIDTH, headerTitle);
		this.setPassengerDetailHeaderLabelProperties(cell);

		cell.setFont(this.BOLD_TIMES_ROMAN);
		cell.setFontSize(this.DEFAULT_HORIZONTAL_SECTION_HEADER_FONT);
		cell.setValign(VerticalAlignment.MIDDLE);
		cell.setAlign(HorizontalAlignment.CENTER);
		cell.setLeftBorderStyle(new LineStyle(DEFAULT_LABEL_BACKGROUND_COLOR, 1));
		cell.setTopBorderStyle(new LineStyle(DEFAULT_LABEL_BACKGROUND_COLOR, 1));
		cell.setRightBorderStyle(new LineStyle(DEFAULT_LABEL_BACKGROUND_COLOR, 1));
		cell.setBottomBorderStyle(new LineStyle(Color.LIGHT_GRAY, 1));
		cell.setFillColor(DEFAULT_LABEL_BACKGROUND_COLOR);
		cell.setTextColor(DEFAULT_HORIZONTAL_SECTION_HEADER_TEXT_COLOR);

		table.addHeaderRow(headerRow);

	}


	public BaseTable createReportCoverlTable(PDDocument document, PDPage page) throws Exception {

		BaseTable table = new BaseTable(REPORT_COVER_Y_TABLE_START_POSITION,
				this.getDefaultReportCoverPageStartYposition(page), this.REPORT_COVER_BOTTOM_MARGIN,
				this.getDefaultTableWidth(page), this.DEFAULT_MARGIN, document, page, true, this.REPORT_DRAW_CONTENT);

		return table;
	}

	public BaseTable createPassengerDetailTable(PDDocument document, PDPage page) throws Exception {

		BaseTable table = new BaseTable(this.DEFAULT_Y_TABLE_START_POSITION,
				this.getDefaultReportPageStartYposition(page), this.DEFAULT_BOTTOM_MARGIN,
				this.getDefaultTableWidth(page), this.DEFAULT_MARGIN, document, page, true, this.REPORT_DRAW_CONTENT);

		return table;
	}

	public BaseTable createPassengerDetailDataTable(PDDocument document, PDPage page) throws Exception {
		float tableStartPosition = DEFAULT_Y_TABLE_START_POSITION - passengerDetailTable.getHeaderAndDataHeight()
				- 10.0f;

		BaseTable table = new BaseTable(tableStartPosition,
				this.getDefaultReportPageStartYposition(page), this.DEFAULT_BOTTOM_MARGIN,
				this.getDefaultTableWidth(page), this.DEFAULT_MARGIN, document, page, true, this.REPORT_DRAW_CONTENT);

		return table;
	}
	public BaseTable createDocumentsTable(PDDocument document, PDPage page) throws Exception {
		float tableStartPosition = DEFAULT_Y_TABLE_START_POSITION - passengerDetailTable.getHeaderAndDataHeight()
				- passengerDetailDataTable.getHeaderAndDataHeight() - 20.0f;

		BaseTable table = new BaseTable(tableStartPosition, this.getDefaultReportPageStartYposition(page),
				this.DEFAULT_BOTTOM_MARGIN, this.getDefaultTableWidth(page), this.DEFAULT_MARGIN, document, page, true,
				this.REPORT_DRAW_CONTENT);

		return table;
	}

	public BaseTable createEventHitTable(PDDocument document, PDPage page) throws Exception {
		float tableStartPosition = DEFAULT_Y_TABLE_START_POSITION - passengerDetailTable.getHeaderAndDataHeight()
				- passengerDetailDataTable.getHeaderAndDataHeight() - documentTable.getHeaderAndDataHeight() - 40.0f;

		BaseTable table = new BaseTable(tableStartPosition, this.getDefaultReportPageStartYposition(page),
				this.DEFAULT_BOTTOM_MARGIN, this.getDefaultTableWidth(page), this.DEFAULT_MARGIN, document, page, true,
				this.REPORT_DRAW_CONTENT);

		return table;
	}

	public BaseTable createHitHistoryTable(PDDocument document, PDPage page) throws Exception {
		float tableStartPosition = DEFAULT_Y_TABLE_START_POSITION - passengerDetailTable.getHeaderAndDataHeight()
				- passengerDetailDataTable.getHeaderAndDataHeight()- documentTable.getHeaderAndDataHeight() - this.eventHitTable.getHeaderAndDataHeight()
				- 60.0f;

		BaseTable table = new BaseTable(tableStartPosition, this.getDefaultReportPageStartYposition(page),
				this.DEFAULT_BOTTOM_MARGIN, this.getDefaultTableWidth(page), this.DEFAULT_MARGIN, document, page, true,
				this.REPORT_DRAW_CONTENT);

		return table;
	}
	
	public BaseTable createCotravelerTable(PDDocument document, PDPage page) throws Exception {
		float tableStartPosition = DEFAULT_Y_TABLE_START_POSITION - passengerDetailTable.getHeaderAndDataHeight()
				- passengerDetailDataTable.getHeaderAndDataHeight()- documentTable.getHeaderAndDataHeight() - this.eventHitTable.getHeaderAndDataHeight() - this.hitHistoryTable.getHeaderAndDataHeight()
				- 80.0f;

		BaseTable table = new BaseTable(tableStartPosition, this.getDefaultReportPageStartYposition(page),
				this.DEFAULT_BOTTOM_MARGIN, this.getDefaultTableWidth(page), this.DEFAULT_MARGIN, document, page, true,
				this.REPORT_DRAW_CONTENT);

		return table;
	}


	
	public BaseTable createCreditCardTable(PDDocument document, PDPage page) throws Exception {
		float tableStartPosition = DEFAULT_Y_TABLE_START_POSITION  - 20.0f;

		BaseTable table = new BaseTable(tableStartPosition, this.getDefaultReportPageStartYposition(page),
				this.DEFAULT_BOTTOM_MARGIN, this.getDefaultTableWidth(page), this.DEFAULT_MARGIN, document, page, true,
				this.REPORT_DRAW_CONTENT);

		return table;
	}
	
	public BaseTable createEmailTable(PDDocument document, PDPage page) throws Exception {
		float tableStartPosition = DEFAULT_Y_TABLE_START_POSITION  - this.creditCardTable.getHeaderAndDataHeight() - 40.0f;

		BaseTable table = new BaseTable(tableStartPosition, this.getDefaultReportPageStartYposition(page),
				this.DEFAULT_BOTTOM_MARGIN, this.getDefaultTableWidth(page), this.DEFAULT_MARGIN, document, page, true,
				this.REPORT_DRAW_CONTENT);

		return table;
	}
	
	public BaseTable createPhoneTable(PDDocument document, PDPage page) throws Exception {
		float tableStartPosition = DEFAULT_Y_TABLE_START_POSITION  - this.creditCardTable.getHeaderAndDataHeight() - this.emailTable.getHeaderAndDataHeight() - 60.0f;

		BaseTable table = new BaseTable(tableStartPosition, this.getDefaultReportPageStartYposition(page),
				this.DEFAULT_BOTTOM_MARGIN, this.getDefaultTableWidth(page), this.DEFAULT_MARGIN, document, page, true,
				this.REPORT_DRAW_CONTENT);

		return table;
	}
	
	public BaseTable createFlightHistoryTable(PDDocument document, PDPage page) throws Exception {
		float tableStartPosition = DEFAULT_Y_TABLE_START_POSITION - this.creditCardTable.getHeaderAndDataHeight()
				- this.emailTable.getHeaderAndDataHeight()- this.phoneTable.getHeaderAndDataHeight() - 80.0f;

		BaseTable table = new BaseTable(tableStartPosition, this.getDefaultReportPageStartYposition(page),
				this.DEFAULT_BOTTOM_MARGIN, this.getDefaultTableWidth(page), this.DEFAULT_MARGIN, document, page, true,
				this.REPORT_DRAW_CONTENT);

		return table;
	}
	
	public BaseTable createEventNotesTable(PDDocument document, PDPage page) throws Exception {
		float tableStartPosition = DEFAULT_Y_TABLE_START_POSITION - this.creditCardTable.getHeaderAndDataHeight()
				- this.emailTable.getHeaderAndDataHeight()- this.phoneTable.getHeaderAndDataHeight() - this.flightHistoryTable.getHeaderAndDataHeight() - 100.0f;

		BaseTable table = new BaseTable(tableStartPosition, this.getDefaultReportPageStartYposition(page),
				this.DEFAULT_BOTTOM_MARGIN, this.getDefaultTableWidth(page), this.DEFAULT_MARGIN, document, page, true,
				this.REPORT_DRAW_CONTENT);

		return table;
	}
	
	
	public BaseTable createEventNoteHistoryTable(PDDocument document, PDPage page) throws Exception {
		float tableStartPosition = DEFAULT_Y_TABLE_START_POSITION - this.creditCardTable.getHeaderAndDataHeight()
				- this.emailTable.getHeaderAndDataHeight()- this.phoneTable.getHeaderAndDataHeight() - this.flightHistoryTable.getHeaderAndDataHeight() - this.eventNotesTable.getHeaderAndDataHeight() - 120.0f;

		BaseTable table = new BaseTable(tableStartPosition, this.getDefaultReportPageStartYposition(page),
				this.DEFAULT_BOTTOM_MARGIN, this.getDefaultTableWidth(page), this.DEFAULT_MARGIN, document, page, true,
				this.REPORT_DRAW_CONTENT);

		return table;
	}

	public void setBottomFieldLabelProperties(Cell<PDPage> cell) {
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setFont(this.BOLD_TIMES_ROMAN);
		cell.setLeftBorderStyle(new LineStyle(Color.WHITE, 0));
		cell.setTopBorderStyle(new LineStyle(Color.WHITE, 0));
		cell.setRightBorderStyle(new LineStyle(Color.WHITE, 0));
		cell.setBottomBorderStyle(new LineStyle(DEFAULT_BORDER_COLOR, 1));
	}

	public void setBottomFieldValueProperties(Cell<PDPage> cell) {
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setTextColor(DEFAULT_CONTENT_TEXT_COLOR);
		cell.setLeftBorderStyle(new LineStyle(Color.WHITE, 0));
		cell.setTopBorderStyle(new LineStyle(Color.WHITE, 0));
		cell.setRightBorderStyle(new LineStyle(Color.WHITE, 0));
		cell.setBottomBorderStyle(new LineStyle(DEFAULT_BORDER_COLOR, 1));
	}

	// set the properties of the report header cell
	public void setPassengerDetailHeaderLabelProperties(Cell<PDPage> cell) {
		cell.setFont(this.BOLD_TIMES_ROMAN);
		cell.setFontSize(12);
		cell.setValign(VerticalAlignment.MIDDLE);
		cell.setAlign(HorizontalAlignment.RIGHT);
		cell.setLeftBorderStyle(new LineStyle(Color.WHITE, 0));
		cell.setTopBorderStyle(new LineStyle(Color.WHITE, 0));
		cell.setRightBorderStyle(new LineStyle(Color.WHITE, 0));
		cell.setBottomBorderStyle(new LineStyle(Color.WHITE, 0));
	}

	public void setBottomPassengerDetailHeaderLabelProperties(Cell<PDPage> cell) {
		cell.setFont(this.BOLD_TIMES_ROMAN);
		cell.setFontSize(12);
		cell.setValign(VerticalAlignment.MIDDLE);
		cell.setAlign(HorizontalAlignment.RIGHT);
		cell.setLeftBorderStyle(new LineStyle(Color.WHITE, 0));
		cell.setTopBorderStyle(new LineStyle(Color.WHITE, 0));
		cell.setRightBorderStyle(new LineStyle(Color.WHITE, 0));
		cell.setBottomBorderStyle(new LineStyle(Color.WHITE, 0));
	}

	public void setPassengerDetailHeaderValueProperties(Cell<PDPage> cell) {

		cell.setFont(this.BOLD_TIMES_ROMAN);
		cell.setFontSize(12);
		cell.setValign(VerticalAlignment.MIDDLE);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setTopBorderStyle(new LineStyle(Color.WHITE, 0));
		cell.setRightBorderStyle(new LineStyle(Color.WHITE, 0));
		cell.setBottomBorderStyle(new LineStyle(Color.WHITE, 0));
		cell.setTextColor(DEFAULT_CONTENT_TEXT_COLOR);
		cell.setLeftPadding(5);
	}

	public void setBottomPassengerDetailHeaderValueProperties(Cell<PDPage> cell) {

		cell.setFont(this.BOLD_TIMES_ROMAN);
		cell.setFontSize(12);
		cell.setValign(VerticalAlignment.MIDDLE);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setTopBorderStyle(new LineStyle(Color.WHITE, 0));
		cell.setRightBorderStyle(new LineStyle(Color.WHITE, 0));
		cell.setBottomBorderStyle(new LineStyle(Color.WHITE,0));
		cell.setTextColor(DEFAULT_CONTENT_TEXT_COLOR);
		cell.setLeftPadding(5);
	}
	
	private String reportDateFormatter(Date date)
	{
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		String formattedDateStr = "";
		if(date == null)
			return formattedDateStr;
		else
			return dateFormatter.format(date);
		 
	}
	
	private String reportDateTimeFormatter(Date date)
	{
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedDateStr = "";
		if(date == null)
			return formattedDateStr;
		else
			return dateFormatter.format(date);
		 
	}
	
	private String replaceTrimChars(String str)
	{
		if(str!=null && !str.isEmpty())
		{
			str = str.replace("$", "");
			str = str.replace("*", "");
			str = str.replace("?", "");
			str = str.replace("^", "");
			str = str.replace("+", "");
			str = str.replace("'", "");
		}
		
		return str;
	}
	
	private String changeToString (Object value	) 
	{
		if(value == null )
			return "";
		else
			return String.valueOf(value);
		
					
	}
	
	private String removeSpecialCharacters(String str)
	{
		
		if(str!=null  && !str.isEmpty())
		{
		
			str = str.replaceAll("\\n", "");
			str = str.replaceAll("\\r", "");
			return str;
			
		}
		
		return str;
	}
	
	private String getNoteTypeAsString (Set<NoteTypeVo> noteTypeSet)
	{
		String result = "";
		
		if(noteTypeSet!=null && !noteTypeSet.isEmpty())
		{
			int i=0;
			for(NoteTypeVo noteTypeVo : noteTypeSet)
			{
				if(i==0)
					result = result + noteTypeVo.getNoteType();
				else
					result = result + ", " + noteTypeVo.getNoteType() ;
				
			}
			
		}
		
		return result;
	}

}
