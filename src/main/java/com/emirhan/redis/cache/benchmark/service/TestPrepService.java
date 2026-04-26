package com.emirhan.redis.cache.benchmark.service;

import com.emirhan.redis.cache.benchmark.dto.TestPrepStatusDto;

public interface TestPrepService {
    TestPrepStatusDto prepare(String scenario);

    TestPrepStatusDto status();
}
