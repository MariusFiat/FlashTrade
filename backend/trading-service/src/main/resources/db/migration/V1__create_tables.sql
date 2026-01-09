CREATE TABLE stocks (
    symbol VARCHAR(10) PRIMARY KEY,
    name TEXT NOT NULL ,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    symbol VARCHAR(10) NOT NULL,
    original_qty INTEGER NOT NULL,
    filled_qty INTEGER NOT NULL DEFAULT 0,
    price DECIMAL(10, 2) NOT NULL,
    status TEXT NOT NULL,
    side TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_orders_stock
        FOREIGN KEY (symbol)
        REFERENCES stocks (symbol)
);

CREATE TABLE trades (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(10) NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    buyer_id VARCHAR(255) NOT NULL,
    seller_id VARCHAR(255) NOT NULL,
    executed_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_trades_stock
        FOREIGN KEY (symbol)
        REFERENCES stocks (symbol)
);