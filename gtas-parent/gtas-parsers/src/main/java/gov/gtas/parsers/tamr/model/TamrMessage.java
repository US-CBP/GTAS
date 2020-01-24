/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.tamr.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model that encompasses all types of messages that can be sent from Tamr to
 * GTAS.
 * @author Cassidy Laidlaw
 */
public class TamrMessage {
    private List<TamrTravelerResponse> travelerQuery;

    private List<TamrHistoryCluster> historyClusters;
	
	private Boolean acknowledgment = null;
	private String error;
	
	private List<TamrRecordError> recordErrors;

	public TamrMessage() {
	}

    public List<TamrTravelerResponse> getTravelerQuery() {
        return travelerQuery;
    }

    public void setTravelerQuery(List<TamrTravelerResponse> travelerQuery) {
        this.travelerQuery = travelerQuery;
    }

    public List<TamrHistoryCluster> getHistoryClusters() {
        return historyClusters;
    }

    public void setHistoryClusters(List<TamrHistoryCluster> historyClusters) {
        this.historyClusters = historyClusters;
    }

    public Boolean getAcknowledgment() {
        return acknowledgment;
    }

    public void setAcknowledgment(Boolean acknowledgment) {
        this.acknowledgment = acknowledgment;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<TamrRecordError> getRecordErrors() {
        return recordErrors;
    }

    public void setRecordErrors(List<TamrRecordError> recordErrors) {
        this.recordErrors = recordErrors;
    }
}
