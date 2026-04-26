package com.emirhan.redis.cache.benchmark.service.ımpl;

import com.emirhan.redis.cache.benchmark.dto.TestPrepStatusDto;
import com.emirhan.redis.cache.benchmark.repository.BrandRepository;
import com.emirhan.redis.cache.benchmark.repository.CategoryRepository;
import com.emirhan.redis.cache.benchmark.repository.ProductRepository;
import com.emirhan.redis.cache.benchmark.service.CacheToggleService;
import com.emirhan.redis.cache.benchmark.service.ProductService;
import com.emirhan.redis.cache.benchmark.service.TestPrepService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;



@Slf4j
@Service
@RequiredArgsConstructor
public class TestPrepServiceImpl implements TestPrepService {



    private static final int WARMUP_BY_ID_QUERIES = 400;
    private static final int WARMUP_BY_CATEGORY_QUERIES = 300;
    private static final int WARMUP_SEARCH_QUERIES = 300;
    private static final int MIXED_POPULAR_COUNT = 20_000;


    private static final String[] SEARCH_TERMS = {
            "a", "e", "i", "o", "co", "in", "le", "er", "an", "or", "ic", "al", "en", "te"
    };

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final CacheToggleService cacheToggleService;


    private final AtomicReference<TestPrepStatusDto> current = new AtomicReference<>(idle());

    @Override
    public TestPrepStatusDto prepare(String scenario) {
        TestPrepStatusDto running = current.get();
        if (running != null && !running.isReady()
                && !"IDLE".equals(running.getPhase()) && !"FAILED".equals(running.getPhase())) {
            return running;
        }
        TestPrepStatusDto initial = TestPrepStatusDto.builder()
                .scenario(scenario)
                .phase("STARTED")
                .percent(0)
                .ready(false)
                .message("Hazırlık başlatıldı")
                .build();
        current.set(initial);
        runAsync(scenario);
        return initial;
    }

    @Override
    public TestPrepStatusDto status() {
        return current.get();
    }

    @Async
    public void runAsync(String scenario) {

        String key = scenario == null ? "" : scenario.trim().toLowerCase();
        try {
            switch (key) {
                case "baseline" -> prepareBaseline();
                case "cold"     -> prepareCold();
                case "warm"     -> prepareWarm();
                case "mixed"    -> prepareMixed();
                default -> {
                    fail("Bilinmeyen senaryo: " + scenario);
                    return;
                }
            }
            ready();
        } catch (Exception ex) {
            log.error("Prep failed", ex);
            fail(ex.getMessage());
        }
    }


    private void prepareBaseline() {
        progress("WARMUP_DB", 10);
        cacheToggleService.toggle(false);
        cacheToggleService.clearAll();
        warmupDatabase();
        progress("WARMUP_DB", 90);
    }


    private void prepareCold() {
        progress("WARMUP_DB", 10);
        cacheToggleService.toggle(true);
        cacheToggleService.clearAll();
        warmupDatabase();
        progress("WARMUP_DB", 90);
    }


    private void prepareWarm() {
        cacheToggleService.toggle(true);
        cacheToggleService.clearAll();
        progress("WARMUP_DB", 20);
        warmupDatabase();
        progress("WARMUP_CACHE", 40);
        warmupListAndSearchCaches();
        
        List<Long> ids = productRepository.findAllIds();
        warmupCache(ids, 50, 95);
    }


    private void prepareMixed() {
        cacheToggleService.toggle(true);
        cacheToggleService.clearAll();
        progress("WARMUP_DB", 20);
        warmupDatabase();
        progress("WARMUP_CACHE", 40);
        warmupListAndSearchCaches();

        List<Long> all = productRepository.findAllIds();
        Collections.shuffle(all, new Random(42));
        List<Long> popular = all.subList(0, Math.min(MIXED_POPULAR_COUNT, all.size()));
        warmupCache(popular, 50, 95);
    }

    private void warmupListAndSearchCaches() {

        long cCount = Math.max(1, categoryRepository.count());
        for (long cid = 1; cid <= cCount; cid++) {
            productService.findByCategory(cid, PageRequest.of(0, 20));
        }
        for (String term : SEARCH_TERMS) {
            productService.search(term);
        }
    }



    private void warmupDatabase() {
        long pCount = productRepository.count();
        if (pCount == 0) return;
        long cCount = Math.max(1, categoryRepository.count());
        Random r = new Random(7);


        brandRepository.findAll();
        categoryRepository.findAll();


        for (int i = 0; i < WARMUP_BY_ID_QUERIES; i++) {
            long id = 1 + (long) r.nextInt((int) Math.min(pCount, Integer.MAX_VALUE));
            productRepository.findWithRelationsById(id);
        }


        for (int i = 0; i < WARMUP_BY_CATEGORY_QUERIES; i++) {
            long cid = 1 + (long) r.nextInt((int) cCount);
            productRepository.findByCategoryId(cid, PageRequest.of(r.nextInt(10), 20));
        }


        for (int i = 0; i < WARMUP_SEARCH_QUERIES; i++) {
            productRepository.search(SEARCH_TERMS[r.nextInt(SEARCH_TERMS.length)], PageRequest.of(0, 20));
        }
    }


    private void warmupCache(List<Long> ids, int fromPercent, int toPercent) {
        int total = ids.size();
        if (total == 0) return;
        int reportEvery = Math.max(1, total / 50);
        for (int i = 0; i < total; i++) {
            try {
                productService.getById(ids.get(i));
            } catch (Exception ignored) {

            }
            if (i % reportEvery == 0) {
                int p = fromPercent + (int) ((toPercent - fromPercent) * ((double) i / total));
                progress("WARMUP_CACHE", p);
            }
        }
    }

    private void progress(String phase, int percent) {
        TestPrepStatusDto cur = current.get();
        current.set(TestPrepStatusDto.builder()
                .scenario(cur.getScenario())
                .phase(phase)
                .percent(percent)
                .ready(false)
                .message(null)
                .build());
    }

    private void ready() {
        TestPrepStatusDto cur = current.get();
        current.set(TestPrepStatusDto.builder()
                .scenario(cur.getScenario())
                .phase("READY")
                .percent(100)
                .ready(true)
                .message("Senaryo hazır")
                .build());
    }

    private void fail(String msg) {
        TestPrepStatusDto cur = current.get();
        current.set(TestPrepStatusDto.builder()
                .scenario(cur != null ? cur.getScenario() : null)
                .phase("FAILED")
                .percent(0)
                .ready(false)
                .message(msg)
                .build());
    }

    private static TestPrepStatusDto idle() {
        return TestPrepStatusDto.builder()
                .scenario(null)
                .phase("IDLE")
                .percent(0)
                .ready(false)
                .message(null)
                .build();
    }
}
