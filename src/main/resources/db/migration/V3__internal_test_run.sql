CREATE TABLE internal_test_run (
    id BIGSERIAL PRIMARY KEY,
    scenario VARCHAR(32) NOT NULL,
    endpoint VARCHAR(64) NOT NULL,
    request_count INTEGER NOT NULL,
    test_seed INTEGER NOT NULL,
    avg_ms DOUBLE PRECISION,
    min_ms DOUBLE PRECISION,
    max_ms DOUBLE PRECISION,
    median_ms DOUBLE PRECISION,
    p95_ms DOUBLE PRECISION,
    hits INTEGER,
    miss INTEGER,
    disabled INTEGER,
    hit_ratio VARCHAR(10),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
