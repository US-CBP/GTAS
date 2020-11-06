package gov.gtas.email.dto;

public class EmailNotificationDTO {
	private String note;
	private String [] to;
	private Long paxId;
	
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String[] getTo() {
		return to;
	}
	public void setTo(String[] to) {
		this.to = to;
	}
	public Long getPaxId() {
		return paxId;
	}
	public void setPaxId(Long paxId) {
		this.paxId = paxId;
	}
	
	

}
