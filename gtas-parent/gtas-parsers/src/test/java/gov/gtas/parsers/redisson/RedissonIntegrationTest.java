/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *
 * Please see LICENSE.txt for details.
 */
package gov.gtas.parsers.redisson;


import gov.gtas.parsers.redisson.model.Ledger;
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
import redis.embedded.RedisServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RedissonIntegrationTest {

    private static RedisServer redisServer;
    private static RedissonClient client;
    private static String[] randomStrings;

    //@BeforeClass
    public static void setUp() throws IOException {
//        redisServer = new RedisServer(6379);
//        redisServer.start();
//        client = Redisson.create();
        //randomStrings[0] = getMessageHash();

    }

    //@AfterClass
    public static void destroy() {
        //  redisServer.stop();
        client.shutdown();
    }


    private void setUpRedissonClient() throws IOException{
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        File _tempFile = new File(classLoader.getResource("singleNodeConfig.json").getFile());
        File _file = new File("C:\\Users\\GTAS7\\projects\\SourceTree\\ML_Git\\GTAS_Merge\\GTAS\\gtas-parent\\gtas-parsers\\src\\test\\resources\\singleNodeConfig.json");
        FileInputStream fis = new FileInputStream("C:\\Users\\GTAS7\\projects\\SourceTree\\ML_Git\\GTAS_Merge\\GTAS\\gtas-parent\\gtas-parsers\\src\\test\\resources\\singleNodeConfig.json");
        try {
            Config config = Config.fromJSON(fis);
            client = Redisson.create(config);
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    //@Test
    public void givenASet_thenSaveSetToRedis(){
        try {
            setUpRedissonClient();
        }catch (Exception ioex) {ioex.printStackTrace();}
        RSet<Ledger> ledgerSet = client.getSet("ledgerSet");
        ledgerSet.add(new Ledger("ledger"));

        assertTrue(ledgerSet.contains(new Ledger("ledger")));
    }

    //@Test
    public void redisObjectLookUpPersist(){
        RLiveObjectService service = client.getLiveObjectService();

        LedgerLiveObject ledger = new LedgerLiveObject();
        try {
            //ledger.setName("ledger1");
            String messageHashKey = getMessageHash();

            // query Redis with the Key
            LedgerLiveObject returnLedger
                    = service.get(LedgerLiveObject.class, messageHashKey);

            if(!returnLedger.getName().equals(messageHashKey)) {
                //persist into Redis
                ledger.setName(getMessageHash());
                ledger = service.persist(ledger);
            }

            System.out.println(ledger.getName());
            System.out.println(returnLedger.getName());

            assertTrue(ledger.getName().equals(returnLedger.getName()));

        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    private static String getMessageHash() throws IOException, NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-1");
        FileInputStream fis = new FileInputStream("c:\\Message\\9225_PNR_009.txt");

        byte[] dataBytes = new byte[1024];

        int nread = 0;
        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        };
        byte[] mdbytes = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        System.out.println("Hex format : " + sb.toString());

        //convert the byte to hex format method 2
        StringBuffer hexString = new StringBuffer();
        for (int i=0;i<mdbytes.length;i++) {
            hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
        }

        System.out.println("Hex format : " + hexString.toString());

        return hexString.toString();
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
        System.out.println("Hex format : " + hexString.toString());
        return hexString.toString();
    }

}
