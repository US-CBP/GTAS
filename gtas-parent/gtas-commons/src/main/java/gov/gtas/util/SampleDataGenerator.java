/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.util;

import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.WatchlistEditEnum;
import gov.gtas.model.watchlist.json.WatchlistItemSpec;
import gov.gtas.model.watchlist.json.WatchlistSpec;
import gov.gtas.model.watchlist.json.WatchlistTerm;
import gov.gtas.querybuilder.mappings.PassengerMapping;

/**
 * A builder pattern object for creating watch list objects programmatically.
 */
public class SampleDataGenerator {
    /**
     * Creates a sample watch list JSON object. (This is used for testing.)
     * 
     * @return watch list JSON object.
     */
    public static WatchlistSpec createSampleWatchlist(String wlName) {
        WatchlistSpec ret = new WatchlistSpec(wlName,
                EntityEnum.PASSENGER.getEntityName().toUpperCase());
        ret.addWatchlistItem(new WatchlistItemSpec(null, WatchlistEditEnum.C
                .getOperationName(), new WatchlistTerm[] {
                new WatchlistTerm(PassengerMapping.FIRST_NAME
                        .getFieldName(), PassengerMapping.FIRST_NAME
                        .getFieldType(), "John"),
                new WatchlistTerm(PassengerMapping.LAST_NAME
                        .getFieldName(), PassengerMapping.LAST_NAME
                        .getFieldType(), "Jones"),
                new WatchlistTerm(PassengerMapping.DOB.getFieldName(),
                        PassengerMapping.DOB.getFieldType(), "1747-07-06") }));
        ret.addWatchlistItem(new WatchlistItemSpec(32L, WatchlistEditEnum.U
                .getOperationName(), new WatchlistTerm[] {
                new WatchlistTerm(PassengerMapping.FIRST_NAME
                        .getFieldName(), PassengerMapping.FIRST_NAME
                        .getFieldType(), "Julius"),
                new WatchlistTerm(PassengerMapping.LAST_NAME
                        .getFieldName(), PassengerMapping.LAST_NAME
                        .getFieldType(), "Seizure"),
                new WatchlistTerm(PassengerMapping.DOB.getFieldName(),
                        PassengerMapping.DOB.getFieldType(), "1966-09-13") }));
        ret.addWatchlistItem(new WatchlistItemSpec(25L, WatchlistEditEnum.D
                .getOperationName(), null));
        return ret;
    }
    public static WatchlistSpec newWlWith2Items(String wlName) {
        WatchlistSpec ret = new WatchlistSpec(wlName,
                EntityEnum.PASSENGER.getEntityName().toUpperCase());
        ret.addWatchlistItem(new WatchlistItemSpec(null, WatchlistEditEnum.C
                .getOperationName(), new WatchlistTerm[] {
                new WatchlistTerm(PassengerMapping.FIRST_NAME
                        .getFieldName(), PassengerMapping.FIRST_NAME
                        .getFieldType(), "John"),
                new WatchlistTerm(PassengerMapping.LAST_NAME
                        .getFieldName(), PassengerMapping.LAST_NAME
                        .getFieldType(), "Jones"),
                new WatchlistTerm(PassengerMapping.DOB.getFieldName(),
                        PassengerMapping.DOB.getFieldType(), "1747-07-06") }));
        ret.addWatchlistItem(new WatchlistItemSpec(null, WatchlistEditEnum.C
                .getOperationName(), new WatchlistTerm[] {
                new WatchlistTerm(PassengerMapping.FIRST_NAME
                        .getFieldName(), PassengerMapping.FIRST_NAME
                        .getFieldType(), "Julius"),
                new WatchlistTerm(PassengerMapping.LAST_NAME
                        .getFieldName(), PassengerMapping.LAST_NAME
                        .getFieldType(), "Seizure"),
                new WatchlistTerm(PassengerMapping.DOB.getFieldName(),
                        PassengerMapping.DOB.getFieldType(), "1966-09-13") }));
        return ret;
    }

}
