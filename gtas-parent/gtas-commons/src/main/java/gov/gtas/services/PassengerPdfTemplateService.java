package gov.gtas.services;

import java.awt.Color;
import java.io.IOException;
import java.util.Date;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.VerticalAlignment;
import be.quodlibet.boxable.line.LineStyle;

public abstract class PassengerPdfTemplateService {
	
	protected String fileName;
	protected String reportName;
	protected Date reportDate;
	protected PDDocument document;
	protected final float  HEADER_ROW_HEIGHT = 50;
	protected final float  COVER_HEADER_ROW_HEIGHT = 17;
	protected final float  DEFAULT_HEADER_ROW_HEIGHT = 40;
	protected final float  HEADER_CELL_WIDTH = 100;
	protected final float  DEFAULT_HEADER_CELL_WIDTH = 100;
	protected final float DEFAULT_CONTENT_FONT_SIZE = 10;
	protected final float DEFAULT_HORIZONTAL_SECTION_HEADER_FONT = 14;
	protected final float DEFAULT_SINGLE_ROW_LABEL_FONT_SIZE = 14;
	protected final Color DEFAULT_HORIZONTAL_SECTION_HEADER_TEXT_COLOR =Color.WHITE;
	protected final Color DEFAULT_CONTENT_TEXT_COLOR = Color.BLACK;
	protected final Color DEFAULT_GRAY_ROW_COLOR = new Color (220,220,220);
	protected final float DEFAULT_LABEL_FONT_SIZE = 14;
	protected final float DEFAULT_ROW_HEIGHT = 20;
	protected final float REPORT_COVER_MARGIN = 50;
	protected final float REPORT_COVER_BOTTOM_MARGIN = 70;
	protected final float DEFAULT_MARGIN = 50;
	protected final float DEFAULT_BOTTOM_MARGIN = 50;
	protected final float REPORT_COVER_Y_TABLE_START_POSITION = 550;
	protected final float DEFAULT_Y_TABLE_START_POSITION = 800;
	protected final boolean REPORT_DRAW_CONTENT = true;
	protected PDFont BOLD_TIMES_ROMAN = PDType1Font.TIMES_BOLD;
	protected PDFont PLAIN_TIMES_ROMAN = PDType1Font.TIMES_ROMAN;
	protected PDFont PLAIN_NOTES_CONTENT_FONT = PDType1Font.COURIER;
	protected Color DEFAULT_BORDER_COLOR =  new Color (220,220,220);
	protected final Color DEFAULT_LABEL_BACKGROUND_COLOR = new Color(0,51,102);
	

	protected PDDocument getDefaultA4ReportDocument(PDPage coverPage)
	{
		
        document = new PDDocument();
        document.addPage(coverPage);
        return document;

	}
	
	protected PDPageContentStream getDefaultContentStream(PDPage page) throws IOException
	{
		  if(document==null || page == null)
			  throw new IOException();

		  PDPageContentStream pDPageContentStream = new PDPageContentStream(this.document, page);
		  
		  return pDPageContentStream;
	}
	
