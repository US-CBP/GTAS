/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.matcher.quickmatch;

import java.util.ArrayList;
import java.util.List;

public class DerogResponse {
	private String gtasId;
	private ArrayList<DerogHit> derogIds = new ArrayList<>();

	public DerogResponse(String gtasId, List<DerogHit> hits) {
		this.gtasId = gtasId;
		this.derogIds.addAll(hits);
	}

	public String getGtasId() {
		return gtasId;
	}

	public void addDerogIds(ArrayList<DerogHit> derogIds) {
		this.derogIds.addAll(derogIds);
	}

	public ArrayList<DerogHit> getDerogIds() {
		return derogIds;
	}

	@Override
	public String toString() {
		return "DerogResponse [gtasId=" + gtasId + ", derogIds=" + derogIds + "]";
	}

}
