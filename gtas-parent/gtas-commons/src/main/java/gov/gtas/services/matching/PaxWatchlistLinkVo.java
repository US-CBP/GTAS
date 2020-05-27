/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.services.matching;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.gtas.model.HitDetail;
import gov.gtas.model.PIIObject;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.model.watchlist.json.WatchlistItemSpec;
import gov.gtas.model.watchlist.json.WatchlistTerm;

public class PaxWatchlistLinkVo implements PIIObject {

	private float percentMatch;
	private Date lastRunTimestamp;
	private int verifiedStatus;

	private Long watchlistItemId;
	private Long passengerId;
	private String watchListFirstName;
	private String watchListLastName;
	private String watchListDOB;
	private String watchlistCategory;
	private static final ObjectMapper mapper = new ObjectMapper();

	public PaxWatchlistLinkVo() {
	}

	public PaxWatchlistLinkVo(float percentMatch, Date lastRunTimestamp, int verifiedStatus, Long watchlistItemId,
			Long passengerId) {
		super();
		this.percentMatch = percentMatch;
		this.lastRunTimestamp = lastRunTimestamp;
		this.verifiedStatus = verifiedStatus;
		this.watchlistItemId = watchlistItemId;
	}

	public PaxWatchlistLinkVo(float percentMatch, Date lastRunTimestamp, int verifiedStatus, Long watchlistItemId,
			String watchListFirstName, String watchListLastName, String watchListDOB, String watchlistCategory) {
		super();
		this.percentMatch = percentMatch;
		this.lastRunTimestamp = lastRunTimestamp;
		this.verifiedStatus = verifiedStatus;
		this.watchlistItemId = watchlistItemId;
		this.passengerId = passengerId;
		this.watchListFirstName = watchListFirstName;
		this.watchListLastName = watchListLastName;
		this.watchListDOB = watchListDOB;
		this.watchlistCategory = watchlistCategory;
	}

	public static PaxWatchlistLinkVo fromHitDetail(HitDetail hitDetail) throws IOException {
		PaxWatchlistLinkVo paxWatchlistLinkVo = new PaxWatchlistLinkVo();
		WatchlistItem watchlistItem = (WatchlistItem) hitDetail.getHitMaker();
		WatchlistItemSpec itemSpec = new ObjectMapper().readValue(watchlistItem.getItemData(), WatchlistItemSpec.class);
		WatchlistTerm[] items = itemSpec.getTerms();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getField().equals("firstName")) {
				paxWatchlistLinkVo.setWatchListFirstName(items[i].getValue());
			}
			if (items[i].getField().equals("lastName")) {
				paxWatchlistLinkVo.setWatchListLastName(items[i].getValue());
			}
			if (items[i].getField().equals("dob")) {
				paxWatchlistLinkVo.setWatchListDOB(items[i].getValue());
			}
		}
		paxWatchlistLinkVo.setWatchlistItemId(hitDetail.getHitMakerId());
		paxWatchlistLinkVo.setPassengerId(hitDetail.getPassengerId());
		paxWatchlistLinkVo.setPercentMatch(hitDetail.getPercentage());
		String category = hitDetail.getHitMaker().getHitCategory().getName();
		paxWatchlistLinkVo.setWatchlistCategory(category);
		return paxWatchlistLinkVo;
	}

	public String getWatchListFirstName() {
		return watchListFirstName;
	}

	public void setWatchListFirstName(String watchListFirstName) {
		this.watchListFirstName = watchListFirstName;
	}

	public String getWatchListLastName() {
		return watchListLastName;
	}

	public void setWatchListLastName(String watchListLastName) {
		this.watchListLastName = watchListLastName;
	}

	public String getWatchListDOB() {
		return watchListDOB;
	}

	public void setWatchListDOB(String watchListDOB) {
		this.watchListDOB = watchListDOB;
	}

	public float getPercentMatch() {
		return percentMatch;
	}

	public void setPercentMatch(float percentMatch) {
		this.percentMatch = percentMatch;
	}

	public Date getLastRunTimestamp() {
		return lastRunTimestamp;
	}

	public void setLastRunTimestamp(Date lastRunTimestamp) {
		this.lastRunTimestamp = lastRunTimestamp;
	}

	public Long getWatchlistItemId() {
		return watchlistItemId;
	}

	public void setWatchlistItemId(Long watchlistItemId) {
		this.watchlistItemId = watchlistItemId;
	}

	public Long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(Long passengerId) {
		this.passengerId = passengerId;
	}

	public int getVerifiedStatus() {
		return verifiedStatus;
	}

	public void setVerifiedStatus(int verifiedStatus) {
		this.verifiedStatus = verifiedStatus;
	}

	public String getWatchlistCategory() {
		return watchlistCategory;
	}

	public void setWatchlistCategory(String watchlistCategory) {
		this.watchlistCategory = watchlistCategory;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		PaxWatchlistLinkVo that = (PaxWatchlistLinkVo) o;
		return getWatchlistItemId().equals(that.getWatchlistItemId()) && getPassengerId().equals(that.getPassengerId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getWatchlistItemId(), getPassengerId());
	}

	@Override
	public PIIObject deletePII() {
		this.watchlistCategory = "DELETED";
		this.watchListDOB = null;
		this.watchListFirstName = "DELETED";
		this.watchListLastName = "DELETED";
		return this;
	}

	@Override
	public PIIObject maskPII() {
		this.watchlistCategory = "MASKED";
		this.watchListDOB = null;
		this.watchListFirstName = "MASKED";
		this.watchListLastName = "MASKED";
		return this;	}
}
