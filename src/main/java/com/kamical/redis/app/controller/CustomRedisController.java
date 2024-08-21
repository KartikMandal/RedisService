package com.kamical.redis.app.controller;

import com.kamical.redis.app.impl.RedisServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/custom-redis")
public class CustomRedisController<K, V> {

    private final RedisServiceImpl<K, V> redisServiceImpl;

    @Autowired
    public CustomRedisController(RedisServiceImpl<K, V> redisServiceImpl) {
        this.redisServiceImpl = redisServiceImpl;
    }

    @PostMapping("/write")
    public void writeValue(@RequestParam K key, @RequestParam V value) {
        redisServiceImpl.writeToRedis(key, value);
    }

    @PostMapping("/batch-write")
    public void batchWriteValue(@RequestBody Map<K, V> map) {
        redisServiceImpl.batchWrite(map);
    }

    @GetMapping("/read")
    public V readValue(@RequestParam K key) {
        return redisServiceImpl.readFromRedis(key);
    }

    @GetMapping("/read-all")
    public Map<K, V> readAllValue() {
        return redisServiceImpl.readAll();
    }

    @GetMapping("/read-all-key-basic")
    public List<V> readAllValueKeyBasic(@RequestParam K key) {
        return redisServiceImpl.readDataWithWildCard(key);
    }

    @DeleteMapping("/delete")
    public void deleteKey(@RequestParam K key) {
        redisServiceImpl.deleteKey(key);
    }

    @DeleteMapping("/delete-all")
    public void deleteAllKey() {
        redisServiceImpl.deleteAll();
    }

    @DeleteMapping("/get-and-delete")
    public V getAndDelete(@RequestParam K key) {
        return redisServiceImpl.getAndDelete(key);
    }

    @GetMapping("/get-or-load")
    public String getOrGenerateUUID(@RequestParam String key) {
        return getOrGenerateUUID(key);
    }

    private String getOrGenerateUUID(K key) {
        return (String) redisServiceImpl.getOrLoad(key, () -> {
            // Simulate an expensive operation
            return (V) UUID.randomUUID().toString();
        });
    }
}