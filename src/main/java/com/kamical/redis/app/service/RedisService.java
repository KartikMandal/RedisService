package com.kamical.redis.app.service;

import java.util.Set;
import java.util.function.Supplier;

public interface RedisService <K,V>{

    Set<Object> readAllDataFromRedis();

    Object readDataFromRedisWithKey(final String key);

    void evictAll();

    void evictByKey(final String key);

    void writeToRedis(K key, V value);

    V readFromRedis(K key);

    V getAndDelete(K key);

    V getOrLoad(K key, Supplier<V> loader);

}
