/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.perf;

import gov.gtas.enumtype.EntityEnum;
import gov.gtas.model.watchlist.json.WatchlistItemSpec;
import gov.gtas.model.watchlist.json.WatchlistSpec;
import gov.gtas.model.watchlist.json.WatchlistTerm;
import gov.gtas.querybuilder.mappings.DocumentMapping;
import gov.gtas.querybuilder.mappings.PassengerMapping;
import gov.gtas.svc.WatchlistService;

/**
 * Generates and persists test Watch list rules.
 */
public class WatchlistRuleGenerator {
    public static final String WL_AUTHOR = "bstygar";
    public static final String[][] WL_DATA = {
        {"Jonathon", "Smith", "1964-07-12", "123456789"},
        {"Williamnevell", "Jones", "1964-07-12", "123456789"},
        {"Johnalva", "Wayne", "2012-07-12", "123456789"},
        {"Garrywilliam", "Cooper", "2015-07-12", "987654321"},
        {"Jonthon", "Smith", "1964-07-12", "548721687"},
        {"Jothon", "Smith", "1964-07-12", "159264375"},
        {"Jonaon", "Smith", "1964-07-12", "12356789"},
        {"Jothon", "Smith", "1964-07-12", "12346789"},
        {"Jonahon", "Smith", "1964-07-12", "12356789"},
        {"Jonathon", "Smith", "1964-07-12", "12346789"},
        {"Jonatn", "Smith", "1964-07-12", "12345689"},
        {"Jonaton", "Smith", "1964-07-12", "12345689"},
        {"Jonaton", "Smith", "1964-07-12", "12345789"},
        {"Jonaton", "Smith", "1964-07-12", "12345789"},
        {"Jonaon", "Smith", "1964-07-12", "12345789"}
        
    };

    public static void generateWlRules(WatchlistService watchlistService, String wlName, EntityEnum entity,
            int count) {
        for(int i = 0; i < count; ++i) {
            WatchlistSpec spec = new WatchlistSpec(wlName, entity.getEntityName());
            for (String[] data : WL_DATA) {
                spec.addWatchlistItem(createItem(entity, null, "create", data));
            }
            watchlistService.createUpdateDeleteWatchlistItems(WL_AUTHOR, spec);
        }
    }

    private static WatchlistItemSpec createItem(EntityEnum entity, Long id,
            String action, String[] data) {
        WatchlistTerm[] terms = null;
        switch (entity) {
        case PASSENGER:
            terms = new WatchlistTerm[3];
            terms[0] = new WatchlistTerm(PassengerMapping.FIRST_NAME.getFieldName(),
                    PassengerMapping.FIRST_NAME.getFieldType(), data[0]);
            terms[1] = new WatchlistTerm(PassengerMapping.LAST_NAME.getFieldName(),
                    PassengerMapping.LAST_NAME.getFieldType(), data[1]);
            terms[2] = new WatchlistTerm(PassengerMapping.DOB.getFieldName(),
                    PassengerMapping.DOB.getFieldType(), data[2]);
            break;
        case DOCUMENT:
            terms = new WatchlistTerm[1];
            terms[0] = new WatchlistTerm(DocumentMapping.DOCUMENT_NUMBER.getFieldName(),
                    DocumentMapping.DOCUMENT_NUMBER.getFieldType(), data[3]);
        default:
            break;
        }
        return new WatchlistItemSpec(id, action, terms);
    }
    
}
