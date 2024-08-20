package com.kamical.redis.app.impl;

import com.kamical.redis.app.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

@Service
public class RedisServiceImpl<K, V> implements RedisService<K,V> {

    private static String WILDCARD = "*";

    @Autowired
    private final RedisTemplate<K, V> redisTemplate;

    @Autowired
    public RedisServiceImpl(RedisTemplate<K, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Autowired
    private CacheManager cacheManager;


    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    // @Override
    public Set<Object> readAllDataFromRedis() {

        return Collections.singleton(redisTemplate.keys((K) WILDCARD));
    }

    //@Override
    public Object readDataFromRedisWithKey(final String key) {
        return redisTemplate.keys((K) (key + WILDCARD));
    }

    //@Override
    public void evictAll() {

        getCacheManager()
                .getCacheNames()
                .forEach(
                        cacheName -> getCacheManager().getCache(cacheName).clear()
                );
    }

    // @Override
    public void evictByKey(final String key) {

        getCacheManager()
                .getCache(key)
                .clear();
    }


    public void writeToRedis(K key, V value) {
        put(key, value, redisTemplate);
    }

    public V readFromRedis(K key) {
        return get(key, redisTemplate);
    }

    public void deleteKey(K key) {
        redisTemplate.delete(key);
    }

    public V getAndDelete(K key) {
        // Step 1: Retrieve the value associated with the key
        V value = redisTemplate.opsForValue().get(key);
        // Step 2: Delete the key from Redis
        redisTemplate.delete(key);
        // Return the retrieved value
        return value;
    }

    public V getOrLoad(K key, Supplier<V> loader) {
        // Attempt to get the value from Redis
        V value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            // If the value is not in Redis, load it using the provided loader
            value = loader.get();
            // Store the loaded value in Redis
            redisTemplate.opsForValue().set(key, value);
        }

        return value;
    }


    private void put(K key, V value, RedisTemplate<K, V> redisTemplate) {
        redisTemplate.opsForValue().set(key, value);
    }

    private V get(K key, RedisTemplate<K, V> redisTemplate) {
        return redisTemplate.opsForValue().get(key);
    }
}

