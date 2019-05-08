/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.tamr.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TamrPassengerSendObject {
	
	@JsonProperty("gtasId")
	private String gtasId;
	
	@JsonProperty("first_name")
	private String first_name;
	
	@JsonProperty("last_name")
	private String last_name;
	
	@JsonProperty("Uid")
	private String Uid;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date ETA_DT;
	
	@JsonProperty("IATA_CARR_CD")
	private String IATA_CARR_CD;
	
	@JsonProperty("FLIT_NBR")
	private String FLIT_NBR;
	
	@JsonProperty("flt")
	private String flt;
	
	@JsonProperty("APIS_DPRTR_APRT_CD")
	private String APIS_DPRTR_APRT_CD;
	
	@JsonProperty("APIS_ARVL_APRT_CD")
	private String APIS_ARVL_APRT_CD;

	@JsonProperty("NATIONALITY_CD")
	private List<String> NATIONALITY_CD;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date DOB_Date;
	
	@JsonProperty("GNDR_CD")
	private String GNDR_CD;
	
	@JsonProperty("documents")
	private List<TamrDocumentSendObject> documents;
	
	public String getGtasId() {
		return gtasId;
	}
	public void setGtasId(String gtasId) {
		this.gtasId = gtasId;
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	
	@JsonProperty("Uid")
	public String getUid() {
		return Uid;
	}
	public void setUid(String uid) {
		Uid = uid;
	}
	@JsonProperty("ETA_DT")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getETA_DT() {
		return ETA_DT;
	}
	public void setETA_DT(Date eTA_DT) {
		ETA_DT = eTA_DT;
	}
	@JsonProperty("IATA_CARR_CD")
	public String getIATA_CARR_CD() {
		return IATA_CARR_CD;
	}
	public void setIATA_CARR_CD(String iATA_CARR_CD) {
		IATA_CARR_CD = iATA_CARR_CD;
	}
	@JsonProperty("FLIT_NBR")
	public String getFLIT_NBR() {
		return FLIT_NBR;
	}
	public void setFLIT_NBR(String fLIT_NBR) {
		FLIT_NBR = fLIT_NBR;
	}
	@JsonProperty("flt")
	public String getFlt() {
		return flt;
	}
	public void setFlt(String flt) {
		this.flt = flt;
	}
	@JsonProperty("APIS_DPRTR_APRT_CD")
	public String getAPIS_DPRTR_APRT_CD() {
		return APIS_DPRTR_APRT_CD;
	}
	public void setAPIS_DPRTR_APRT_CD(String aPIS_DPRTR_APRT_CD) {
		APIS_DPRTR_APRT_CD = aPIS_DPRTR_APRT_CD;
	}
	@JsonProperty("APIS_ARVL_APRT_CD")
	public String getAPIS_ARVL_APRT_CD() {
		return APIS_ARVL_APRT_CD;
	}
	public void setAPIS_ARVL_APRT_CD(String aPIS_ARVL_APRT_CD) {
		APIS_ARVL_APRT_CD = aPIS_ARVL_APRT_CD;
	}
	@JsonProperty("NATIONALITY_CD")
	public List<String> getNATIONALITY_CD() {
		return NATIONALITY_CD;
	}
	public void setNATIONALITY_CD(List<String> NATIONALITY_CD) {
		this.NATIONALITY_CD = NATIONALITY_CD;
	}
	@JsonProperty("DOB_Date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	public Date getDOB_Date() {
		return DOB_Date;
	}
	public void setDOB_Date(Date date) {
		DOB_Date = date;
	}
	@JsonProperty("GNDR_CD")
	public String getGNDR_CD() {
		return GNDR_CD;
	}
	public void setGNDR_CD(String gNDR_CD) {
		GNDR_CD = gNDR_CD;
	}
	@JsonProperty("documents")
	public List<TamrDocumentSendObject> getDocuments() {
		return documents;
	}
	public void setDocuments(List<TamrDocumentSendObject> documents) {
		this.documents = documents;
	}
	
}
