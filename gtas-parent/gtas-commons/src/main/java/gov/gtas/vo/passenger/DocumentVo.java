/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.vo.passenger;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import gov.gtas.model.Document;
import gov.gtas.model.PIIObject;

public class DocumentVo implements PIIObject {
	private String documentType;
	private String documentNumber;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FlightVo.SHORT_DATE_FORMAT)
	private Date expirationDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FlightVo.SHORT_DATE_FORMAT)
	private Date issuanceDate;
	private String issuanceCountry;
	private String firstName;
	private String lastName;
	private String messageType;

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Date getIssuanceDate() {
		return issuanceDate;
	}

	public void setIssuanceDate(Date issuanceDate) {
		this.issuanceDate = issuanceDate;
	}

	public String getIssuanceCountry() {
		return issuanceCountry;
	}

	public void setIssuanceCountry(String issuanceCountry) {
		this.issuanceCountry = issuanceCountry;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public static DocumentVo fromDocument(Document document) {
		DocumentVo docVo = new DocumentVo();
		docVo.setDocumentNumber(document.getDocumentNumber());
		docVo.setDocumentType(document.getDocumentType());
		docVo.setIssuanceCountry(document.getIssuanceCountry());
		docVo.setExpirationDate(document.getExpirationDate());
		docVo.setIssuanceDate(document.getIssuanceDate());
		docVo.setMessageType(document.getMessageType() == null ? "" : document.getMessageType().toString());
		return docVo;
	}

	@Override
	public PIIObject deletePII() {
		this.setDocumentNumber("DELETED");
		this.setFirstName("DELETED");
		this.setLastName("DELETED");
		return this;
	}

	@Override
	public PIIObject maskPII() {
		this.setDocumentNumber("MASKED");
		this.setFirstName("MASKED");
		this.setLastName("MASKED");
		return this;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
}
