package com.emirhan.redis.cache.benchmark.repository;

import com.emirhan.redis.cache.benchmark.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {

}
