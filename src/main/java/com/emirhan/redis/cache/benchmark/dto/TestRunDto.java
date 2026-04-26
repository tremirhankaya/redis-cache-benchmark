package com.emirhan.redis.cache.benchmark.dto;

import com.emirhan.redis.cache.benchmark.entity.TestRun;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestRunDto implements Serializable {

    private Long id;
    private String name;
    private String scenario;
    private int load;
    private long totalRequests;
    private double avgMs;
    private double minMs;
    private double maxMs;
    private double p50;
    private double p90;
    private double p95;
    private double p99;
    private double throughput;
    private double errorRate;
    private double stddev;
    private double cacheHitRatio;
    private LocalDateTime uploadedAt;

    public static TestRunDto from(TestRun run) {
        if (run == null) return null;
        return TestRunDto.builder()
                .id(run.getId())
                .name(run.getName())
                .scenario(run.getScenario())
                .load(run.getLoad())
                .totalRequests(run.getTotalRequests())
                .avgMs(run.getAvgMs())
                .minMs(run.getMinMs())
                .maxMs(run.getMaxMs())
                .p50(run.getP50())
                .p90(run.getP90())
                .p95(run.getP95())
                .p99(run.getP99())
                .throughput(run.getThroughput())
                .errorRate(run.getErrorRate())
                .stddev(run.getStddev())
                .cacheHitRatio(run.getCacheHitRatio())
                .uploadedAt(run.getUploadedAt())
                .build();
    }
}
