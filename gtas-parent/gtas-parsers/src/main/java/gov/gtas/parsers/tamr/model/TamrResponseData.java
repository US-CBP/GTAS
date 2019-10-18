/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.tamr.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TamrResponseData {
	private String tamrId;
	private Long gtasId;
	private double version;
	private double score;
	
	@JsonIgnore
	private List<Object> derogIds;
	
	public TamrResponseData() {

	}

	public String getTamrId() {
		return tamrId;
	}

	public void setTamrId(String tamrId) {
		this.tamrId = tamrId;
	}

	public Long getGtasId() {
		return gtasId;
	}

	public void setGtasId(Long gtasId) {
		this.gtasId = gtasId;
	}

	public double getVersion() {
		return version;
	}

	public void setVersion(double version) {
		this.version = version;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public List<Object> getDerogs() {
		return derogIds;
	}

	public void setDerogs(List<Object> derogs) {
		this.derogIds = derogs;
	}
	
	
}
