package com.emirhan.redis.cache.benchmark.controller;

import com.emirhan.redis.cache.benchmark.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminDataController {
    private final AdminService adminService;

    @DeleteMapping("/delete-all")
    public ResponseEntity<Void> deleteAllData() {
        log.info("Data clean request received");
        adminService.cleanAllDatabases();
        log.info("All data cleaned from databases");

        return ResponseEntity.noContent().build();
    }

//    @PostMapping
//    public ResponseEntity<Void> seedData() {
//
//    }
}
