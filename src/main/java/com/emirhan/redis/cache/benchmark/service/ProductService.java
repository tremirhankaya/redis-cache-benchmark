package com.emirhan.redis.cache.benchmark.service;

import com.emirhan.redis.cache.benchmark.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.emirhan.redis.cache.benchmark.dto.PageResponse;

import java.util.List;

public interface ProductService {
    ProductDto getById(Long id);

    PageResponse<ProductDto> findByCategory(Long categoryId, Pageable pageable);

    List<ProductDto> search(String q);
}
