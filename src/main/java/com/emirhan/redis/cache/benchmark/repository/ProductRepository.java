package com.emirhan.redis.cache.benchmark.repository;

import com.emirhan.redis.cache.benchmark.entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE product, category, brand, test_run RESTART IDENTITY CASCADE", nativeQuery = true)
    void truncateAllData();


    @EntityGraph(attributePaths = {"brand", "category"})
    Optional<Product> findWithRelationsById(Long id);


    @EntityGraph(attributePaths = {"brand", "category"})
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);


    @EntityGraph(attributePaths = {"brand", "category"})
    @Query("select p from Product p")
    Page<Product> findAllWithRelations(Pageable pageable);


    @Query("""
            select p from Product p
              join fetch p.brand b
              join fetch p.category c
            where lower(p.name) like lower(concat('%', :q, '%'))
               or lower(b.name) like lower(concat('%', :q, '%'))
            """)
    List<Product> search(@Param("q") String q, Pageable pageable);


    @Query("select p.id from Product p")
    List<Long> findAllIds();
}
