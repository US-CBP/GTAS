/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.soap.Text;

//import org.elasticsearch.search.highlight.HighlightField;

import gov.gtas.services.search.FlightPassengerVo;

public class AdhocQueryDto {
	/** a single page of search results */
	private final List<FlightPassengerVo> passengers;

	/** total hits returned by search engine */
	private final long totalHits;

	private final String error;

	public AdhocQueryDto(String error) {
		this.error = error;
		this.totalHits = 0;
		this.passengers = new ArrayList<>();
	}

	public AdhocQueryDto(List<FlightPassengerVo> pass, long totalHits) {
		this.error = null;
		this.totalHits = totalHits;
		this.passengers = pass;
	}

	public List<FlightPassengerVo> getPassengers() {
		return passengers;
	}

	public long getTotalHits() {
		return totalHits;
	}

	public String getError() {
		return error;
	}
}
