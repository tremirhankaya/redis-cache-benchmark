package com.emirhan.redis.cache.benchmark.dto;

import com.emirhan.redis.cache.benchmark.entity.Brand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandDto implements Serializable {

    private Long id;
    private String name;
    private String country;

    public static BrandDto from(Brand brand) {
        if (brand == null) return null;
        return BrandDto.builder()
                .id(brand.getId())
                .name(brand.getName())
                .country(brand.getCountry())
                .build();
    }
}
