package com.emirhan.redis.cache.benchmark.repository;

import com.emirhan.redis.cache.benchmark.entity.InternalTestRun;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternalTestRunRepository extends JpaRepository<InternalTestRun, Long> {
}
