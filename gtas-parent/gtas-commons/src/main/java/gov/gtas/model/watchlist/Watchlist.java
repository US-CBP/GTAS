/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model.watchlist;

import static gov.gtas.constant.DomainModelConstants.WL_UNIQUE_CONSTRAINT_NAME;
import gov.gtas.constant.DomainModelConstants;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.model.BaseEntity;
import gov.gtas.model.User;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "watch_list", uniqueConstraints = {
		@UniqueConstraint(name = WL_UNIQUE_CONSTRAINT_NAME, columnNames = { "WL_NAME" }) })
public class Watchlist extends BaseEntity {
	private static final long serialVersionUID = 345L;

	public Watchlist() {
	}

	public Watchlist(String name, EntityEnum entity) {
		this.watchlistName = name;
		this.watchlistEntity = entity;
	}

	@ManyToOne
	@JoinColumn(name = "WL_EDITOR", referencedColumnName = "user_id", nullable = false)
	private User watchListEditor;

	@Column(name = "WL_NAME", nullable = false, length = DomainModelConstants.WL_NAME_COLUMN_SIZE)
	private String watchlistName;

	@Enumerated(EnumType.STRING)
	@Column(name = "WL_ENTITY", nullable = false, length = DomainModelConstants.ENTITY_NAME_SIZE)
	private EntityEnum watchlistEntity;

	@Column(name = "WL_EDIT_DTTM", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date editTimestamp;

	/**
	 * @return the watchListEditor
	 */
	public User getWatchListEditor() {
		return watchListEditor;
	}

	/**
	 * @param watchListEditor
	 *            the watchListEditor to set
	 */
	public void setWatchListEditor(User watchListEditor) {
		this.watchListEditor = watchListEditor;
	}

	/**
	 * @return the editTimestamp
	 */
	public Date getEditTimestamp() {
		return editTimestamp;
	}

	/**
	 * @param editTimestamp
	 *            the editTimestamp to set
	 */
	public void setEditTimestamp(Date editTimestamp) {
		this.editTimestamp = editTimestamp;
	}

	/**
	 * @return the watchlistName
	 */
	public String getWatchlistName() {
		return watchlistName;
	}

	/**
	 * @return the watchlistEntity
	 */
	public EntityEnum getWatchlistEntity() {
		return watchlistEntity;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Watchlist other = (Watchlist) obj;
		return Objects.equals(this.watchListEditor, other.watchListEditor)
				&& Objects.equals(this.watchlistName, other.watchlistName);
	}
}
