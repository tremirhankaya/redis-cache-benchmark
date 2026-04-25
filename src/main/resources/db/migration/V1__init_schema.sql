

CREATE TABLE brand (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(120) NOT NULL,
    country VARCHAR(80)
);

CREATE TABLE category (
    id        BIGSERIAL PRIMARY KEY,
    name      VARCHAR(120) NOT NULL,
    parent_id BIGINT,
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES category (id) ON DELETE SET NULL
);

CREATE TABLE product (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(200)   NOT NULL,
    description TEXT,
    sku         VARCHAR(32),
    price       NUMERIC(10, 2),
    stock       INTEGER,
    created_at  TIMESTAMP WITHOUT TIME ZONE,
    category_id BIGINT,
    brand_id    BIGINT,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE SET NULL,
    CONSTRAINT fk_product_brand    FOREIGN KEY (brand_id)    REFERENCES brand (id)    ON DELETE SET NULL
);

CREATE INDEX idx_product_category ON product (category_id);
CREATE INDEX idx_product_brand    ON product (brand_id);
CREATE INDEX idx_product_name     ON product (name);
CREATE INDEX idx_category_parent  ON category (parent_id);
