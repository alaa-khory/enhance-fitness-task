package com.example.enhance_fitness_task.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class IdempotencyService {

    private final Map<String, Boolean> requestTracker = new ConcurrentHashMap<>();

    public boolean isRequestProcessed(String requestId) {
        return requestTracker.containsKey(requestId);
    }

    public void markRequestProcessed(String requestId) {
        requestTracker.put(requestId, true);
    }

    public String generateRequestId() {
        return UUID.randomUUID().toString();
    }
}
