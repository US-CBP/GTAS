/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.watchlist.json;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Recursive query condition object.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public class WatchlistSpec implements Serializable {

	/**
	 * default constructor
	 */
	public WatchlistSpec() {
	}

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -182544361080662L;

	/** The name. */
	private String name;

	/** The entity. */
	private String entity;

	/** The watchlist items. */
	private List<WatchlistItemSpec> watchlistItems;

	/**
	 * Instantiates a new watchlist spec.
	 *
	 * @param name
	 *            the name
	 * @param entity
	 *            the entity
	 */
	public WatchlistSpec(String name, String entity) {
		this.name = name;
		this.entity = entity;
		watchlistItems = new LinkedList<>();
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the entity.
	 *
	 * @return the entity
	 */
	public String getEntity() {
		return entity;
	}

	/**
	 * Sets the entity.
	 *
	 * @param entity
	 *            the entity to set
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}

	/**
	 * Gets the watchlist items.
	 *
	 * @return the watchlistItems
	 */
	public List<WatchlistItemSpec> getWatchlistItems() {
		return watchlistItems;
	}

	/**
	 * Adds the watchlist item.
	 *
	 * @param watchlistItem
	 *            the watchlist item
	 */
	public void addWatchlistItem(WatchlistItemSpec watchlistItem) {
		this.watchlistItems.add(watchlistItem);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
