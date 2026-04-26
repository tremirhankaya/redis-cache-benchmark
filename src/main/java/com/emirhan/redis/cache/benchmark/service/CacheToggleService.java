package com.emirhan.redis.cache.benchmark.service;

import com.emirhan.redis.cache.benchmark.dto.CacheStatusDto;

public interface CacheToggleService {
    boolean isEnabled();

    CacheStatusDto toggle(boolean enabled);

    void clearAll();

    CacheStatusDto status();
}
