package com.emirhan.redis.cache.benchmark.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "internal_test_run")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternalTestRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String scenario;
    private String endpoint;
    @Column(name = "request_count")
    private Integer requestCount;
    @Column(name = "test_seed")
    private Integer testSeed;
    @Column(name = "avg_ms")
    private Double avgMs;
    @Column(name = "min_ms")
    private Double minMs;
    @Column(name = "max_ms")
    private Double maxMs;
    @Column(name = "median_ms")
    private Double medianMs;
    @Column(name = "p95_ms")
    private Double p95Ms;
    private Integer hits;
    private Integer miss;
    private Integer disabled;
    @Column(name = "hit_ratio")
    private String hitRatio;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
