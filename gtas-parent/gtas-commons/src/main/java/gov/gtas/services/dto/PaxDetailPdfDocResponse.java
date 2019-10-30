package gov.gtas.services.dto;

public class PaxDetailPdfDocResponse extends PdfDocumentResponse{
	
	public String getPdfDocumentPath() {
		return this.pdfDocumentPath;
	}
	public void setPdfDocumentPath(String pdfDocumentPath) {
		this.pdfDocumentPath = pdfDocumentPath;
	}
	public String getPdfDocumentSize() {
		return this.pdfDocumentSize;
	}
	public void setPdfDocumentSize(String pdfDocumentSize) {
		this.pdfDocumentSize = pdfDocumentSize;
	}
	public byte[] getFileByteArray() {
		return this.fileByteArray;
	}
	public void setFileByteArray(byte[] fileByteArray) {
		this.fileByteArray = fileByteArray;
	}
	public String getReportFileName() {
		return this.reportFileName;
	}
	public void setReportFileName(String reportFileName) {
		this.reportFileName = reportFileName;
	}
	

}
