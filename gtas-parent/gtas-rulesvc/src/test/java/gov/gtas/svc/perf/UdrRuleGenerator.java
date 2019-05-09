/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.svc.perf;

import gov.gtas.enumtype.CriteriaOperatorEnum;
import gov.gtas.enumtype.EntityEnum;
import gov.gtas.enumtype.TypeEnum;
import gov.gtas.model.udr.json.QueryConditionEnum;
import gov.gtas.model.udr.json.UdrSpecification;
import gov.gtas.model.udr.json.util.UdrSpecificationBuilder;
import gov.gtas.svc.UdrService;

import java.util.Date;

/**
 * Generates and persists test Watch list rules.
 */
public class UdrRuleGenerator {
    public static final String UDR_AUTHOR = "bstygar";
    public static final String[][][] UDR_DATA = {
        {
            {"Passenger", "firstName", "string", "BEGINS_WITH", "Jona"},
            {"Passenger", "lastName", "string", "ENDS_WITH", "Smith"},
            {"Passenger", "nationality", "string", "NOT_EQUAL", "USA"},
            {"Flight", "carrier", "string", "EQUAL", "DL"},
            {"Flight", "destination", "string", "EQUAL", "JFK"}
        },
        {
            {"Passenger", "lastName", "string", "EQUAL", "Cooper"},
            {"Document", "expirationDate", "date", "LESS", "2013-01-31"},
            {"Address", "country", "string", "NOT_EQUAL", "USA"},
            {"Pnr", "bagCount", "integer", "GREATER_OR_EQUAL", "2"},
            {"Flight", "carrier", "string", "EQUAL", "DL"},
            {"Flight", "origin", "string", "EQUAL", "LHR"},
            {"Flight", "destination", "string", "EQUAL", "JFK"}
        },
        {
            {"Passenger", "lastName", "string", "EQUAL", "Cooper"},
            {"Document", "expirationDate", "date", "LESS", "2013-01-31"},
            {"Address", "country", "string", "NOT_EQUAL", "USA"},
            {"Pnr", "bagCount", "integer", "IS_NULL", "2"},
            {"Flight", "carrier", "string", "EQUAL", "DL"},
            {"Flight", "origin", "string", "EQUAL", "LHR"},
            {"Flight", "destination", "string", "EQUAL", "JFK"}
        }
        
    };

    public static void generateUdr(UdrService udrService, String title, int count) {
        int titleNum = 1;
        for(int i = 0; i < count; ++i) {
            UdrSpecification spec = null;
            for (String[][] data : UDR_DATA) {
                boolean invalid = false;
                if(titleNum > 3){
                    invalid = Math.random() < 0.85;
                }
                spec = createUdr(title+(titleNum++), data, invalid);
                udrService.createUdr(UDR_AUTHOR, spec);
            }
        }
    }

    private static UdrSpecification createUdr(String title, String[][] data, boolean invalidate) {
        UdrSpecificationBuilder bldr = new UdrSpecificationBuilder(null, QueryConditionEnum.AND);
        bldr.addMeta(title, null, new Date(), null, true, UDR_AUTHOR);
        for(String[] term:data){
            String value = invalidate?term[4].substring(0, term[4].length()-1)+"0":term[4];
            bldr.addTerm(EntityEnum.getEnum(term[0]), term[1], TypeEnum.getEnum(term[2]), CriteriaOperatorEnum.getEnum(term[3]), new String[]{value});
        }
        return bldr.build();
    }
    
}
