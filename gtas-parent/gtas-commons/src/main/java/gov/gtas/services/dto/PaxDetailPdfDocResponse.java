package gov.gtas.services.dto;

import gov.gtas.vo.passenger.PassengerVo;

public class PaxDetailPdfDocResponse extends PdfDocumentResponse{
	
	private PassengerVo passengerVo;
	
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
	public PassengerVo getPassengerVo() {
		return passengerVo;
	}
	public void setPassengerVo(PassengerVo passengerVo) {
		this.passengerVo = passengerVo;
	}
	

}
