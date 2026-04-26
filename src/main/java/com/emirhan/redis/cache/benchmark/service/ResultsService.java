package com.emirhan.redis.cache.benchmark.service;

import com.emirhan.redis.cache.benchmark.dto.ComparisonDto;
import com.emirhan.redis.cache.benchmark.dto.TestRunDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResultsService {
    TestRunDto upload(MultipartFile file, String name, String scenario, int load);

    List<TestRunDto> findAll();

    ComparisonDto compare(List<Long> runIds);
}
