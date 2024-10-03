package com.encora.taskmanagementapplication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;

import java.time.Duration;

@Configuration
public class LoginConfig {
    @Bean
    public Bucket bucket() {
        Refill refill = Refill.greedy(5, Duration.ofMinutes(1)); // Allow 5 requests per minute
        Bandwidth limit = Bandwidth.classic(5, refill);
        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }
}
