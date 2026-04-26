package com.emirhan.redis.cache.benchmark.service.ımpl;

import com.emirhan.redis.cache.benchmark.repository.ProductRepository;
import com.emirhan.redis.cache.benchmark.seed.DataSeeder;
import com.emirhan.redis.cache.benchmark.service.AdminService;
import com.emirhan.redis.cache.benchmark.service.CacheToggleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final ProductRepository productRepository;
    private final CacheToggleService cacheToggleService;
    private final DataSeeder dataSeeder;

    @Override
    public void cleanAllDatabases() {
        productRepository.truncateAllData();
        cacheToggleService.clearAll();
        log.info("DB truncate + cache flush tamamlandı");
    }

    @Override
    public void factoryReset(Integer productCount) {
        log.info("Factory reset başlıyor — DB + cache temizlenip {} ürünle seed edilecek",
                productCount != null ? productCount : "default");
        productRepository.truncateAllData();
        cacheToggleService.clearAll();
        dataSeeder.seed(productCount);
        log.info("Factory reset bitti");
    }
}
