package com.emirhan.redis.cache.benchmark.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "test_run")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 32)
    private String scenario;

    @Column(name = "load_users", nullable = false)
    private int load;

    @Column(name = "total_requests")
    private long totalRequests;

    @Column(name = "avg_ms")
    private double avgMs;

    @Column(name = "min_ms")
    private double minMs;

    @Column(name = "max_ms")
    private double maxMs;

    private double p50;
    private double p90;
    private double p95;
    private double p99;

    private double throughput;

    @Column(name = "error_rate")
    private double errorRate;

    private double stddev;

    @Column(name = "cache_hit_ratio")
    private double cacheHitRatio;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;
}
