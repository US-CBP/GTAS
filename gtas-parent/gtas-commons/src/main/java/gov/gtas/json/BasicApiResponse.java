package gov.gtas.json;

import gov.gtas.enumtype.Status;

public class BasicApiResponse {
	private Status status;
	private String message;
	
	public BasicApiResponse(Status status, String message) {
		this.status = status;
		this.message = message;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
