package gov.gtas.services.dto;

import java.util.Set;

import javax.validation.constraints.Email;

public class EmailDTO {
	@Email
    private String [] to;
	private String subject;
    private String body;
    private String pathToAttachment;

    
    

	public String[] getTo() {
		return to;
	}

	public void setTo(String[] to) {
		this.to = to;
	}

	public String getPathToAttachment() {
		return pathToAttachment;
	}

	public void setPathToAttachment(String pathToAttachment) {
		this.pathToAttachment = pathToAttachment;
	}	

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	public boolean hasAttachment() {
		return pathToAttachment != null;
	}

}

