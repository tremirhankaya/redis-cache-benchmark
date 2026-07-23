# Performance Improvement in Spring Boot Applications Using Redis Caching
[![Paper PDF]([1783086351164.pdf](https://github.com/user-attachments/files/30304365/1783086351164.pdf))

## 1. Overview
This project evaluates the performance impact of integrating **Redis** as a caching layer into a **Spring Boot** application backed by **PostgreSQL**. The goal is to measure and analyze how response times and latency change under different cache states (Disabled, Cold Cache, and Warm Cache) during concurrent load testing.

---

## 2. Technical Stack & Environment
* **Backend Framework:** Spring Boot 3.x, Java 21[cite: 3]
* **Database:** PostgreSQL 16 (Running in Docker)[cite: 3]
* **Cache Layer:** Redis 7 (Running in Docker)[cite: 3]
* **Load Testing:** Apache JMeter 5.6.3[cite: 3]
* **Data Generation:** Java Faker (200,000 synthetic records)[cite: 3]

---

## 3. System Architecture & Flow
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


Each HTTP response contains a custom header `X-Cache-Status` (`HIT` or `MISS`) to indicate whether data was retrieved from Redis or PostgreSQL[cite: 3].

---

## 4. Test Scenarios

All tests were executed against the `GET /api/products/{id}` endpoint with **100 concurrent users** and a total of **10,000 requests** per run[cite: 3].

1. **Baseline Scenario (Cache Disabled):** Caching logic is bypassed completely. Every request hits PostgreSQL directly to fetch data from disk[cite: 3].
2. **Cold Cache Scenario (Cache Enabled, Empty Redis):** Redis starts completely empty. Incoming requests result in cache misses, fetching data from PostgreSQL and writing the result back to Redis[cite: 3].
3. **Warm Cache Scenario (Cache Enabled, Full Redis):** Redis is pre-populated. All requests hit the in-memory cache directly, bypassing PostgreSQL entirely[cite: 3].

---

## 5. Test Results & Metrics

All metrics were recorded via Apache JMeter and processed from raw CSV output[cite: 3].

| Metric | Baseline | Cold Cache | Warm Cache |
| :--- | :--- | :--- | :--- |
| **Total Requests** | 10,000[cite: 3] | 10,000[cite: 3] | 10,000[cite: 3] |
| **Avg Response Time** | **1.21 ms**[cite: 3] | **1.42 ms**[cite: 3] | **0.71 ms**[cite: 3] |
| **Max Response Time** | 51 ms[cite: 3] | 19 ms[cite: 3] | 14 ms[cite: 3] |
| **Performance Difference** | *Base Reference*[cite: 3] | **-17% (Slower)**[cite: 3] | **+41% (Faster)**[cite: 3] |

---

## 6. Key Takeaways

* **The Cold Cache Overhead:** An empty cache introduces a slight penalty on average response time (+17%) during the initial phase because the application executes a two-step write operation (PostgreSQL Read + Redis Write)[cite: 3]. However, even in a cold state, it caps the worst-case response spikes (Max latency reduced from 51 ms to 19 ms) as the cache warms up[cite: 3].
* **The Warm Cache Benefit:** When data resides in RAM, responses are served with sub-millisecond speeds, yielding a **41% improvement** in overall response latency compared to pure database reads[cite: 3].
* **Applicability:** Redis caching provides maximum value for read-heavy API workloads where identical data is queried frequently[cite: 3].
