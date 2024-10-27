package com.example.enhance_fitness_task.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitingConfig {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Bean
    public Map<String, Bucket> cache() {
        return cache;
    }

    public Bucket resolveBucket(String key) {
        return cache.computeIfAbsent(key, k -> {
            Refill refill = Refill.intervally(5, Duration.ofMinutes(1)); // Lower limit for testing (5 requests per minute)
            Bandwidth limit = Bandwidth.classic(5, refill);
            return Bucket.builder().addLimit(limit).build();
        });
    }

}
