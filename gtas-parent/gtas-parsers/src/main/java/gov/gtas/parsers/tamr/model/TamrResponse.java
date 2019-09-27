/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.tamr.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TamrResponse {
	private List<TamrResponseData> travelerQuery;

	@JsonIgnore
	private Object historyClusters;

	private Object acknowledgment;

	private Object error;

	private Object recordErrors;

	public Object getError() {
		return error;
	}

	public void setError(Object error) {
		this.error = error;
	}

	public Object getHistoryClusters() {
		return historyClusters;
	}

	public void setHistoryClusters(Object historyClusters) {
		this.historyClusters = historyClusters;
	}

	public Object getAcknowledgment() {
		return acknowledgment;
	}

	public void setAcknowledgment(Object acknowledgment) {
		this.acknowledgment = acknowledgment;
	}

	public TamrResponse() {

	}

	public List<TamrResponseData> getTravelerQuery() {
		return travelerQuery;
	}

	public void setTravelerQuery(List<TamrResponseData> travelerQuery) {
		this.travelerQuery = travelerQuery;
	}

	public Object getRecordErrors() {
		return recordErrors;
	}

	public void setRecordErrors(Object recordErrors) {
		this.recordErrors = recordErrors;
	}
}
