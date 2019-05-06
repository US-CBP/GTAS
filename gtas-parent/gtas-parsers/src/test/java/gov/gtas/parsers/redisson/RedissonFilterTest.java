/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.redisson;

import gov.gtas.parsers.redisson.model.LedgerLiveObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.RedissonMultiLock;
import org.redisson.api.*;
import org.redisson.client.RedisClient;
import org.redisson.client.RedisConnection;
import org.redisson.client.codec.StringCodec;
import org.redisson.client.protocol.RedisCommands;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RedissonFilterTest {

    private static final Logger logger = LoggerFactory.getLogger(RedissonFilterTest.class);

    private static RedissonClient client;
    private static String[] randomStrings;


    private void setUpRedissonClient() throws IOException{
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try {
            Config config = Config.fromJSON(new File(classLoader.getResource("singleNodeConfig.json").getFile()));
            client = Redisson.create(config);
        }catch (Exception ex){
            logger.error("error!", ex);
        }

    }


    //@Test
    public void redisObjectLookUpPersist(String messagePayload, Date messageTimestamp){

        try {
            setUpRedissonClient();
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
            }


        }catch(Exception ex){
            logger.error("error!", ex);
        }

    }

//    private static String getMessageHash() throws IOException, NoSuchAlgorithmException {
//
//        MessageDigest md = MessageDigest.getInstance("SHA-1");
//        FileInputStream fis = new FileInputStream("c:\\Message\\9225_PNR_009.txt");
//
//        byte[] dataBytes = new byte[1024];
//
//        int nread = 0;
//        while ((nread = fis.read(dataBytes)) != -1) {
//            md.update(dataBytes, 0, nread);
//        };
//        byte[] mdbytes = md.digest();
//
//        //convert the byte to hex format method 1
//        StringBuffer sb = new StringBuffer();
//        for (int i = 0; i < mdbytes.length; i++) {
//            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
//        }
//
//        logger.info("Hex format : " + sb.toString());
//
//        //convert the byte to hex format method 2
//        StringBuffer hexString = new StringBuffer();
//        for (int i=0;i<mdbytes.length;i++) {
//            hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
//        }
//
//        logger.info("Hex format : " + hexString.toString());
//
//        return hexString.toString();
//    }

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
        logger.info("Hex format : " + hexString.toString());
        return hexString.toString();
    }

}
