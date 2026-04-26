package com.emirhan.redis.cache.benchmark.repository;

import com.emirhan.redis.cache.benchmark.entity.TestRun;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRunRepository extends JpaRepository<TestRun, Long> {
}
