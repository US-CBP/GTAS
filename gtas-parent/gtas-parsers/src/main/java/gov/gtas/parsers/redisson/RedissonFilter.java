/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.redisson;

import gov.gtas.parsers.redisson.model.LedgerLiveObject;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.embedded.RedisServer;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;


public class RedissonFilter {

    private static final Logger LOG = LoggerFactory.getLogger(RedissonFilter.class);
    private static RedisServer redisServer;
    private RedissonClient client;
    private static String[] randomStrings;

    public RedissonFilter() {
    }

    public RedissonFilter(RedissonClient client) {
        this.client = client;
    }


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
                ledger = service.persist(ledger);
                if(!publishToDownstreamQueues(messagePayload)){throw new Exception("Error publishing to parsing queue");};
            }else{
                //key exists, derivative logic goes here (time processed and placement on Queues)
            }


        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    public void redisObjectLookUpPersist(String messagePayload, Date messageTimestamp, RLiveObjectService service){

        try {

            LedgerLiveObject ledger = new LedgerLiveObject();
            String messageHashKey = getMessageHash(messagePayload);

            // query Redis with the Key
            LedgerLiveObject returnLedger
                    = service.get(LedgerLiveObject.class, messageHashKey);

            if( (returnLedger == null) || (!returnLedger.getName().equals(messageHashKey))) {
                //persist into Redis
                ledger.setMessageTimeStamp(messageTimestamp);
                ledger.setProcessedTimeStamp(new Date());
                ledger.setName(getMessageHash(messagePayload));
                ledger = service.persist(ledger);
                LOG.info("++++++++++++++++++ REDIS Key Indexed +++++++++++++++++++++++++++++++++++");
                if(!publishToDownstreamQueues(messagePayload)){throw new Exception("Error publishing to parsing queue");};
            }else{
                //key exists, derivative logic goes here (time processed and placement on Queues)
                LOG.info("++++++++++++++++++ REDIS Key Exists +++++++++++++++++++++++++++++++++++");
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    private boolean publishToDownstreamQueues(String messagePayload){

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
        LOG.info("Hex format : " + hexString.toString());
        return hexString.toString();
    }



    private void setUpRedissonClient() throws IOException{
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try {
            Config config = Config.fromJSON(new File(classLoader.getResource("singleNodeConfig.json").getFile()));
            client = Redisson.create(config);
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }


}
