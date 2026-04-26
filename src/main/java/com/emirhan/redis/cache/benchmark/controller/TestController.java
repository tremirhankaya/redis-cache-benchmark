package com.emirhan.redis.cache.benchmark.controller;

import com.emirhan.redis.cache.benchmark.dto.TestPrepStatusDto;
import com.emirhan.redis.cache.benchmark.service.TestPrepService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/test")
public class TestController {
    private final TestPrepService testPrepService;

    @PostMapping("/prepare")
    public ResponseEntity<TestPrepStatusDto> prepare(@RequestParam String scenario) {
        log.info("Test prepare request scenario={}", scenario);
        return ResponseEntity.accepted().body(testPrepService.prepare(scenario));
    }

    @GetMapping("/status")
    public ResponseEntity<TestPrepStatusDto> status() {
        return ResponseEntity.ok(testPrepService.status());
    }
}
