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
public class CacheStatusDto implements Serializable {

    private boolean enabled;
    private long keyCount;
    private String cacheManager;
}
