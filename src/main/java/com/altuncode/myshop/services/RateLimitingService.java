package com.altuncode.myshop.services;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * Creates or retrieves a bucket for a given key with custom limits.
     */
    public Bucket resolveBucket(String key, Bandwidth bandwidths) {
        return buckets.computeIfAbsent(key, k -> {
            var bucketBuilder = Bucket.builder();
            if (bandwidths != null) {
                bucketBuilder.addLimit(bandwidths);
            }
            // Create a bucket with all provided bandwidths
            return bucketBuilder.build();
        });
    }

    /**
     * Checks if the user is allowed to proceed based on the rate limits.
     */
    public boolean consumeToken(String key, Bandwidth bandwidths) {
        Bucket bucket = resolveBucket(key, bandwidths);
        return bucket.tryConsume(1); // Attempt to consume 1 token
    }

    public boolean checkToken(String key, Bandwidth bandwidths) {
        Bucket bucket = resolveBucket(key, bandwidths);
        return bucket.getAvailableTokens() > 0; // Attempt to consume 1 token
    }
}
