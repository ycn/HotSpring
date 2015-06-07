INSERT INTO customer(first_name, last_name, age, created_at, updated_at) VALUES('Andy', 'Yuan', 36, UNIX_TIMESTAMP(), UNIX_TIMESTAMP());
INSERT INTO customer(first_name, last_name, age, created_at, updated_at) VALUES('Chaos', 'Yuan', 18, UNIX_TIMESTAMP(), UNIX_TIMESTAMP());
INSERT INTO address(customer_id, addr, phone, created_at, updated_at) VALUES(1, 'ABC. st NO.123', '123456', UNIX_TIMESTAMP(), UNIX_TIMESTAMP());
INSERT INTO address(customer_id, addr, phone, created_at, updated_at) VALUES(1, 'ABC.', '01022222222', UNIX_TIMESTAMP(), UNIX_TIMESTAMP());