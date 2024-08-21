package com.kamical.redis.app.impl;

import com.kamical.redis.app.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Supplier;

@Service
public class RedisServiceImpl<K, V> implements RedisService<K, V> {

    private static String WILDCARD = "*";

    @Autowired
    private final RedisTemplate<K, V> redisTemplate;

    @Autowired
    public RedisServiceImpl(RedisTemplate<K, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public void writeToRedis(K key, V value) {
        put(key, value, redisTemplate);
    }

    @Override
    public void batchWrite(Map<K, V> map) {
        // Use the executePipelined method with a SessionCallback to perform batch operations
        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) {
                map.forEach((key, value) -> {
                    operations.opsForValue().set((K) key, (V) value);
                });
                return null;
            }
        });
    }

    @Override
    public List<V> readDataWithWildCard(K key) {
        Set<K> keys = redisTemplate.keys((K) (key + WILDCARD));
        List<V> res = new ArrayList<>();
        if (keys != null) {
            for (K k1 : keys) {
                V value = redisTemplate.opsForValue().get(k1);
                res.add(value);
            }

        }
        return res;
        //return List.of();
    }

    @Override
    public V readFromRedis(K key) {
        return get(key, redisTemplate);
    }

    @Override
    public Map<K, V> readAll() {
        Set<K> keys = redisTemplate.keys((K) WILDCARD);
        Map<K, V> res = new HashMap<>();
        if (keys != null) {
            for (K k1 : keys) {
                V value = redisTemplate.opsForValue().get(k1);
                res.put(k1, value);
            }
        }
        return res;
        // return Map.of();
    }

    @Override
    public Set<Object> readAllDataFroRedis() {
        return Set.of(redisTemplate.keys((K) WILDCARD));
    }

    @Override
    public void deleteKey(K key) {
        redisTemplate.delete(key);
    }

    @Override
    public V getAndDelete(K key) {
        V val = redisTemplate.opsForValue().get(key);
        redisTemplate.delete(key);
        return val;
    }

    @Override
    public void deleteAll() {
        Set<K> keys = redisTemplate.keys((K) WILDCARD);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Override
    public V getOrLoad(K key, Supplier<V> loader) {
        V val = redisTemplate.opsForValue().get(key);

        if (val != null) {
            val = loader.get();
            redisTemplate.opsForValue().set(key, val);
        }

        return val;
    }

    private void put(K key, V value, RedisTemplate<K, V> redisTemplate) {
        redisTemplate.opsForValue().set(key, value);
    }

    private V get(K key, RedisTemplate<K, V> redisTemplate) {
        return redisTemplate.opsForValue().get(key);
    }
}

