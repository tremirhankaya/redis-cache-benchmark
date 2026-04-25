    package com.emirhan.redis.cache.benchmark.service.ımpl;

    import com.emirhan.redis.cache.benchmark.repository.ProductRepository;
    import com.emirhan.redis.cache.benchmark.seed.DataSeeder;
    import com.emirhan.redis.cache.benchmark.service.AdminService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;

    @Service
    @RequiredArgsConstructor
    public class AdminServiceImpl implements AdminService {
        private final ProductRepository productRepository;
        @Override
        public void cleanAllDatabases() {
            productRepository.truncateAllData();
        }
        public void seedData() {
        }
    }
