/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.gtas.util;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 *
 * @author gbays
 */
public enum RedisClientSingletonEnum 
{
   INSTANCE; 
   
   private RedissonClient client;
   
   private String redisConnectionString = "redis://0.0.0.0:6379";
   
   RedisClientSingletonEnum()
   {
       if (client == null)
       {
           System.out.println("creating Redis client");
           Config config = new Config();
           config.useSingleServer().setAddress(redisConnectionString);
           client = Redisson.create(config); 
       }              
   }
   
   public RedissonClient getRedisClient()
   {
       return client;
   }
}
