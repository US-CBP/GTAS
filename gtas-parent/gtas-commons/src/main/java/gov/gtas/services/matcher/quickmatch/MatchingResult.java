/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.matcher.quickmatch;

import java.util.Map;

public class MatchingResult {

	private int totalHits;
	private Map<String, DerogResponse> responses;

	public int getTotalHits() {
		return totalHits;
	}

	public Map<String, DerogResponse> getResponses() {
		return responses;
	}

	public MatchingResult(int totalHits, Map<String, DerogResponse> responses) {
		this.totalHits = totalHits;
		this.responses = responses;

	}

	@Override
	public String toString() {
		return "MatchingResult [totalHits=" + totalHits + ", responses=" + responses + "]";
	}

}
