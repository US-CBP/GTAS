package gov.gtas.email.dto;

import gov.gtas.model.ErrorRecord;

public class ErrorRecordDto {
	private Long errorId;
	private String errorCode;
	private Long timestamp;
	private String errorDescription;
	
	public Long getErrorId() {
		return errorId;
	}
	public void setErrorId(Long errorId) {
		this.errorId = errorId;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public String getErrorDescription() {
		return errorDescription;
	}
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
	
	
	public static ErrorRecordDto from(ErrorRecord record) {
		ErrorRecordDto dto = new ErrorRecordDto();
		
		dto.setErrorCode(record.getCode());
		dto.setErrorDescription(record.getDescription());
		dto.setErrorId(record.getId());
		dto.setTimestamp(record.getTimestamp().getTime());
		
		return dto;
	}
	
	
}
