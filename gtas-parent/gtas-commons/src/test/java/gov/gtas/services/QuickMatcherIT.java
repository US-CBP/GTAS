/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import gov.gtas.config.CachingConfig;
import gov.gtas.config.CommonServicesConfig;
import gov.gtas.model.Passenger;
import gov.gtas.model.PassengerDetails;
import gov.gtas.model.watchlist.json.WatchlistItemSpec;
import gov.gtas.services.matcher.quickmatch.DerogHit;
import gov.gtas.services.matcher.quickmatch.MatchingResult;
import gov.gtas.services.matcher.quickmatch.QuickMatcher;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CommonServicesConfig.class,
        CachingConfig.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Rollback(true)
public class QuickMatcherIT {

    @Autowired
    private QuickMatcher qm;
    private ObjectMapper mapper = new ObjectMapper();


    @SuppressWarnings("Duplicates")
    @Test
    public void testingStart() throws IOException {
        String data = "{\"id\":1,\"action\":null,\"terms\":[{\"field\":\"firstName\",\"type\":\"string\",\"value\":\"FOO\"},{\"field\":\"lastName\",\"type\":\"string\",\"value\":\"BAR\"},{\"field\":\"dob\",\"type\":\"date\",\"value\":\"1988-09-14\"}]}";
        Passenger p = new Passenger();
        PassengerDetails pd = new PassengerDetails();
        List<HashMap<String, String>> derogList = new ArrayList<>();

        pd.setFirstName("FOO");
        pd.setLastName("BAR");
        pd.setMiddleName(null);
        p.setId(90909L);
        p.setPassengerDetails(pd);
        WatchlistItemSpec itemSpec = mapper.readValue(data, WatchlistItemSpec.class);
        HashMap<String, String> derogItem = new HashMap<>();
        derogItem.put(DerogHit.WATCH_LIST_NAME, "TEST");
        if (itemSpec != null && itemSpec.getTerms() != null) {
            for (int i = 0; i < itemSpec.getTerms().length; i++) {
                derogItem.put(itemSpec.getTerms()[i].getField(), itemSpec.getTerms()[i].getValue());
            }
        }
        derogList.add(derogItem);
        MatchingResult result = qm.match(p, derogList, .85F);
            result.getResponses();
    }


}
