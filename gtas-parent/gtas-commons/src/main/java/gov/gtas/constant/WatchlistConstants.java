/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.constant;

public class WatchlistConstants {
	public static final String WL_KNOWLEDGE_BASE_NAME = "WL Knowledge Base";
	public static final String DELETE_OP_NAME = "delete";
	//////////////////////////////////////////////////////////////////////////////////
	// VALIDATION FIELDS and ERROR codes
	/////////////////////////////////////////////////////////////////////////////////
	public static final String WL_NAME_FIELD = "name";
	public static final String WL_ENTITY_FIELD = "entity";
	public static final String WL_ITEMS_FIELD = "watchlistItems";

	public static final String WL_ITEM_ACTION_FIELD = "action";
	public static final String WL_ITEM_FIELD_FIELD = "field";
	public static final String WL_ITEM_TYPE_FIELD = "type";
	///////////////////////////////////////////////////////////////////////////////////
	// ERRORS
	///////////////////////////////////////////////////////////////////////////////////
	/*
	 * This is the error code indicating that the watch list to be deleted is not
	 * empty.
	 */
	public static final String CANNOT_DELETE_NONEMPTY_WATCHLIST_ERROR_CODE = "CANNOT_DELETE_NONEMPTY_WATCHLIST";
	/*
	 * This is the error code indicating that one or more watch list items to be
	 * deleted or updated cannot be found in the data base.
	 */
	public static final String MISSING_DELETE_OR_UPDATE_ITEM_ERROR_CODE = "MISSING_DELETE_OR_UPDATE_ITEM";
	/*
	 * This is the error code indicating that an id was specified for a create item
	 * operation.
	 */
	public static final String CANNOT_SET_ID_FOR_CREATE_ITEM_ERROR_CODE = "CANNOT_SET_ID_FOR_CREATE_ITEM";

	/*
	 * This is the error message indicating that the watch list to be deleted is not
	 * empty.
	 */
	public static final String CANNOT_DELETE_NONEMPTY_WATCHLIST_ERROR_MESSAGE = "Watch list '%s' cannot be deleted since it is not empty.";
	/*
	 * This is the error message indicating that one or more watch list items to be
	 * deleted or updated cannot be found in the data base.
	 */
	public static final String MISSING_DELETE_OR_UPDATE_ITEM_ERROR_MESSAGE = "Missing watch list items for delete/update, expected id's [%s] but found [%s]";
	/*
	 * This is the error message indicating that an id was specified for a create
	 * item operation.
	 */
	public static final String CANNOT_SET_ID_FOR_CREATE_ITEM_ERROR_MESSAGE = "An id was specified for a create item operation (%d)";
}
