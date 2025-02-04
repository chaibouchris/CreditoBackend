package com.credito.creditobackend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
public class CreditoRedisConfig {
    private static final Logger logger = LoggerFactory.getLogger(CreditoRedisConfig.class);

    @Bean
    public CacheManager cacheManager() {
        try {
            LettuceConnectionFactory redisConnectionFactory = new LettuceConnectionFactory();
            redisConnectionFactory.afterPropertiesSet();
            redisConnectionFactory.getConnection().ping();

            RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(10))
                    .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

            logger.info("✅ Connected to Redis! Redis Cache Manager has been activated.");
            return RedisCacheManager.builder(redisConnectionFactory)
                    .cacheDefaults(cacheConfig)
                    .build();
        } catch (Exception e) {
            logger.warn("⚠️ Redis is unavailable! Switching to in-memory cache.", e);
            return new ConcurrentMapCacheManager();
        }
    }
}
