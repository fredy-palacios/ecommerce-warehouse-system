-- SAMPLE DATA
INSERT INTO users (username, password, email, full_name, role)
VALUES ('admin', 'admin123', 'admin@warehouse.com', 'Administrator', 'MANAGER');

INSERT INTO categories (name, description, active)
VALUES ('Home', 'Home household items', 1);

INSERT INTO products (sku, name, description, price, stock, reserved_stock, min_stock, location, status, category_id)
VALUES ('H-A-001', 'Lodge 26', 'Cast iron skillet', 50.00, 10, 5, 2, 'A-1-3', 'AVAILABLE', 1);

COMMIT;