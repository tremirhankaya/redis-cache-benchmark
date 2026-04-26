package com.emirhan.redis.cache.benchmark.controller;

import com.emirhan.redis.cache.benchmark.entity.InternalTestRun;
import com.emirhan.redis.cache.benchmark.repository.InternalTestRunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/internal-runs")
@RequiredArgsConstructor
public class InternalTestRunController {

    private final InternalTestRunRepository repository;

    @PostMapping
    public ResponseEntity<InternalTestRun> saveRun(@RequestBody InternalTestRun run) {
        if (run.getCreatedAt() == null) {
            run.setCreatedAt(LocalDateTime.now());
        }
        return ResponseEntity.ok(repository.save(run));
    }

    @GetMapping
    public ResponseEntity<List<InternalTestRun>> getAllRuns() {
        return ResponseEntity.ok(repository.findAll());
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        repository.deleteAll();
        return ResponseEntity.ok().build();
    }
}
