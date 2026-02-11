package com.shopmanager.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_MS = 60_000;

    private final ConcurrentHashMap<String, Attempt> attempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String username) {
        Attempt attempt = attempts.get(username);
        return attempt != null &&
                attempt.count >= MAX_ATTEMPTS &&
                Instant.now().toEpochMilli() - attempt.timestamp < WINDOW_MS;
    }

    public void recordFailure(String username) {
        attempts.compute(username, (k, v) -> {
            if (v == null || expired(v)) {
                return new Attempt(1);
            }
            v.count++;
            return v;
        });
    }

    public void reset(String username) {
        attempts.remove(username);
    }

    private boolean expired(Attempt a) {
        return Instant.now().toEpochMilli() - a.timestamp > WINDOW_MS;
    }

    private static class Attempt {
        int count;
        long timestamp = Instant.now().toEpochMilli();
        Attempt(int count) { this.count = count; }
    }
}