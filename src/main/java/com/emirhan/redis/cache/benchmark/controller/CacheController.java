package com.emirhan.redis.cache.benchmark.controller;

import com.emirhan.redis.cache.benchmark.dto.CacheStatusDto;
import com.emirhan.redis.cache.benchmark.service.CacheToggleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/cache")
public class CacheController {
    private final CacheToggleService cacheToggleService;

    @PostMapping("/toggle")
    public ResponseEntity<CacheStatusDto> toggle(@RequestParam boolean enabled) {
        log.info("Cache toggle request enabled={}", enabled);
        return ResponseEntity.ok(cacheToggleService.toggle(enabled));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clear() {
        log.info("Cache clear request received");
        cacheToggleService.clearAll();
        log.info("Cache cleared");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status")
    public ResponseEntity<CacheStatusDto> status() {
        return ResponseEntity.ok(cacheToggleService.status());
    }
}
