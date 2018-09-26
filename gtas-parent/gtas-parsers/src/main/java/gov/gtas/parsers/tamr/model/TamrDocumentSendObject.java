/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.tamr.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TamrDocumentSendObject {
	
	@JsonProperty("DOC_CTRY_CD")
	private String DOC_CTRY_CD;
	
	@JsonProperty("DOC_TYP_NM")
	private String DOC_TYP_NM;
	
	@JsonProperty("DOC_ID")
	private String DOC_ID;
	
	@JsonProperty("DOC_CTRY_CD")
	public String getDOC_CTRY_CD() {
		return DOC_CTRY_CD;
	}
	public void setDOC_CTRY_CD(String dOC_CTRY_CD) {
		DOC_CTRY_CD = dOC_CTRY_CD;
	}
	@JsonProperty("DOC_TYP_NM")
	public String getDOC_TYP_NM() {
		return DOC_TYP_NM;
	}
	public void setDOC_TYP_NM(String dOC_TYP_NM) {
		DOC_TYP_NM = dOC_TYP_NM;
	}
	@JsonProperty("DOC_ID")
	public String getDOC_ID() {
		return DOC_ID;
	}
	public void setDOC_ID(String dOC_ID) {
		DOC_ID = dOC_ID;
	}
}
