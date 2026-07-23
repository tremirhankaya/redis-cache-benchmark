# Performance Improvement in Spring Boot Applications Using Redis Caching

## Overview

This project evaluates the performance impact of integrating **Redis** as a caching layer into a **Spring Boot** application backed by **PostgreSQL**. The goal is to measure and analyze how response times and latency change under different cache states (Disabled, Cold Cache, and Warm Cache) during concurrent load testing.

## Technical Stack & Environment

| Component | Technology |
| :--- | :--- |
| Backend Framework | Spring Boot 3.x, Java 21 |
| Database | PostgreSQL 16 (Docker) |
| Cache Layer | Redis 7 (Docker) |
| Load Testing | Apache JMeter 5.6.3 |
| Data Generation | Java Faker (200,000 synthetic records) |

## System Architecture & Flow

```text
                    +-------------------+
                    |   HTTP Request    |
                    +---------+---------+
                              |
                              v
                    +-------------------+
                    | Controller Layer  |
                    +---------+---------+
                              |
                              v
                    +-------------------+
                    |   Service Layer   |
                    +---------+---------+
                              |
                    +---------+---------+
                    | Is Caching Active?|
                    +----+---------+----+
                      NO |         | YES
                         |         v
                         |  +---------------+
                         |  |  Redis Cache  |
                         |  +---+-------+---+
                         |  HIT |       | MISS
                         |      |       v
                         |      |  +------------------+
                         |      |  | Repository Layer |
                         |      |  +--------+---------+
                         |      |           |
                         v      v           v
                    +---------------+ +---------------+
                    |  PostgreSQL   | | Store in Redis|
                    +-------+-------+ +-------+-------+
                            |                 |
                            +--------+--------+
                                     |
                                     v
                           +-------------------+
                           |  Return Response  |
                           +-------------------+
```

Each HTTP response includes a custom header `X-Cache-Status` (`HIT` or `MISS`) to indicate whether data was retrieved from Redis or PostgreSQL.

## Test Scenarios

All tests were executed against the `GET /api/products/{id}` endpoint with **100 concurrent users** and a total of **10,000 requests** per run.

1. **Baseline Scenario (Cache Disabled)**  
   Caching logic is bypassed completely. Every request hits PostgreSQL directly.

2. **Cold Cache Scenario (Cache Enabled, Empty Redis)**  
   Redis starts empty. Cache misses fetch data from PostgreSQL and write the result back to Redis.

3. **Warm Cache Scenario (Cache Enabled, Full Redis)**  
   Redis is pre-populated. All requests are served from cache, bypassing PostgreSQL.

## Test Results & Metrics

All metrics were recorded via Apache JMeter and processed from raw CSV output.

| Metric | Baseline | Cold Cache | Warm Cache |
| :--- | :---: | :---: | :---: |
| Total Requests | 10,000 | 10,000 | 10,000 |
| Avg Response Time | **1.21 ms** | **1.42 ms** | **0.71 ms** |
| Max Response Time | 51 ms | 19 ms | 14 ms |
| Performance Difference | Base Reference | **-17% (Slower)** | **+41% (Faster)** |

## Key Takeaways

- **Cold Cache Overhead:** An empty cache introduces a slight penalty on average response time (+17%) because the application performs a two-step operation (PostgreSQL read + Redis write). Even so, it caps worst-case spikes (max latency reduced from 51 ms to 19 ms) as the cache warms up.

- **Warm Cache Benefit:** When data resides in RAM, responses are served at sub-millisecond speeds, yielding a **41% improvement** in overall response latency compared to pure database reads.

- **Applicability:** Redis caching provides the most value for read-heavy API workloads where identical data is queried frequently.
