/*
 * All GTAS code is Copyright 2016, Unisys Corporation.
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.spring.cache.HazelcastCacheManager;

@Configuration
@EnableCaching
public class CachingConfig {
    @Bean(name = "cacheManager")
    HazelcastCacheManager hazelcastcacheManager() throws Exception {
        return new HazelcastCacheManager(Hazelcast.newHazelcastInstance());
    }
}
