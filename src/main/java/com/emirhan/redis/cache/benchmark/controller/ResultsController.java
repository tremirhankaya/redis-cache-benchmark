package com.emirhan.redis.cache.benchmark.controller;

import com.emirhan.redis.cache.benchmark.dto.ComparisonDto;
import com.emirhan.redis.cache.benchmark.dto.TestRunDto;
import com.emirhan.redis.cache.benchmark.service.ResultsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/results")
public class ResultsController {
    private final ResultsService resultsService;

    @PostMapping("/upload")
    public ResponseEntity<TestRunDto> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam String name,
            @RequestParam String scenario,
            @RequestParam int load) {
        log.info("Results upload request name={} scenario={} load={} size={}",
                name, scenario, load, file.getSize());
        return ResponseEntity.ok(resultsService.upload(file, name, scenario, load));
    }

    @GetMapping
    public ResponseEntity<List<TestRunDto>> list() {
        return ResponseEntity.ok(resultsService.findAll());
    }

    @PostMapping("/compare")
    public ResponseEntity<ComparisonDto> compare(@RequestBody List<Long> runIds) {
        log.info("Results compare request runIds={}", runIds);
        return ResponseEntity.ok(resultsService.compare(runIds));
    }
}
