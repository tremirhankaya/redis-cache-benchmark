package com.emirhan.redis.cache.benchmark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestPrepStatusDto implements Serializable {

    private String scenario;
    private String phase;
    private int percent;
    private boolean ready;
    private String message;
}
