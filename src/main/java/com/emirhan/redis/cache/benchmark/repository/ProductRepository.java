package com.emirhan.redis.cache.benchmark.repository;

import com.emirhan.redis.cache.benchmark.entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE product, category, brand RESTART IDENTITY CASCADE", nativeQuery = true)
    void truncateAllData();
}
