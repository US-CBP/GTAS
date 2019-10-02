/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.error;

import gov.gtas.constant.WatchlistConstants;

/**
 * Error Handler for the UDR Service.
 */
public class WatchlistServiceErrorHandler extends BasicErrorHandler {

	/**
	 * Instantiates a new watchlist service error handler.
	 */
	public WatchlistServiceErrorHandler() {
		super();
		super.addErrorCodeToHandlerMap(WatchlistConstants.CANNOT_DELETE_NONEMPTY_WATCHLIST_ERROR_CODE,
				WatchlistConstants.CANNOT_DELETE_NONEMPTY_WATCHLIST_ERROR_MESSAGE);

		super.addErrorCodeToHandlerMap(WatchlistConstants.MISSING_DELETE_OR_UPDATE_ITEM_ERROR_CODE,
				WatchlistConstants.MISSING_DELETE_OR_UPDATE_ITEM_ERROR_MESSAGE);

		super.addErrorCodeToHandlerMap(WatchlistConstants.CANNOT_SET_ID_FOR_CREATE_ITEM_ERROR_CODE,
				WatchlistConstants.CANNOT_SET_ID_FOR_CREATE_ITEM_ERROR_MESSAGE);
	}
}
