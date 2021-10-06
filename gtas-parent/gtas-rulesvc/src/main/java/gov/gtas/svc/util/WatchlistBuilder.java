/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.util;

import gov.gtas.constant.CommonErrorConstants;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.WatchlistEditEnum;
import gov.gtas.error.ErrorHandlerFactory;
import gov.gtas.model.watchlist.Watchlist;
import gov.gtas.model.watchlist.WatchlistItem;
import gov.gtas.model.watchlist.json.WatchlistItemSpec;
import gov.gtas.model.watchlist.json.WatchlistSpec;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A builder pattern object for creating watch list objects programmatically.
 */
public class WatchlistBuilder {
	private static Logger logger = LoggerFactory.getLogger(WatchlistBuilder.class);

	private String name;
	private EntityEnum entity;
	private List<WatchlistItemSpec> items;
	private List<WatchlistItem> deleteList;
	private List<WatchlistItem> createUpdateList;
	private ObjectMapper mapper;

	public WatchlistBuilder(final WatchlistSpec spec) {
		this.mapper = new ObjectMapper();
		if (spec != null) {
			this.name = spec.getName();
			this.entity = EntityEnum.getEnum(spec.getEntity());
			this.items = spec.getWatchlistItems();
		}
	}

	public WatchlistBuilder(final Watchlist watchlist, List<WatchlistItem> wlitems) {
		this.mapper = new ObjectMapper();
		if (watchlist != null) {
			this.name = watchlist.getWatchlistName();
			this.entity = watchlist.getWatchlistEntity();
			this.createUpdateList = wlitems;
		}
	}

	public WatchlistSpec buildWatchlistSpec() {
		WatchlistSpec ret = new WatchlistSpec(this.name, this.entity.getEntityName());
		for (WatchlistItem item : createUpdateList) {
			try {
				WatchlistItemSpec itemSpec = mapper.readValue(item.getItemData(), WatchlistItemSpec.class);
				itemSpec.setId(item.getId());
				ret.addWatchlistItem(itemSpec);
			} catch (IOException ioe) {
				logger.error("WatchlistBuilder.buildWatchlistSpec() - " + ioe.getMessage());
				throw ErrorHandlerFactory.getErrorHandler().createException(
						CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE, item.getId(), "buildWatchlistSpec");

			}
		}
		return ret;
	}

	public void buildPersistenceLists() {
		if (items != null && items.size() > 0) {
			deleteList = new LinkedList<WatchlistItem>();
			createUpdateList = new LinkedList<WatchlistItem>();
			for (WatchlistItemSpec itemSpec : items) {
				// default action is C
				String action = itemSpec.getAction();
				itemSpec.setAction(null);
				WatchlistEditEnum op = WatchlistEditEnum.getEditEnumForOperationName(action);
				WatchlistItem item = new WatchlistItem();
				switch (op) {
				case U:
					item.setId(itemSpec.getId());
				case C:
					String json = null;
					try {
						json = mapper.writeValueAsString(itemSpec);
					} catch (JsonProcessingException jpe) {
						logger.error("WatchlistBuilder.buildPersistenceLists() - " + jpe.getMessage());
						throw ErrorHandlerFactory.getErrorHandler().createException(
								CommonErrorConstants.INVALID_ARGUMENT_ERROR_CODE, itemSpec.getId(),
								"buildPersistenceLists");
					}

					item.setItemData(json);
					item.setKeyString(itemSpec.getStringKey());
					StringBuilder ruleBldr = new StringBuilder();
					/* List<String> ruleCriteria = */ WatchlistRuleCreationUtil.createWatchlistRule(this.entity,
							itemSpec.getTerms(), this.getName(), ruleBldr);
					item.setItemRuleData(ruleBldr.toString());
					this.createUpdateList.add(item);
					break;
				case D:
					item.setId(itemSpec.getId());
					item.setKeyString(itemSpec.getStringKey());
					this.deleteList.add(item);
					break;
				}
			}
			if (deleteList.size() == 0) {
				this.deleteList = null;
			}
			if (createUpdateList.size() == 0) {
				this.createUpdateList = null;
			}
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the entity
	 */
	public EntityEnum getEntity() {
		return entity;
	}

	/**
	 * @return the deleteList
	 */
	public List<WatchlistItem> getDeleteList() {
		return deleteList;
	}

	/**
	 * @return the createUpdateList
	 */
	public List<WatchlistItem> getCreateUpdateList() {
		return createUpdateList;
	}

}
