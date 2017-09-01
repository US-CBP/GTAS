/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.dto;

import java.util.ArrayList;
import java.util.List;

import gov.gtas.vo.passenger.LinkPassengerVo;

public class LinkAnalysisDto {
	private final List<LinkPassengerVo> passengers;
	/** total hits returned by search engine */
	private final long totalHits;
	
	private final String error;

	public LinkAnalysisDto(String error){
		this.error = error;
		this.totalHits = 0;
		this.passengers = new ArrayList<>();
	}

	public LinkAnalysisDto(List<LinkPassengerVo> passengers, long totalHits) {
		this.error = null;
		this.totalHits = totalHits;
		this.passengers = passengers;
	}

	public List<LinkPassengerVo> getPassengers() {
		return passengers;
	}

	public long getTotalHits() {
		return totalHits;
	}

	public String getError() {
		return error;
	}	
}