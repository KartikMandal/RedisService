package com.kamical.redis.app.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.Jedis;

import java.io.Serializable;

@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableCaching
public class RedisConfig<K,V> {

    @Autowired
    private CacheManager cacheManager;

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }


    // Create a RedisConnectionFactory
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Configure the LettuceConnectionFactory
        return new LettuceConnectionFactory();
    }

    @Bean
    public <K,V> RedisTemplate<K, V> redisTemplate() {
        RedisTemplate<K, V> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());

        // Optionally, you can set key and value serializers
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));

        return template;
    }

    // Create a RedisTemplate bean
    @Bean
    public <K, V> RedisTemplate<K, V> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<K, V> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // Set the serializers for keys and values
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));

        return template;
    }

    // Create a RedisTemplate bean
    @Bean
    public <K, V> RedisTemplate<K, V> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<K, V> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // Set the serializers for keys and values
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));

        return template;
    }

   /* @Bean
    public RedisTemplate<String, Serializable> redisCacheTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Serializable> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }*/

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        RedisCacheConfiguration redisCacheConfiguration = config
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));
        RedisCacheManager redisCacheManager = RedisCacheManager.builder(factory).cacheDefaults(redisCacheConfiguration)
                .build();
        return redisCacheManager;
    }

    @PostConstruct
    public void clearCache() {
        System.out.println("In Clear Cache");
        Jedis jedis = new Jedis(redisHost, redisPort, timeout);
        jedis.flushAll();
        jedis.close();
    }


}
