-- JMeter koşularının parse edilmiş metriklerini saklayan tablo.
CREATE TABLE test_run (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(120) NOT NULL,
    scenario        VARCHAR(32)  NOT NULL,
    load_users      INTEGER      NOT NULL,
    total_requests  BIGINT,
    avg_ms          DOUBLE PRECISION,
    min_ms          DOUBLE PRECISION,
    max_ms          DOUBLE PRECISION,
    p50             DOUBLE PRECISION,
    p90             DOUBLE PRECISION,
    p95             DOUBLE PRECISION,
    p99             DOUBLE PRECISION,
    throughput      DOUBLE PRECISION,
    error_rate      DOUBLE PRECISION,
    stddev          DOUBLE PRECISION,
    cache_hit_ratio DOUBLE PRECISION,
    uploaded_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE INDEX idx_test_run_scenario ON test_run (scenario);
CREATE INDEX idx_test_run_uploaded ON test_run (uploaded_at);
