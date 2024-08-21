package com.kamical.redis.app.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public interface RedisService<K, V> {

    void writeToRedis(final K key, final V value);

    void batchWrite(final Map<K, V> map);

    List<V> readDataWithWildCard(final K key);

    V readFromRedis(final K key);

    Map<K, V> readAll();

    Set<Object> readAllDataFroRedis();

    void deleteKey(final K key);

    V getAndDelete(K key);

    void deleteAll();

    V getOrLoad(final K key, final Supplier<V> loader);

}
