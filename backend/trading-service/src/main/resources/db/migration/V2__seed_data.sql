INSERT INTO stocks (symbol, name) VALUES
                                      ('AAPL', 'Apple Inc.'),
                                      ('TSLA', 'Tesla Inc.'),
                                      ('MSFT', 'Microsoft Corporation');

INSERT INTO orders (
    user_id,
    symbol,
    original_qty,
    filled_qty,
    price,
    order_type,
    status,
    created_at
) VALUES
('user_1001', 'AAPL', 100, 50, 185.50, 'BUY',  'PARTIALLY_FILLED', NOW() - INTERVAL '10 minutes'),
('user_2001', 'AAPL', 50,  50, 185.75, 'SELL', 'FILLED',           NOW() - INTERVAL '9 minutes'),
('user_1003', 'TSLA', 20,  10, 250.00, 'BUY',  'PARTIALLY_FILLED', NOW() - INTERVAL '6 minutes'),
('user_2003', 'TSLA', 10,  10, 251.20, 'SELL', 'FILLED',           NOW() - INTERVAL '5 minutes'),
('user_1005', 'MSFT', 100, 100, 372.50, 'BUY',  'FILLED', NOW() - INTERVAL '12 minutes'),
('user_2005', 'MSFT', 100, 100, 372.50, 'SELL', 'FILLED', NOW() - INTERVAL '12 minutes');

INSERT INTO trades (
    symbol,
    quantity,
    price,
    buyer_id,
    seller_id,
    executed_at
) VALUES
('AAPL', 50, 185.75, 'user_1001', 'user_2001', NOW() - INTERVAL '5 minutes'),
('AAPL', 30, 186.10, 'user_1002', 'user_2002', NOW() - INTERVAL '3 minutes'),
('TSLA', 10, 251.20, 'user_1003', 'user_2003', NOW() - INTERVAL '2 minutes'),
('TSLA', 5, 252.00, 'user_1004', 'user_2004', NOW() - INTERVAL '1 minutes'),
('MSFT', 100, 372.50, 'user_1005', 'user_2005', NOW() - INTERVAL '10 minutes');