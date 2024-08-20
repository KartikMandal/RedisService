package com.kamical.redis.app.controller;

import com.kamical.redis.app.impl.RedisServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/read")
    public V readValue(@RequestParam K key) {
        return redisServiceImpl.readFromRedis(key);
    }

    @DeleteMapping("/delete")
    public void deleteKey(@RequestParam K key) {
        redisServiceImpl.deleteKey(key);
    }

    @GetMapping("/get-or-load")
    public String getOrGenerateUUID(@RequestParam String key) {
        return getOrGenerateUUID(key);
    }

    @DeleteMapping("/get-and-delete")
    public V getAndDelete(@RequestParam K key) {
        return redisServiceImpl.getAndDelete(key);
    }



    private String getOrGenerateUUID(K key) {
        return (String) redisServiceImpl.getOrLoad(key, () -> {
            // Simulate an expensive operation
            return (V) UUID.randomUUID().toString();
        });
    }
}