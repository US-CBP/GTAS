/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *  
 */

package gov.gtas.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.gtas.enumtype.HitTypeEnum;

import java.util.HashSet;
import java.util.Set;

public class PriorityVettingListRuleTypes {
	@JsonProperty("WATCHLIST")
	private Boolean watchlist = false;
	@JsonProperty("USER_RULE")
	private Boolean userRule = false;
	@JsonProperty("EXTERNAL_RULE")
	private Boolean externalHit = false;
	@JsonProperty("GRAPH_RULE")
	private Boolean graphRule = false;
	@JsonProperty("MANUAL")
	private Boolean manual = false;
	@JsonProperty("PARTIAL_WATCHLIST")
	private Boolean partialWatchlist = false;

	public Boolean getManual() {
		return manual;
	}

	public void setManual(Boolean manual) {
		this.manual = manual;
	}

	public Boolean getWatchlist() {
		return watchlist;
	}

	public void setWatchlist(Boolean watchlist) {
		this.watchlist = watchlist;
	}

	public Boolean getUserRule() {
		return userRule;
	}

	public void setUserRule(Boolean userRule) {
		this.userRule = userRule;
	}

	public Boolean getGraphRule() {
		return graphRule;
	}

	public void setGraphRule(Boolean graphRule) {
		this.graphRule = graphRule;
	}

	public Boolean getPartialWatchlist() {
		return partialWatchlist;
	}

	public void setPartialWatchlist(Boolean partialWatchlist) {
		this.partialWatchlist = partialWatchlist;
	}

	public Set<HitTypeEnum> hitTypeEnums() {
		Set<HitTypeEnum> hitTypeEnums = new HashSet<>();
		if (watchlist) {
			hitTypeEnums.add(HitTypeEnum.WATCHLIST_PASSENGER);
			hitTypeEnums.add(HitTypeEnum.WATCHLIST_DOCUMENT);
			hitTypeEnums.add(HitTypeEnum.WATCHLIST);
		}
		if (userRule) {
			hitTypeEnums.add(HitTypeEnum.USER_DEFINED_RULE);
		}
		if (graphRule) {
			hitTypeEnums.add(HitTypeEnum.GRAPH_HIT);
		}
		if (partialWatchlist) {
			hitTypeEnums.add(HitTypeEnum.PARTIAL_WATCHLIST);
		}
		if (manual) {
			hitTypeEnums.add(HitTypeEnum.MANUAL_HIT);
		}
		if (externalHit) {
			hitTypeEnums.add(HitTypeEnum.EXTERNAL_HIT);
		}
		if (hitTypeEnums.isEmpty()) {
			hitTypeEnums.add(HitTypeEnum.NOT_USED);
		}
		return hitTypeEnums;
	}

	public Boolean getExternalHit() {
		return externalHit;
	}

	public void setExternalHit(Boolean externalHit) {
		this.externalHit = externalHit;
	}
}
