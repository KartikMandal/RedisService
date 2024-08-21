package com.kamical.redis.app.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableCaching
@Service
public class RedisConfig<K, V> implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);
    private static volatile RedisTemplate redisTemplate;

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.timeout}")
    private int timeout;


    @Value("${spring.redis.username}")
    private String username;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.ssl}")
    private boolean ssl;

    @PostConstruct
    public void init() {
        log.info("In Clear Cache");
    }

    // Create a RedisConnectionFactory
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisHost);
        redisConfig.setPort(redisPort);
        redisConfig.setPassword(password);
        //redisConfig.setUsername(username);

        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = LettuceClientConfiguration.builder();
        if (ssl) {
            builder.useSsl();
        }
        LettuceClientConfiguration clientConfiguration = builder.commandTimeout(Duration.ofMillis(timeout)).build();
        // Configure the LettuceConnectionFactory
        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
    }


    // Create a RedisTemplate bean
    @Bean
    public <K, V> RedisTemplate<K, V> redisTemplate() {
        if (redisTemplate == null) {
            synchronized (RedisTemplate.class) {
                if (redisTemplate == null) {
                    try {
                        redisTemplate = new RedisTemplate();
                        redisTemplate.setConnectionFactory(redisConnectionFactory());
                        // Set the serializers for keys and values
                        redisTemplate.setKeySerializer(new StringRedisSerializer());
                        redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Object.class));
                    } catch (Exception e) {
                        log.error("redis db connection issue " + e);
                    }
                }
            }
        }
        return redisTemplate;
    }


    @Override
    public void close() throws Exception {
        RedisConnection redisConnection = null;
        try {
            if (redisTemplate != null && redisTemplate.getConnectionFactory() != null) {
                redisConnection = redisTemplate.getConnectionFactory().getConnection();
                redisConnection.close();
            }
        } catch (Exception e) {
            log.error("redis db close connection issue " + e);
        }
    }
}
