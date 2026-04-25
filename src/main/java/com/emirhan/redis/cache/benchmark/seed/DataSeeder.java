package com.emirhan.redis.cache.benchmark.seed;

import com.emirhan.redis.cache.benchmark.entity.Brand;
import com.emirhan.redis.cache.benchmark.entity.Category;
import com.emirhan.redis.cache.benchmark.entity.Product;
import com.emirhan.redis.cache.benchmark.repository.BrandRepository;
import com.emirhan.redis.cache.benchmark.repository.CategoryRepository;
import com.emirhan.redis.cache.benchmark.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Value("${app.seed.enabled:true}")
    private boolean enabled;

    @Value("${app.seed.products:200000}")
    private int productCount;

    @Value("${app.seed.categories:50}")
    private int categoryCount;

    @Value("${app.seed.brands:200}")
    private int brandCount;

    @Value("${app.seed.batch-size:1000}")
    private int batchSize;

    @Override
    public void run(String... args) {
        if (!enabled) {
            log.info("Seed disabled (app.seed.enabled=false)");
            return;
        }
        if (productRepository.count() >= productCount) {
            log.info("Seed skipped — product count already >= {}", productCount);
            return;
        }

        long t0 = System.currentTimeMillis();
        Faker faker = new Faker(new Locale("en"));
        Random random = new Random(42);

        List<Brand> brands = seedBrands(faker);
        List<Category> categories = seedCategories(faker);
        seedProducts(faker, random, brands, categories);

        long elapsed = System.currentTimeMillis() - t0;
        log.info("Seed complete: {} brands, {} categories, {} products in {} ms",
                brands.size(), categories.size(), productCount, elapsed);
    }

    protected List<Brand> seedBrands(Faker faker) {
        if (brandRepository.count() >= brandCount) {
            return brandRepository.findAll();
        }
        List<Brand> result = new ArrayList<>(brandCount);
        for (int i = 0; i < brandCount; i++) {
            Brand b = new Brand();
            b.setName(faker.company().name() + " " + i);
            b.setCountry(faker.country().name());
            result.add(b);
        }
        return brandRepository.saveAll(result);
    }

    protected List<Category> seedCategories(Faker faker) {
        if (categoryRepository.count() >= categoryCount) {
            return categoryRepository.findAll();
        }
        int rootCount = Math.max(1, categoryCount / 5);
        List<Category> roots = new ArrayList<>();
        for (int i = 0; i < rootCount; i++) {
            Category c = new Category();
            c.setName(faker.commerce().department() + " " + i);
            roots.add(c);
        }
        List<Category> savedRoots = categoryRepository.saveAll(roots);

        List<Category> children = new ArrayList<>();
        for (int i = rootCount; i < categoryCount; i++) {
            Category c = new Category();
            c.setName(faker.commerce().department() + " sub-" + i);
            c.setParent(savedRoots.get(i % savedRoots.size()));
            children.add(c);
        }
        categoryRepository.saveAll(children);

        List<Category> all = new ArrayList<>(savedRoots);
        all.addAll(children);
        return all;
    }

    protected void seedProducts(Faker faker, Random random, List<Brand> brands, List<Category> categories) {
        long existing = productRepository.count();
        int remaining = (int) Math.max(0, productCount - existing);
        log.info("Seeding {} products (existing: {})", remaining, existing);

        List<Product> batch = new ArrayList<>(batchSize);
        long batchT0 = System.currentTimeMillis();

        for (int i = 0; i < remaining; i++) {
            Product p = new Product();
            p.setName(faker.commerce().productName());
            p.setDescription(faker.lorem().paragraph(2));
            p.setSku(faker.code().ean13());
            p.setPrice(BigDecimal.valueOf(10 + random.nextDouble() * 1990)
                    .setScale(2, RoundingMode.HALF_UP));
            p.setStock(random.nextInt(1001));
            p.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(365)));
            p.setBrand(brands.get(random.nextInt(brands.size())));
            p.setCategory(categories.get(random.nextInt(categories.size())));
            batch.add(p);

            if (batch.size() >= batchSize) {
                productRepository.saveAll(batch);
                long elapsed = System.currentTimeMillis() - batchT0;
                log.info("Inserted {} / {} products ({} ms / batch)", i + 1, remaining, elapsed);
                batch.clear();
                batchT0 = System.currentTimeMillis();
            }
        }
        if (!batch.isEmpty()) {
            productRepository.saveAll(batch);
        }
    }
}
