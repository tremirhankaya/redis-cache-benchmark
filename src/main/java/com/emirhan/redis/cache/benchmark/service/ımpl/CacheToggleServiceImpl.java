package com.emirhan.redis.cache.benchmark.service.ımpl;

import com.emirhan.redis.cache.benchmark.dto.CacheStatusDto;
import com.emirhan.redis.cache.benchmark.service.CacheToggleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheToggleServiceImpl implements CacheToggleService {

    private final AtomicBoolean enabled = new AtomicBoolean(false);
    private final CacheManager cacheManager;
    private final RedisConnectionFactory redisConnectionFactory;


    @Override
    public boolean isEnabled() {
        return enabled.get();
    }

    @Override
    public CacheStatusDto toggle(boolean newValue) {
        boolean previous = enabled.getAndSet(newValue);
        log.info("Cache toggle {} -> {}", previous, newValue);
        return status();
    }

    @Override
    public void clearAll() {
        try (RedisConnection conn = redisConnectionFactory.getConnection()) {
            conn.serverCommands().flushDb();
            log.info("Redis DB flushed completely.");
        } catch (Exception ex) {
            log.warn("Redis flushDb failed: {}", ex.getMessage());
        }
    }

    @Override
    public CacheStatusDto status() {
        long keys = redisDbSize();
        return CacheStatusDto.builder()
                .enabled(enabled.get())
                .keyCount(keys)
                .cacheManager(cacheManager.getClass().getSimpleName())
                .build();
    }


    private long redisDbSize() {
        try (RedisConnection conn = redisConnectionFactory.getConnection()) {
            Long size = conn.serverCommands().dbSize();
            return size != null ? size : 0L;
        } catch (Exception ex) {
            log.warn("Redis DBSIZE alınamadı: {}", ex.getMessage());
            return -1L;
        }
    }
}
