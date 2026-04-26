package com.emirhan.redis.cache.benchmark.service.ımpl;

import com.emirhan.redis.cache.benchmark.dto.ProductDto;
import com.emirhan.redis.cache.benchmark.entity.Product;
import com.emirhan.redis.cache.benchmark.filter.CacheHitTracker;
import com.emirhan.redis.cache.benchmark.repository.ProductRepository;
import com.emirhan.redis.cache.benchmark.service.CacheToggleService;
import com.emirhan.redis.cache.benchmark.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.emirhan.redis.cache.benchmark.dto.PageResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Tüm cache mantığı burada — controller @Cacheable bilmiyor, servisi çağırıyor.
 * condition = "@cacheToggleServiceImpl.isEnabled()" runtime toggle:
 *   kapalıyken Redis'e bakmadan DB'ye gidiyor.
 *
 * HIT/MISS tespiti: @Cacheable method body çalışırsa → MISS (method body çağrıldı demek).
 *   Çalışmazsa Spring doğrudan cache'ten dönmüş demek → HIT.
 *   CacheHitTracker.markMiss() sadece method BODY'de çağrılıyor.
 *   TimingFilter, method bittiğinde status null ise VE cache enabled ise → HIT olarak kabul eder.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final int SEARCH_LIMIT = 50;

    private final ProductRepository productRepository;
    private final CacheToggleService cacheToggleService;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "products",
            key = "#id",
            condition = "@cacheToggleServiceImpl.isEnabled()"
    )
    public ProductDto getById(Long id) {
        trackMissOrDisabled();
        log.debug("DB hit: product id={}", id);
        Product product = productRepository.findWithRelationsById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
        return ProductDto.from(product);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "productList",
            key = "T(java.util.Objects).hash(#categoryId, #pageable.pageNumber, #pageable.pageSize, #pageable.sort.toString())",
            condition = "@cacheToggleServiceImpl.isEnabled()"
    )
    public PageResponse<ProductDto> findByCategory(Long categoryId, Pageable pageable) {
        trackMissOrDisabled();
        log.debug("DB hit: list category={} page={}", categoryId, pageable.getPageNumber());
        Page<Product> page = (categoryId == null)
                ? productRepository.findAllWithRelations(pageable)
                : productRepository.findByCategoryId(categoryId, pageable);
        return PageResponse.from(page.map(ProductDto::from));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "productSearch",
            key = "#q.toLowerCase()",
            condition = "@cacheToggleServiceImpl.isEnabled() and #q != null and #q.length() >= 2"
    )
    public List<ProductDto> search(String q) {
        trackMissOrDisabled();
        log.debug("DB hit: search q={}", q);
        if (q == null || q.isBlank()) {
            return List.of();
        }
        List<Product> result = productRepository.search(q.trim(), PageRequest.of(0, SEARCH_LIMIT));
        return result.stream().map(ProductDto::from).toList();
    }

    /**
     * Bu method SADECE @Cacheable'ın method body'sine girildiğinde çalışıyor.
     * Cache HIT olursa Spring proxy method body'yi atlar → bu çağrılmaz → status null kalır.
     * TimingFilter null gördüğünde HIT olarak işaretler.
     */
    private void trackMissOrDisabled() {
        if (!cacheToggleService.isEnabled()) {
            CacheHitTracker.markDisabled();
        } else {
            CacheHitTracker.markMiss();
        }
    }
}
