package com.emirhan.redis.cache.benchmark.dto;

import com.emirhan.redis.cache.benchmark.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto implements Serializable {

    private Long id;
    private String name;
    private String description;
    private String sku;
    private BigDecimal price;
    private Integer stock;
    private LocalDateTime createdAt;
    private CategoryDto category;
    private BrandDto brand;

    public static ProductDto from(Product product) {
        if (product == null) return null;
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .sku(product.getSku())
                .price(product.getPrice())
                .stock(product.getStock())
                .createdAt(product.getCreatedAt())
                .category(CategoryDto.from(product.getCategory()))
                .brand(BrandDto.from(product.getBrand()))
                .build();
    }
}
