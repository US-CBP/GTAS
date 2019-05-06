/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.redisson;

import gov.gtas.parsers.edifact.EdifactLexer;
import gov.gtas.parsers.edifact.Segment;
import gov.gtas.parsers.redisson.jms.InboundQMessageSender;
import gov.gtas.parsers.redisson.model.LedgerLiveObject;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class RedissonFilter {

    private static final Logger logger = LoggerFactory.getLogger(RedissonFilter.class);
    private RedissonClient client;
    private static String[] randomStrings;

    private static final String MESSAGE_SEGMENT_BEGIN="UNH";
    private static final String MESSAGE_SEGMENT_END="UNT";
    private static final String EMPTY_STRING="";
    private static final String TVL_HEADER_LABEL="TVL";
    private static final String DAT_HEADER_LABEL="DAT+700";
    private Long REDIS_KEY_TTL_MINUTES=7200L; // 5 Days - default


    public RedissonFilter() {
    }

    public RedissonFilter(RedissonClient client) {
        this.client = client;
    }


    // @todo legacy method call, purge in future iterations
    public void redisObjectLookUpPersist(String messagePayload, Date messageTimestamp){

        try {
        RLiveObjectService service = client.getLiveObjectService();

            LedgerLiveObject ledger = new LedgerLiveObject();

            //ledger.setName("ledger1");
            String messageHashKey = getMessageHash(messagePayload);

            // query Redis with the Key
            LedgerLiveObject returnLedger
                    = service.get(LedgerLiveObject.class, messageHashKey);

            if( (returnLedger == null) || (!returnLedger.getName().equals(messageHashKey))) {
                //persist into Redis
                ledger.setMessageTimeStamp(messageTimestamp);
                ledger.setProcessedTimeStamp(new Date());
                ledger.setName(getMessageHash(messagePayload));
                RExpirable rExpirable = service.asRExpirable(ledger);
                rExpirable.expireAt(Date.from(LocalDateTime.now().plusDays(7).toInstant(ZoneOffset.of("UTC"))));
                ledger = service.persist(ledger);

                //if(!publishToDownstreamQueues(messagePayload)){throw new Exception("Error publishing to parsing queue");};
            }else{
                //key exists, derivative logic goes here (time processed and placement on Queues)
            }


        }catch(Exception ex){
            logger.error("Redis look up presist errored", ex);
        }

    }

    /**
     * Method to handle check, skip/or insert logic into REDIS
     * @param messagePayload
     * @param messageTimestamp
     * @param service
     * @param sender
     * @param outboundLoaderQueue
     * @param filename
     * @param client
     * @param REDIS_KEYS_TTL
     */
    public void redisObjectLookUpPersist(String messagePayload, Date messageTimestamp,
                                         RLiveObjectService service,
                                         InboundQMessageSender sender,
                                         String outboundLoaderQueue,
                                         String filename, RedissonClient client,
                                         Long REDIS_KEYS_TTL,
                                         String REDIS_KEYS_TTL_TIME_UNIT){
        List<Segment> segments = new ArrayList<>();
        String tvlLineText = EMPTY_STRING;
        if(REDIS_KEYS_TTL_TIME_UNIT.equalsIgnoreCase("DAYS")) {
            REDIS_KEY_TTL_MINUTES = (REDIS_KEYS_TTL >= 1) ? REDIS_KEYS_TTL * 24 * 60 : REDIS_KEY_TTL_MINUTES;
        }
        else{
            REDIS_KEY_TTL_MINUTES = (REDIS_KEYS_TTL >= 1) ? REDIS_KEYS_TTL : REDIS_KEY_TTL_MINUTES;
        }
        try {

            LedgerLiveObject ledger = new LedgerLiveObject();
            RMapCache<String, String> map = client.getMapCache("ledger");
            String messageHashKey = EMPTY_STRING;
            EdifactLexer lexer = new EdifactLexer((String)messagePayload);
            segments = lexer.tokenize();

            for(Segment seg : segments){
                if(seg.getName().equalsIgnoreCase(TVL_HEADER_LABEL)){
                    tvlLineText = seg.getText();
                    break;
                    // not entertaining the concept of multiple PNRs(multiple TVL0 lines) in one file for now
                    // will revisit if need be
                }
            }
            String payload = lexer.getMessagePayload(MESSAGE_SEGMENT_BEGIN, MESSAGE_SEGMENT_END);
            if(payload == null){
                publishToDownstreamQueues(messagePayload, sender, outboundLoaderQueue, filename, tvlLineText);
            }else {
                // Eject DAT line, if it exists, and then hash the payload
                payload = removeDATSegment(payload);
                messageHashKey = getMessageHash(payload);
            }
            // query Redis with the Key
            LedgerLiveObject returnLedger
                    = service.get(LedgerLiveObject.class, messageHashKey);


            if(payload!=null && !map.containsKey(messageHashKey)){
                //persist into Redis
                ledger.setMessageTimeStamp(messageTimestamp);
                ledger.setProcessedTimeStamp(new Date());
                ledger.setName(getMessageHash(payload));
                map.put(messageHashKey, messageHashKey, REDIS_KEY_TTL_MINUTES, TimeUnit.MINUTES);
                logger.debug("++++++++++++++++++ REDIS Key Indexed +++++++++++++++++++++++++++++++++++");
				if (!publishToDownstreamQueues(messagePayload, sender, outboundLoaderQueue, filename, tvlLineText)) {
					throw new Exception("Error publishing to parsing queue");
				}
				
            }else{
                //key exists, derivative logic goes here (time processed and placement on Queues)
                if(payload == null) {
                    logger.info("++++++++++++++++++ Message Payload Is Empty - Publish to Downstream Q +++++++++++++++++++++++++++++++++++");
                }else {
                    logger.info("++++++++++++++++++ REDIS Key Exists +++++++++++++++++++++++++++++++++++");
                }
            }

        }catch(Exception ex){
            logger.error("error in redis update and persist", ex);
            publishToDownstreamQueues(messagePayload, sender, outboundLoaderQueue, filename, tvlLineText);
        }

    }

    private boolean publishToDownstreamQueues(String messagePayload, InboundQMessageSender sender,
                                              String outboundLoaderQueue, String filename, String tvlLineText){
        try {
            sender.sendFileToDownstreamQs(outboundLoaderQueue, messagePayload, filename, tvlLineText);
        }catch (Exception ex){
            logger.error("error publishing to downstream queues", ex);
        }
        return true;
    }


    private static String getMessageHash(String inputMessage) throws Exception, NoSuchAlgorithmException{

        if(inputMessage == null) throw new Exception("Input String is Null");
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] dataBytes = inputMessage.getBytes();
        md.update(dataBytes, 0, dataBytes.length);
        byte[] mdbytes = md.digest();
        StringBuffer hexString = new StringBuffer();
        for (int i=0;i<mdbytes.length;i++) {
            hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
        }
        logger.debug("Hex format : " + hexString.toString());
        return hexString.toString();
    }



    private void setUpRedissonClient() throws IOException{
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try {
            Config config = Config.fromJSON(new File(classLoader.getResource("singleNodeConfig.json").getFile()));
            client = Redisson.create(config);
        }catch (Exception ex){
            logger.error("error setting up redis client", ex);
        }

    }

    /**
     * Utility method to strip out DAT line from message payload before hashing
     * @param origString
     * @return
     * @throws Exception
     */
    private String removeDATSegment(String origString) throws Exception {

        String[] lines = origString.split("'\n");
        for(int i=0;i<lines.length;i++){
            if(lines[i].startsWith(DAT_HEADER_LABEL)){
                lines[i]=EMPTY_STRING;
            }
        }
        StringBuilder returnStr= new StringBuilder();
        for(String s:lines){
            if(!s.equals("")){
                returnStr.append(s).append("'\n");
            }
        }
        return returnStr.toString();
    }


}
