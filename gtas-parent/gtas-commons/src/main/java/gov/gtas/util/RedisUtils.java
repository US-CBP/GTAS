/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.gtas.util;

import gov.gtas.model.PassengerIDTag;
import gov.gtas.model.redis.PersonHistoryBucket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.redisson.api.RBucket;
import org.redisson.api.RRemoteService;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 *
 * @author gbays
 */
public class RedisUtils {
    
    private static RedissonClient client;
    private Config config = new Config();
    private RRemoteService service;  
    
    public static void main(String[] args)
    {
        initialize();
    }
    
    public static void initialize()
    {
       long startClient = System.currentTimeMillis();
       client = RedisClientSingletonEnum.INSTANCE.getRedisClient();
       long endClient = System.currentTimeMillis();
       System.out.println("Time to get client: " + (endClient - startClient));
       putPersonBorderCrossingInfoOnRedis();

    }
    
    public static void storePersonHistoryBorderCrossingsOnRedis(List<PassengerIDTag> passengerIDTags, String flightDirection)
    {
       client = RedisClientSingletonEnum.INSTANCE.getRedisClient(); 
       RBucket<PersonHistoryBucket> bucket = client.getBucket("personhistory");
       PersonHistoryBucket phBucket = bucket.get();
       
       if (phBucket == null)
       {
           PersonHistoryBucket personHistoryBucket = new PersonHistoryBucket();
           bucket.set(personHistoryBucket);
           phBucket = bucket.get();
       }
       
       
       Map<String, Integer> inboundMap = phBucket.getInboundMap();
       Map<String, Integer> outboundMap = phBucket.getOutboundMap();
       
       for (PassengerIDTag pId : passengerIDTags)
       {
           if (pId.getDocHashId() != null)
           {
               if (flightDirection.equalsIgnoreCase("I") )
               {
                 if (!inboundMap.isEmpty())
                 {
                     Integer inInt = inboundMap.get(pId.getDocHashId());
                     if (inInt != null)
                     {
                        inboundMap.put(pId.getDocHashId(), inInt + 1); 
                     }
                     else
                     {
                        inboundMap.put(pId.getDocHashId(),1); 
                     }
                 }
                 else
                 {
                    inboundMap.put(pId.getDocHashId(),1); 
                 }
               }
               else if (flightDirection.equalsIgnoreCase("O"))
               {
                     Integer outInt = outboundMap.get(pId.getDocHashId());
                     if (outInt != null)
                     {
                        outboundMap.put(pId.getDocHashId(), outInt + 1); 
                     }
                     else
                     {
                        outboundMap.put(pId.getDocHashId(),1); 
                     }
                 }
                 else
                 {
                    outboundMap.put(pId.getDocHashId(),1); 
                 }               
               }  
           }
       
       bucket.set(phBucket);

    }

    public static void putPersonBorderCrossingInfoOnRedis()
    {
      
      PersonHistoryBucket personHistoryBucket = new PersonHistoryBucket();
      //personHistoryBucket.setMap(map);
      
      Map<String, Integer> inboundMap = new HashMap<>();

      inboundMap.put("q1w2e3r4t5y6u7i8", 6);
      
      Map<String, Integer> outboundMap = new HashMap<>();

      outboundMap.put("q1w2e3r4t5y6u7i8", 8);
      
      personHistoryBucket.setInboundMap(inboundMap);
      personHistoryBucket.setOutboundMap(outboundMap);
      
      RBucket<PersonHistoryBucket> bucket = client.getBucket("personhistory");
      bucket.set(personHistoryBucket);
      PersonHistoryBucket phb = bucket.get();
      
     
      Map<String, Integer> inboundMap2 = phb.getInboundMap();
      Map<String, Integer> outboundMap2 = phb.getOutboundMap();
      Integer outInt = outboundMap2.get("q1w2e3r4t5y6u7i8");
      Integer inInt = inboundMap2.get("q1w2e3r4t5y6u7i8");
      outboundMap2.put("q1w2e3r4t5y6u7i8", outInt + 2);
      inboundMap2.put("q1w2e3r4t5y6u7i8", inInt + 3);

      System.out.println(inboundMap2);
      System.out.println(outboundMap2);
      
      bucket.set(phb);

        long startClient = System.currentTimeMillis();
        RedissonClient client2 = RedisClientSingletonEnum.INSTANCE.getRedisClient();
        long endClient = System.currentTimeMillis();
        System.out.println("Time to get client2: " + (endClient - startClient));       
        //System.out.println(client == client2);
        
        client.shutdown();

        
    }
    
}
