package com.emirhan.redis.cache.benchmark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComparisonDto implements Serializable {

    private List<TestRunDto> runs;
    private List<String> labels;
    private List<Double> avgMsSeries;
    private List<Double> p95Series;
    private List<Double> p99Series;
    private List<Double> throughputSeries;
    private List<Double> errorRateSeries;
}
