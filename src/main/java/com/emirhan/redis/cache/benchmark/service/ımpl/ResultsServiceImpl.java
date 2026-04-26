package com.emirhan.redis.cache.benchmark.service.ımpl;

import com.emirhan.redis.cache.benchmark.dto.ComparisonDto;
import com.emirhan.redis.cache.benchmark.dto.TestRunDto;
import com.emirhan.redis.cache.benchmark.entity.TestRun;
import com.emirhan.redis.cache.benchmark.repository.TestRunRepository;
import com.emirhan.redis.cache.benchmark.service.ResultsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



@Slf4j
@Service
@RequiredArgsConstructor
public class ResultsServiceImpl implements ResultsService {

    private final TestRunRepository testRunRepository;

    @Override
    @Transactional
    public TestRunDto upload(MultipartFile file, String name, String scenario, int load) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("CSV dosyası boş");
        }
        Stats stats = parseJmeterCsv(file);
        TestRun entity = new TestRun();
        entity.setName(name);
        entity.setScenario(scenario);
        entity.setLoad(load);
        entity.setTotalRequests(stats.total);
        entity.setAvgMs(stats.avgMs);
        entity.setMinMs(stats.minMs);
        entity.setMaxMs(stats.maxMs);
        entity.setP50(stats.p50);
        entity.setP90(stats.p90);
        entity.setP95(stats.p95);
        entity.setP99(stats.p99);
        entity.setThroughput(stats.throughput);
        entity.setErrorRate(stats.errorRate);
        entity.setStddev(stats.stddev);
        entity.setCacheHitRatio(stats.cacheHitRatio);
        entity.setUploadedAt(LocalDateTime.now());

        TestRun saved = testRunRepository.save(entity);
        log.info("TestRun saved id={} requests={} avgMs={}", saved.getId(), stats.total, stats.avgMs);
        return TestRunDto.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestRunDto> findAll() {

        return testRunRepository.findAll(Sort.by(Sort.Direction.DESC, "uploadedAt"))
                .stream()
                .map(TestRunDto::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ComparisonDto compare(List<Long> runIds) {
        if (runIds == null || runIds.isEmpty()) {
            return ComparisonDto.builder()
                    .runs(List.of())
                    .labels(List.of())
                    .avgMsSeries(List.of())
                    .p95Series(List.of())
                    .p99Series(List.of())
                    .throughputSeries(List.of())
                    .errorRateSeries(List.of())
                    .build();
        }
        List<TestRunDto> runs = testRunRepository.findAllById(runIds).stream()
                .map(TestRunDto::from)
                .toList();
        return ComparisonDto.builder()
                .runs(runs)
                .labels(runs.stream().map(TestRunDto::getName).toList())
                .avgMsSeries(runs.stream().map(TestRunDto::getAvgMs).toList())
                .p95Series(runs.stream().map(TestRunDto::getP95).toList())
                .p99Series(runs.stream().map(TestRunDto::getP99).toList())
                .throughputSeries(runs.stream().map(TestRunDto::getThroughput).toList())
                .errorRateSeries(runs.stream().map(TestRunDto::getErrorRate).toList())
                .build();
    }



    private Stats parseJmeterCsv(MultipartFile file) {
        List<Long> elapsed = new ArrayList<>();
        long errors = 0;
        long cacheHits = 0;
        long cacheMisses = 0;
        long firstTs = Long.MAX_VALUE;
        long lastTs = Long.MIN_VALUE;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .build()
                     .parse(reader)) {

            for (CSVRecord r : parser) {
                long e = parseLong(r, "elapsed", 0L);
                long ts = parseLong(r, "timeStamp", 0L);
                boolean success = "true".equalsIgnoreCase(get(r, "success"));

                elapsed.add(e);
                if (!success) errors++;
                if (ts > 0) {
                    if (ts < firstTs) firstTs = ts;
                    if (ts > lastTs) lastTs = ts;
                }


                String headers = get(r, "responseHeaders");
                if (headers != null) {
                    String lower = headers.toLowerCase();
                    if (lower.contains("x-cache-status: hit"))  cacheHits++;
                    else if (lower.contains("x-cache-status: miss")) cacheMisses++;
                }
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("CSV okunamadı: " + ex.getMessage(), ex);
        }

        return Stats.from(elapsed, errors, firstTs, lastTs, cacheHits, cacheMisses);
    }

    private static long parseLong(CSVRecord r, String col, long fallback) {
        try {
            String v = get(r, col);
            return v == null || v.isEmpty() ? fallback : Long.parseLong(v.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static String get(CSVRecord r, String col) {
        return r.isMapped(col) ? r.get(col) : null;
    }


    private record Stats(
            long total, double avgMs, double minMs, double maxMs,
            double p50, double p90, double p95, double p99,
            double throughput, double errorRate, double stddev,
            double cacheHitRatio
    ) {
        static Stats from(List<Long> elapsed, long errors, long firstTs, long lastTs,
                          long hits, long misses) {
            long total = elapsed.size();
            if (total == 0) {
                return new Stats(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            }
            Collections.sort(elapsed);
            double sum = 0, sumSq = 0;
            for (long v : elapsed) { sum += v; sumSq += (double) v * v; }
            double avg = sum / total;
            double variance = (sumSq / total) - (avg * avg);
            double stddev = Math.sqrt(Math.max(0, variance));
            double durationSec = (lastTs > firstTs) ? (lastTs - firstTs) / 1000.0 : 0;
            double throughput = durationSec > 0 ? total / durationSec : 0;
            double errorRate = (errors * 100.0) / total;
            double hitRatio = (hits + misses) > 0 ? (double) hits / (hits + misses) : 0;

            return new Stats(
                    total,
                    avg,
                    elapsed.get(0),
                    elapsed.get((int) total - 1),
                    percentile(elapsed, 50),
                    percentile(elapsed, 90),
                    percentile(elapsed, 95),
                    percentile(elapsed, 99),
                    throughput,
                    errorRate,
                    stddev,
                    hitRatio
            );
        }

        private static double percentile(List<Long> sorted, int p) {
            if (sorted.isEmpty()) return 0;
            int idx = (int) Math.ceil((p / 100.0) * sorted.size()) - 1;
            return sorted.get(Math.max(0, Math.min(sorted.size() - 1, idx)));
        }
    }
}