	//set the properties of the report header cell
	protected void setHeaderCellProperties( Cell<PDPage> cell )
	{
        cell.setFont(this.BOLD_TIMES_ROMAN);
        cell.setFontSize(16);
        cell.setValign(VerticalAlignment.MIDDLE);
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setTopBorderStyle(new LineStyle(DEFAULT_BORDER_COLOR, 10));
        cell.setLeftBorderStyle(new LineStyle(DEFAULT_BORDER_COLOR,1));
        cell.setRightBorderStyle(new LineStyle(DEFAULT_BORDER_COLOR,1));
        cell.setBottomBorderStyle(new LineStyle(DEFAULT_BORDER_COLOR,1));
	}
	
	
    protected  Row<PDPage> getRow( BaseTable table)
    {
    	return table.createRow(DEFAULT_ROW_HEIGHT);
    	
    }
    protected  Row<PDPage> getRow( BaseTable table, float rowHeight)
    {
    	return table.createRow(rowHeight);
    	
    }
   
    
    protected Cell<PDPage> createCell( Row<PDPage> row,  float cellWidth, String rowValue)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, rowValue);
        cell.setFontSize(DEFAULT_CONTENT_FONT_SIZE);
        cell.setFont(PLAIN_TIMES_ROMAN);
        cell.setTopBorderStyle(new LineStyle(DEFAULT_BORDER_COLOR, 1));
        cell.setLeftBorderStyle(new LineStyle(DEFAULT_BORDER_COLOR,1));
        cell.setRightBorderStyle(new LineStyle(DEFAULT_BORDER_COLOR,1));
        cell.setBottomBorderStyle(new LineStyle(DEFAULT_BORDER_COLOR,1));
    	return cell;
    	
    }
    protected Cell<PDPage> createHorizontalColumnLabelCell(  Row<PDPage> row,  float cellWidth, String rowValue)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, rowValue);
        cell.setFontSize(DEFAULT_CONTENT_FONT_SIZE);
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setFont(this.BOLD_TIMES_ROMAN);
        cell.setLeftBorderStyle(new LineStyle(DEFAULT_LABEL_BACKGROUND_COLOR,0));
        cell.setTopBorderStyle(new LineStyle(DEFAULT_LABEL_BACKGROUND_COLOR,0));
        cell.setRightBorderStyle(new LineStyle(DEFAULT_LABEL_BACKGROUND_COLOR,0));
        cell.setBottomBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setFillColor(DEFAULT_LABEL_BACKGROUND_COLOR);
        cell.setTextColor(Color.WHITE);
    	return cell;
    	
    }
    
    
    
    protected Cell<PDPage> creatPlainFieldLabelCell( Row<PDPage> row,  float cellWidth, String rowValue)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, rowValue);
        cell.setFontSize(DEFAULT_CONTENT_FONT_SIZE);
        cell.setAlign(HorizontalAlignment.LEFT);
        cell.setFont(this.BOLD_TIMES_ROMAN);
        cell.setLeftBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setTopBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setRightBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setBottomBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setTextColor(Color.BLACK);
        
        
    	return cell;
    	
    }
    
    protected Cell<PDPage> creatVerticalColumnLabelCell( Row<PDPage> row,  float cellWidth, String rowValue)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, rowValue);
        cell.setFontSize(DEFAULT_CONTENT_FONT_SIZE);
        cell.setAlign(HorizontalAlignment.LEFT);
        cell.setFont(this.BOLD_TIMES_ROMAN);
        cell.setLeftBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,0));
        cell.setTopBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setRightBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,0));
        cell.setBottomBorderStyle(new LineStyle(Color.WHITE,1));
        cell.setFillColor(DEFAULT_GRAY_ROW_COLOR);
        cell.setTextColor(Color.BLACK);
        
        
    	return cell;
    	
    }
    
    protected Cell<PDPage> createFirstVerticalColumnValueCell( Row<PDPage> row,  float cellWidth, String rowValue)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, rowValue);
        cell.setFontSize(DEFAULT_CONTENT_FONT_SIZE);
        cell.setFont(PLAIN_TIMES_ROMAN);
        cell.setAlign(HorizontalAlignment.LEFT);
        cell.setTextColor(DEFAULT_CONTENT_TEXT_COLOR);
        cell.setLeftBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,0));
        cell.setTopBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,0));
        cell.setRightBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
        cell.setBottomBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
        cell.setValign(VerticalAlignment.BOTTOM);
    	return cell;
    	
    }
    
    protected Cell<PDPage> createVerticalColumnValueCell( Row<PDPage> row,  float cellWidth, String rowValue)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, rowValue);
        cell.setFontSize(DEFAULT_CONTENT_FONT_SIZE);
        cell.setFont(PLAIN_TIMES_ROMAN);
        cell.setAlign(HorizontalAlignment.LEFT);
        cell.setTextColor(DEFAULT_CONTENT_TEXT_COLOR);
        cell.setLeftBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setTopBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setRightBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
        cell.setBottomBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
        cell.setValign(VerticalAlignment.BOTTOM);
    	return cell;
    	
    }
    
   
    
    protected Cell<PDPage> createLastVerticalColumnValueCell( Row<PDPage> row,  float cellWidth, String rowValue)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, rowValue);
        cell.setFontSize(DEFAULT_CONTENT_FONT_SIZE);
        cell.setFont(PLAIN_TIMES_ROMAN);
        cell.setAlign(HorizontalAlignment.LEFT);
        cell.setTextColor(DEFAULT_CONTENT_TEXT_COLOR);
        cell.setLeftBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setTopBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setRightBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
        cell.setBottomBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
        cell.setValign(VerticalAlignment.BOTTOM);
    	return cell;
    	
    }
    
    

    

    
    
    protected Cell<PDPage> createFieldValueCell( Row<PDPage> row,  float cellWidth, String rowValue)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, rowValue);
        cell.setFontSize(DEFAULT_CONTENT_FONT_SIZE);
        cell.setFont(PLAIN_TIMES_ROMAN);
        cell.setAlign(HorizontalAlignment.LEFT);
        cell.setTextColor(DEFAULT_CONTENT_TEXT_COLOR);
        cell.setLeftBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setTopBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setRightBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setBottomBorderStyle(new LineStyle(Color.WHITE,0));
    	return cell;
    	
    }
    
    
    
    
    
    protected Cell<PDPage> createFirstColoredCell( Row<PDPage> row,  float cellWidth, String rowValue, int index)
    {
    	
    	Cell<PDPage> cell= row.createCell(cellWidth, rowValue);
    	if(index%2 == 0)
    	{
    		 	cell.setFontSize(DEFAULT_CONTENT_FONT_SIZE);
    	        cell.setFont(PLAIN_TIMES_ROMAN);
    	        cell.setTopBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR, 1));
    	        cell.setLeftBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
    	        cell.setRightBorderStyle(new LineStyle(Color.WHITE,0));
    	        cell.setBottomBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
    	        cell.setFillColor(Color.WHITE);
    	        cell.setAlign(HorizontalAlignment.CENTER);
    	        cell.setTextColor(DEFAULT_CONTENT_TEXT_COLOR);
    	}
    	else
    	{
    		cell.setFontSize(DEFAULT_CONTENT_FONT_SIZE);
            cell.setFont(PLAIN_TIMES_ROMAN);
            cell.setTopBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR, 1));
            cell.setLeftBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
            cell.setRightBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
            cell.setBottomBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setFillColor(DEFAULT_GRAY_ROW_COLOR);
            cell.setTextColor(DEFAULT_CONTENT_TEXT_COLOR);
    		
    		
    		
    	}
    	return cell;
    	
    }
    
 
    protected Cell<PDPage> createColoredCell (Row<PDPage> row,  float cellWidth, String rowValue, int index)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, rowValue);
      
    	if(index%2 == 0)
    	{
            cell.setFontSize(DEFAULT_CONTENT_FONT_SIZE);
            cell.setFont(PLAIN_TIMES_ROMAN);
            cell.setTopBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR, 1));
            cell.setLeftBorderStyle(new LineStyle(Color.WHITE,0));
            cell.setRightBorderStyle(new LineStyle(Color.WHITE,0));
            cell.setBottomBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
            cell.setFillColor(Color.WHITE);
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setTextColor(DEFAULT_CONTENT_TEXT_COLOR);
    		
    	}
    	else
    	{
    			cell.setFontSize(DEFAULT_CONTENT_FONT_SIZE);
    	        cell.setFont(PLAIN_TIMES_ROMAN);
    	        cell.setTopBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR, 1));
    	        cell.setLeftBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
    	        cell.setRightBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
    	        cell.setBottomBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
    	        cell.setAlign(HorizontalAlignment.CENTER);
    	        cell.setFillColor(DEFAULT_GRAY_ROW_COLOR);
    	        cell.setTextColor(DEFAULT_CONTENT_TEXT_COLOR);
    		
    	}
    	
    	
    	return cell;
    	
    }
    
    protected Cell<PDPage> createLastColoredCell( Row<PDPage> row,  float cellWidth, String rowValue, int index)
    {
    	
       
    	Cell<PDPage> cell= row.createCell(cellWidth, rowValue);
        
    	if(index%2 == 0)
    	{
    		cell.setFontSize(DEFAULT_CONTENT_FONT_SIZE);
            cell.setFont(PLAIN_TIMES_ROMAN);
            cell.setTopBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR, 1));
            cell.setLeftBorderStyle(new LineStyle(Color.WHITE,0));
            cell.setRightBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
            cell.setBottomBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
            cell.setFillColor(Color.WHITE);
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setTextColor(DEFAULT_CONTENT_TEXT_COLOR);
    		
    	}
    	else
    	{
    		cell.setFontSize(DEFAULT_CONTENT_FONT_SIZE);
            cell.setFont(PLAIN_TIMES_ROMAN);
            cell.setTopBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR, 1));
            cell.setLeftBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
            cell.setRightBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
            cell.setBottomBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
            cell.setAlign(HorizontalAlignment.CENTER);
            cell.setFillColor(DEFAULT_GRAY_ROW_COLOR);
            cell.setTextColor(DEFAULT_CONTENT_TEXT_COLOR);
    		
    		
    	}
    	
    	return cell;
    	
    }
    
    protected Cell<PDPage> createFirstNoteColumnLabelCell( Row<PDPage> row,  float cellWidth, String rowValue)
    {
    			Cell<PDPage> cell= row.createCell(cellWidth, rowValue);
    		 	cell.setFontSize(DEFAULT_CONTENT_FONT_SIZE);
    	        cell.setFont(this.BOLD_TIMES_ROMAN);
    	        cell.setTopBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR, 1));
    	        cell.setLeftBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
    	        cell.setRightBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,0));
    	        cell.setBottomBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
    	        cell.setFillColor(DEFAULT_GRAY_ROW_COLOR);
    	        cell.setAlign(HorizontalAlignment.LEFT);
    	        cell.setTextColor(DEFAULT_CONTENT_TEXT_COLOR);
 
    	
    	return cell;
    	
    }
    
    protected Cell<PDPage> createNoteColumnLabelCell( Row<PDPage> row,  float cellWidth, String rowValue)
    {
    			Cell<PDPage> cell= row.createCell(cellWidth, rowValue);
    		 	cell.setFontSize(DEFAULT_CONTENT_FONT_SIZE);
    	        cell.setFont(this.BOLD_TIMES_ROMAN);
    	        cell.setTopBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR, 1));
    	        cell.setLeftBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,0));
    	        cell.setRightBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,0));
    	        cell.setBottomBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
    	        cell.setFillColor(DEFAULT_GRAY_ROW_COLOR);
    	        cell.setAlign(HorizontalAlignment.LEFT);
    	        cell.setTextColor(DEFAULT_CONTENT_TEXT_COLOR);
 
    	
    	return cell;
    	
    }
    
    protected Cell<PDPage> createLastNoteColumnLabelCell( Row<PDPage> row,  float cellWidth, String rowValue)
    {
    			Cell<PDPage> cell= row.createCell(cellWidth, rowValue);
    		 	cell.setFontSize(DEFAULT_CONTENT_FONT_SIZE);
    	        cell.setFont(this.BOLD_TIMES_ROMAN);
    	        cell.setTopBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR, 1));
    	        cell.setLeftBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,0));
    	        cell.setRightBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
    	        cell.setBottomBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
    	        cell.setFillColor(DEFAULT_GRAY_ROW_COLOR);
    	        cell.setAlign(HorizontalAlignment.LEFT);
    	        cell.setTextColor(DEFAULT_CONTENT_TEXT_COLOR);
 
    	
    	return cell;
    	
    }
    
    protected Cell<PDPage> createNoteColumnValueCell( Row<PDPage> row,  float cellWidth, String rowValue)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, rowValue);		
    	cell.setFontSize(DEFAULT_CONTENT_FONT_SIZE);
        cell.setFont(PLAIN_TIMES_ROMAN);
        cell.setTopBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
        cell.setLeftBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setRightBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setBottomBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
        cell.setFillColor(Color.WHITE);
        cell.setAlign(HorizontalAlignment.LEFT);
        cell.setTextColor(DEFAULT_CONTENT_TEXT_COLOR);
    	
    	return cell;
    	
    }
    
    protected Cell<PDPage> createLastNoteColumnValueCell( Row<PDPage> row,  float cellWidth, String rowValue)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, rowValue);		
    	cell.setFontSize(DEFAULT_CONTENT_FONT_SIZE);
        cell.setFont(PLAIN_TIMES_ROMAN);
        cell.setTopBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR, 1));
        cell.setLeftBorderStyle(new LineStyle(Color.WHITE, 0));
        cell.setRightBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR, 1));
        cell.setBottomBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
        cell.setFillColor(Color.WHITE);
        cell.setAlign(HorizontalAlignment.LEFT);
        cell.setTextColor(DEFAULT_CONTENT_TEXT_COLOR);
    	
    	return cell;
    	
    }
    
    protected Cell<PDPage> createNoteContentCell(  Row<PDPage> row,  float cellWidth, String rowValue)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, rowValue);		
    	cell.setFontSize(DEFAULT_CONTENT_FONT_SIZE);
        cell.setFont(PLAIN_NOTES_CONTENT_FONT);
        cell.setTopBorderStyle(new LineStyle(Color.WHITE,  0));
        cell.setLeftBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
        cell.setRightBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
        cell.setBottomBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,10));
        cell.setFillColor(Color.WHITE);
        cell.setAlign(HorizontalAlignment.LEFT);
        cell.setTextColor(Color.BLUE);
    	
    	return cell;
    	
    }
    
 
	
    protected Cell<PDPage> createCell(  Row<PDPage> row, float cellWidth, PDFont font,  String rowValue)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, rowValue);
        cell.setFontSize(DEFAULT_CONTENT_FONT_SIZE);
        cell.setFont(font);
        cell.setTopBorderStyle(new LineStyle(DEFAULT_BORDER_COLOR, 1));
        cell.setLeftBorderStyle(new LineStyle(DEFAULT_BORDER_COLOR,1));
        cell.setRightBorderStyle(new LineStyle(DEFAULT_BORDER_COLOR,1));
        cell.setBottomBorderStyle(new LineStyle(DEFAULT_BORDER_COLOR,1));
    	return cell;
    	
    }
    protected Cell<PDPage> createCell(  Row<PDPage> row,  float cellWidth, LineStyle bottomBorderStyle, String rowValue)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, rowValue);
        cell.setFontSize(DEFAULT_CONTENT_FONT_SIZE);
        cell.setFont(PLAIN_TIMES_ROMAN);
        cell.setBottomBorderStyle(bottomBorderStyle);
        cell.setTopBorderStyle(new LineStyle(DEFAULT_BORDER_COLOR, 1));
        cell.setLeftBorderStyle(new LineStyle(DEFAULT_BORDER_COLOR,1));
        cell.setRightBorderStyle(new LineStyle(DEFAULT_BORDER_COLOR,1));
    	return cell;
    	
    }
	
    protected Cell<PDPage> createCell(  Row<PDPage> row, float cellWidth, PDFont font, float fontSize, String rowValue)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, rowValue);
        cell.setFontSize(fontSize);
        cell.setFont(font);
        cell.setBottomBorderStyle(new LineStyle(DEFAULT_BORDER_COLOR, 1));
        cell.setTopBorderStyle(new LineStyle(DEFAULT_BORDER_COLOR, 1));
        cell.setLeftBorderStyle(new LineStyle(DEFAULT_BORDER_COLOR,1));
        cell.setRightBorderStyle(new LineStyle(DEFAULT_BORDER_COLOR,1));
    	return cell;
    	
    }
	
    protected Cell<PDPage> createCell( Row<PDPage> row, float cellWidth, PDFont font, float fontSize,LineStyle bottomBorderStyle, String rowValue)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, rowValue);
        cell.setFontSize(fontSize);
        cell.setFont(font);
        cell.setBottomBorderStyle(bottomBorderStyle);
        cell.setTopBorderStyle(new LineStyle(DEFAULT_BORDER_COLOR, 1));
        cell.setLeftBorderStyle(new LineStyle(DEFAULT_BORDER_COLOR,1));
        cell.setRightBorderStyle(new LineStyle(DEFAULT_BORDER_COLOR,1));
    	return cell;
    	
    }
    
    protected Cell<PDPage> createCell( Row<PDPage> row, float cellWidth, PDFont font, float fontSize,LineStyle borderStyle, Color textColor, Color fillColor, String rowValue)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, rowValue);
        cell.setFont(font);
        cell.setFontSize(fontSize);
        cell.setBorderStyle(borderStyle);
        cell.setTextColor(textColor);
        cell.setFillColor(fillColor);
    	return cell;
    	
    }
    
    
    protected Cell<PDPage> createEmptyWhiteCell( Row<PDPage> row, float cellWidth)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, "");
        cell.setLeftBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setTopBorderStyle(new LineStyle(Color.WHITE, 0));
        cell.setRightBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setBottomBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setFillColor(Color.WHITE);
    	return cell;
    	
    }
    
    protected Cell<PDPage> createReportCoverLabelCell( Row<PDPage> row,  float cellWidth, String rowValue)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, rowValue);
        cell.setFontSize(this.DEFAULT_CONTENT_FONT_SIZE);
        cell.setFont(BOLD_TIMES_ROMAN);
        cell.setTopBorderStyle(new LineStyle(this.DEFAULT_GRAY_ROW_COLOR, 0));
        cell.setLeftBorderStyle(new LineStyle(this.DEFAULT_GRAY_ROW_COLOR,1));
        cell.setRightBorderStyle(new LineStyle(this.DEFAULT_GRAY_ROW_COLOR,1));
        cell.setBottomBorderStyle(new LineStyle(Color.WHITE,1));
        cell.setFillColor(this.DEFAULT_GRAY_ROW_COLOR);
    	return cell;
    	
    }
    
    protected Cell<PDPage> createBorderlessCell( Row<PDPage> row,  float cellWidth, String rowValue)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, rowValue);
        cell.setFontSize(this.DEFAULT_CONTENT_FONT_SIZE);
        cell.setFont(PLAIN_TIMES_ROMAN);
        cell.setTopBorderStyle(new LineStyle(Color.WHITE, 0));
        cell.setLeftBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setRightBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setBottomBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setFillColor(Color.WHITE);
    	return cell;
    	
    }
    
    protected Cell<PDPage> createValueCellWithRighBottomtBorder( Row<PDPage> row,  float cellWidth, String rowValue)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, rowValue);
        cell.setFontSize(this.DEFAULT_CONTENT_FONT_SIZE);
        cell.setFont(PLAIN_TIMES_ROMAN);
        cell.setTopBorderStyle(new LineStyle(Color.WHITE, 0));
        cell.setLeftBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setRightBorderStyle(new LineStyle(this.DEFAULT_GRAY_ROW_COLOR,1));
        cell.setBottomBorderStyle(new LineStyle(this.DEFAULT_GRAY_ROW_COLOR,1));
        cell.setFillColor(Color.WHITE);
        cell.setValign(VerticalAlignment.BOTTOM );
    	return cell;
    	
    }
    
    protected Cell<PDPage> createEmptyClosingRightCell( Row<PDPage> row,  float cellWidth)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, "");
        cell.setFontSize(this.DEFAULT_CONTENT_FONT_SIZE);
        cell.setFont(PLAIN_TIMES_ROMAN);
        cell.setTopBorderStyle(new LineStyle(Color.WHITE, 0));
        cell.setLeftBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setRightBorderStyle(new LineStyle(Color.LIGHT_GRAY,1));
        cell.setBottomBorderStyle(new LineStyle(Color.LIGHT_GRAY,1));
        cell.setFillColor(Color.WHITE);
    	return cell;
    	
    }
    protected Cell<PDPage> createEmptyClosingLeftCell( Row<PDPage> row,  float cellWidth)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, "");
        cell.setFontSize(this.DEFAULT_CONTENT_FONT_SIZE);
        cell.setFont(PLAIN_TIMES_ROMAN);
        cell.setTopBorderStyle(new LineStyle(Color.WHITE, 0));
        cell.setLeftBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setRightBorderStyle(new LineStyle(Color.WHITE,0));
        cell.setBottomBorderStyle(new LineStyle(Color.LIGHT_GRAY,1));
        cell.setFillColor(Color.WHITE);
    	return cell;
    	
    }
    
    
    protected Cell<PDPage> createSingleRowLabelCell(  Row<PDPage> row,  float cellWidth, String rowValue)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, rowValue);
        cell.setFontSize(this.DEFAULT_SINGLE_ROW_LABEL_FONT_SIZE);
        cell.setAlign(HorizontalAlignment.LEFT);
        cell.setFont(this.BOLD_TIMES_ROMAN);
        cell.setLeftBorderStyle(new LineStyle(DEFAULT_LABEL_BACKGROUND_COLOR,0));
        cell.setTopBorderStyle(new LineStyle(DEFAULT_LABEL_BACKGROUND_COLOR,0));
        cell.setRightBorderStyle(new LineStyle(DEFAULT_LABEL_BACKGROUND_COLOR,0));
        cell.setBottomBorderStyle(new LineStyle(DEFAULT_LABEL_BACKGROUND_COLOR,0));
        cell.setFillColor(DEFAULT_LABEL_BACKGROUND_COLOR);
        cell.setTextColor(Color.WHITE);
    	return cell;
    	
    }
    
    protected Cell<PDPage> createSingleRowValueCell( Row<PDPage> row,  float cellWidth, String rowValue)
    {
    	Cell<PDPage> cell= row.createCell(cellWidth, rowValue);
        cell.setFontSize(this.DEFAULT_CONTENT_FONT_SIZE);
        cell.setAlign(HorizontalAlignment.LEFT);
        cell.setFont(this.PLAIN_TIMES_ROMAN);
        cell.setLeftBorderStyle(new LineStyle(DEFAULT_LABEL_BACKGROUND_COLOR,0));
        cell.setTopBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
        cell.setRightBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
        cell.setBottomBorderStyle(new LineStyle(DEFAULT_GRAY_ROW_COLOR,1));
        cell.setFillColor(Color.WHITE);
        cell.setTextColor(DEFAULT_CONTENT_TEXT_COLOR);
    	return cell;
    	
    }
    
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}
	
	protected float getDefaultReportCoverTableWidth(PDPage page) throws Exception
	{
		if(page==null)
			 throw new Exception();
		return page.getMediaBox().getWidth() - (2 * this.REPORT_COVER_MARGIN);
	}
	
	
	protected float getDefaultReportCoverPageStartYposition(PDPage page) throws Exception
	{
		if(page==null)
			 throw new Exception();
		
		return page.getMediaBox().getHeight() - (2 *  this.REPORT_COVER_MARGIN);
	}
	
	protected float getDefaultTableWidth(PDPage page) throws Exception
	{
		if(page==null)
			 throw new Exception();
		return page.getMediaBox().getWidth() - (2 * this.DEFAULT_MARGIN);
	}
	
	protected float getDefaultReportPageStartYposition(PDPage page) throws Exception
	{
		if(page==null)
			 throw new Exception();
		
		return page.getMediaBox().getHeight() - (2 *  this.DEFAULT_MARGIN);
	}

}
