package com.msa.gateway.redis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private Integer port;

    @Value("${spring.data.redis.password}")
    private String password;

    // WebFlux를 위한 Lettuce Reactive Connection 설정
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        config.setDatabase(0);
        if (password != null && !password.isEmpty()) {
            config.setPassword(RedisPassword.of(password));
        }
        return new LettuceConnectionFactory(config);
    }

    // 비동기 처리를 위한 ReactiveStringRedisTemplate 빈 등록
    @Bean
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        return new ReactiveStringRedisTemplate(redisConnectionFactory);
    }
}