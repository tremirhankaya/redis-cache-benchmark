package com.emirhan.redis.cache.benchmark.controller;

import com.emirhan.redis.cache.benchmark.dto.ProductDto;
import com.emirhan.redis.cache.benchmark.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.emirhan.redis.cache.benchmark.dto.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable Long id) {
        log.info("Product detail request id={}", id);
        return ResponseEntity.ok(productService.getById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductDto>> list(
            @RequestParam(required = false) Long category,
            Pageable pageable) {
        log.info("Product list request category={} page={} size={}",
                category, pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(productService.findByCategory(category, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> search(@RequestParam String q) {
        log.info("Product search request q={}", q);
        return ResponseEntity.ok(productService.search(q));
    }
}
