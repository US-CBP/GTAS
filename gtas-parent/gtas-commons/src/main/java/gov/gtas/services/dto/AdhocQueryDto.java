/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.dto;

import java.util.List;

import gov.gtas.services.search.FlightPassengerVo;

public class AdhocQueryDto {
	/** a single page of search results */
	private List<FlightPassengerVo> passengers;
	
	/** total hits returned by search engine */
	private long totalHits;
	
	public AdhocQueryDto(List<FlightPassengerVo> pass, long totalHits){
		this.passengers = pass;
		this.totalHits = totalHits;
	}

	public List<FlightPassengerVo> getPassengers() {
		return passengers;
	}

	public long getTotalHits() {
		return totalHits;
	}
}
