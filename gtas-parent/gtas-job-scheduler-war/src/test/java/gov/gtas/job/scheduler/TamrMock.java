/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.job.scheduler;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * A mock of the functionality Tamr provides, for performing integration
 * testing against.
 */
@Component
public class TamrMock {

    private final Logger logger = LoggerFactory.getLogger(TamrMock.class);
    
    private final static String queryPassengerRegex =
            "\\{\"gtasId\":\"(?<gtasId>\\d+)\",\"firstName\":\"(?<firstName>[^\"]+)\",\"middleName\":[\"\\w ]+,\"lastName\":\"(?<lastName>[^\"]+)\",\"gender\":\"[MF]\",\"dob\":\"(?<dob>[^\"]+)\",\"documents\":\\[[^\\]]+\\],\"citizenshipCountry\":\\[[^\\]]+\\]\\}";
    private final static String queryRegex =
            "\\{\"passengers\":\\[(?:" + queryPassengerRegex + ",?)+\\]\\}";
    
    private final static String derogEntryRegex =
            "\\{\"gtasId\":\"(?<gtasId>\\d+)\",\"firstName\":\"(?<firstName>[^\"]+)\",\"middleName\":[\"\\w ]+,\"lastName\":\"(?<lastName>[^\"]+)\",\"gender\":null,\"dob\":\"(?<dob>[^\"]+)\",\"documents\":null,\"citizenshipCountry\":null,\"derogId\":\"(?<derogId>\\d+)\"\\}";
    private final static String derogRegex =
            "\\{\"passengers\":\\[(?:" + derogEntryRegex + ",?)+\\]\\}";
    
    private final static Map<String, String> nameToTamrId = new HashMap<String, String>() {{
        put("HSIUYUAN JIANG", "00738a54-e024-3dcf-9df0-6a982022c8ea");
        put("XIUYUAN JIANG", "00738a54-e024-3dcf-9df0-6a982022c8ea");
        put("XIUYUAN CHIANG", "00738a54-e024-3dcf-9df0-6a982022c8ea");
        
        put("GAYLA JOSEPH", "00647e30-95ba-31a9-8660-2143e7dae0d3");
        put("GAELLA JOSEPH", "00647e30-95ba-31a9-8660-2143e7dae0d3");
        
        put("RUBEN THEBAULT", "75c317a7-e8f1-39e5-8faa-f18b757dba79");
        put("REUBEN THEBAULT", "75c317a7-e8f1-39e5-8faa-f18b757dba79");
    }};
    
    private Map<String, String> tamrIdToDerogId = new HashMap<>();
    
    private TamrIntegrationTestUtils tamrUtils;
    
    public TamrMock(TamrIntegrationTestUtils tamrUtils) {
        this.tamrUtils = tamrUtils;
    }
    
    private String getTamrIdFromName(String name) {
        String tamrId = nameToTamrId.get(name);
        if (tamrId == null) {
            tamrId = UUID.randomUUID().toString();
        }
        return tamrId;
    }
    
    /**
     * Get the JSON clustering information Tamr should return for a single
     * passenger with the given gtasId and name.
     */
    private String getPassengerHistoryResponse(String gtasId, String name) {
        return String.format("{\"gtasId\":\"%s\",\"derogIds\":[],\"tamrId\":\"%s\",\"version\":1,\"score\":1.0}",
                gtasId, getTamrIdFromName(name));
    }
    
    /**
     * Get the JSON derog hit information Tamr should return for a single
     * passenger with the given gtasId and name.
     */
    private String getPassengerDerogResponse(String gtasId, String name) {
        String derogHit = "";
        String derogId = tamrIdToDerogId.get(getTamrIdFromName(name));
        if (derogId != null) {
            derogHit = String.format("{\"derogId\":\"%s\",\"score\":0.6}",
                    derogId);
        }
        return String.format("{\"gtasId\":\"%s\",\"derogIds\":[%s],\"tamrId\":null,\"version\":-1,\"score\":0.0}",
                gtasId, derogHit);
    }
    
    /**
     * Responds to a QUERY message that has been sent to the ActiveMQ broker.
     * Returns the number of passengers processed.
     */
    public int respondToQuery() throws JMSException {
        TextMessage queryMessage = tamrUtils.getMessageSentToTamr();
        assertNotNull(queryMessage);
        assertEquals("QUERY", queryMessage.getJMSType());

        String queryJson = queryMessage.getText();
        logger.info("Processing QUERY message.");
        
        // First, make sure the entire message is well-formed.
        Matcher queryMatcher = Pattern.compile(queryRegex).matcher(queryJson);
        assertTrue(queryMatcher.matches());
        
        StringJoiner historiesResponseJoiner = new StringJoiner(
                ",", "{\"travelerQuery\":[", "]}");
        StringJoiner derogsResponseJoiner = new StringJoiner(
                ",", "{\"travelerQuery\":[", "]}");
        
        // Then, extract the individual passengers and build responses.
        int passengersProcessed = 0;
        Matcher passengerMatcher = Pattern.compile(queryPassengerRegex)
                .matcher(queryJson);
        while (passengerMatcher.find()) {
            String gtasId = passengerMatcher.group("gtasId");
            String name = passengerMatcher.group("firstName") + " " +
                    passengerMatcher.group("lastName");
            
            historiesResponseJoiner.add(
                    getPassengerHistoryResponse(gtasId, name));
            derogsResponseJoiner.add(
                    getPassengerDerogResponse(gtasId, name));
            
            passengersProcessed += 1;
        }
        
        // Finally, send messages back to GTAS from Tamr mock.
        logger.info("Processed {} passengers. Responding with histories and derog hits.",
                passengersProcessed);
        tamrUtils.sendMessageToGtasFromTamr("QUERY",
                historiesResponseJoiner.toString());
        tamrUtils.sendMessageToGtasFromTamr("QUERY",
                derogsResponseJoiner.toString());
        
        return passengersProcessed;
    }
    
    /**
     * Respond to a DC.REPLACE message sent to Tamr. Returns the number of
     * derog list entries received.
     */
    public int respondToDerogReplace() throws JMSException {
        TextMessage derogMessage = tamrUtils.getMessageSentToTamr();
        assertNotNull(derogMessage);
        assertEquals("DC.REPLACE", derogMessage.getJMSType());

        String derogJson = derogMessage.getText();
        logger.info("Processing DC.REPLACE message.");
        
        // First, make sure the entire message is well-formed.
        Matcher derogMatcher = Pattern.compile(derogRegex).matcher(derogJson);
        assertTrue(derogMatcher.matches());
        
        // Then, extract the entries and make a new derog list from them.
        int derogCount = 0;
        tamrIdToDerogId.clear();
        Matcher entryMatcher = Pattern.compile(derogEntryRegex)
                .matcher(derogJson);
        while (entryMatcher.find()) {
            String derogId = entryMatcher.group("derogId");
            String name = entryMatcher.group("firstName") + " " +
                    entryMatcher.group("lastName");
            tamrIdToDerogId.put(getTamrIdFromName(name), derogId);
            derogCount += 1;
        }
        
        // Finally, send an acknowledgement back to GTAS.
        logger.info("Processed {} derog entries. Responding with acknowledgement.",
                derogCount);
        tamrUtils.sendMessageToGtasFromTamr("DC.REPLACE",
                "{\"acknowledgment\":true,\"error\":null}");
        return derogCount;
    }
}
