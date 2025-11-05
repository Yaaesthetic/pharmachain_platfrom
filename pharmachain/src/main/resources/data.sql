-- ==================== ADMIN (Default from Vault) ====================
-- Note: Password is BCrypt encoded "SecurePass123!"
-- INSERT INTO users (id, user_type, username, password, code, is_active, created_at)
-- VALUES (1, 'ADMIN', 'admin', '$2a$10$rJZhN8Y3lQ7xF0X4gPzH0uK7X2qB5wM9nF3pQ8vR1tY6sZ4jH7uGe', '300001', true, '2025-01-15T10:00:00');

-- ==================== ADDITIONAL ADMINS ====================
INSERT INTO users (id, user_type, username, password, code, is_active, created_at)
VALUES
    (2, 'ADMIN', 'admin2', '$2a$10$rJZhN8Y3lQ7xF0X4gPzH0uK7X2qB5wM9nF3pQ8vR1tY6sZ4jH7uGe', '300002', true, '2025-02-20T14:30:00'),
    (3, 'ADMIN', 'admin3', '$2a$10$rJZhN8Y3lQ7xF0X4gPzH0uK7X2qB5wM9nF3pQ8vR1tY6sZ4jH7uGe', '300003', false, '2025-03-10T09:15:00');

-- ==================== MANAGERS ====================
INSERT INTO users (id, user_type, username, password, code, is_active, created_at, secteur_name, phone, address, assigned_admin_id)
VALUES
    (4, 'MANAGER', 'manager1', '$2a$10$rJZhN8Y3lQ7xF0X4gPzH0uK7X2qB5wM9nF3pQ8vR1tY6sZ4jH7uGe', '200001', true, '2025-01-20T11:00:00', 'Casablanca Centre', '+212600000001', 'Casa', 1),
    (5, 'MANAGER', 'manager2', '$2a$10$rJZhN8Y3lQ7xF0X4gPzH0uK7X2qB5wM9nF3pQ8vR1tY6sZ4jH7uGe', '200002', true, '2025-02-15T10:30:00', 'Rabat Nord', '+212600000002', 'Rabat', 1),
    (6, 'MANAGER', 'manager3', '$2a$10$rJZhN8Y3lQ7xF0X4gPzH0uK7X2qB5wM9nF3pQ8vR1tY6sZ4jH7uGe', '200003', true, '2025-03-01T12:00:00', 'Marrakech Sud', '+212600000003', 'Marrakech', 2);

-- ==================== DRIVERS ====================
INSERT INTO users (id, user_type, username, password, code, is_active, created_at, license_number, phone, assigned_manager_id)
VALUES
    (7, 'DRIVER', 'driver1', '$2a$10$rJZhN8Y3lQ7xF0X4gPzH0uK7X2qB5wM9nF3pQ8vR1tY6sZ4jH7uGe', '100001', true, '2025-01-25T08:00:00', 'LIC001', '+212611111111', 4),
    (8, 'DRIVER', 'driver2', '$2a$10$rJZhN8Y3lQ7xF0X4gPzH0uK7X2qB5wM9nF3pQ8vR1tY6sZ4jH7uGe', '100002', true, '2025-02-10T09:00:00', 'LIC002', '+212611111112', 4),
    (9, 'DRIVER', 'driver3', '$2a$10$rJZhN8Y3lQ7xF0X4gPzH0uK7X2qB5wM9nF3pQ8vR1tY6sZ4jH7uGe', '100003', true, '2025-02-20T10:00:00', 'LIC003', '+212611111113', 5),
    (10, 'DRIVER', 'driver4', '$2a$10$rJZhN8Y3lQ7xF0X4gPzH0uK7X2qB5wM9nF3pQ8vR1tY6sZ4jH7uGe', '100004', false, '2025-03-05T11:00:00', 'LIC004', '+212611111114', 6);

-- ==================== CLIENTS ====================
INSERT INTO client (client_code, name, address, phone, coordinates, secteur_id, auto_created)
VALUES
    ('400001', 'Pharmacie Al Amal', '123 Rue Mohammed V, Casablanca', '+212520000001', '33.5731,-7.5898', 4, false),
    ('400002', 'Pharmacie Centrale', '456 Avenue Hassan II, Rabat', '+212520000002', '34.0209,-6.8416', 5, false),
    ('400003', 'Pharmacie du Coin', '789 Boulevard Zerktouni, Casablanca', '+212520000003', '33.5883,-7.6114', 4, true),
    ('400004', 'Pharmacie Moderne', '321 Rue Oued Fes, Marrakech', '+212520000004', '31.6295,-7.9811', 6, false);

-- ==================== BORDEREAUX ====================
INSERT INTO bordereau (bordereau_number, delivery_date, current_driver_id, secteur_id, original_driver_id, status, scanned_at, completed_at, auto_created)
VALUES
    ('500001', '2025-10-15', 7, 4, 7, 'ASSIGNED', '2025-10-15T08:00:00', NULL, false),
    ('500002', '2025-10-15', 8, 4, 8, 'IN_TRANSIT', '2025-10-15T09:00:00', NULL, false),
    ('500003', '2025-10-14', 9, 5, 9, 'COMPLETED', '2025-10-14T08:00:00', '2025-10-14T18:00:00', false),
    ('500004', '2025-10-16', 10, 6, 10, 'CREATED', '2025-10-15T10:00:00', NULL, true);

-- ==================== DELIVERY ITEMS ====================
INSERT INTO delivery_item (id, bordereau_bordereau_number, bl_number, client_client_code, nombre_colis, nombre_sachets, status, delivered_at, delivery_notes, recipient_signature)
VALUES
    (1, '500001', '300001', '400001', 5, 10, 'PENDING', NULL, NULL, NULL),
    (2, '500001', '300002', '400003', 3, 7, 'PENDING', NULL, NULL, NULL),
    (3, '500002', '300003', '400001', 8, 15, 'PENDING', NULL, NULL, NULL),
    (4, '500003', '300004', '400002', 4, 8, 'DELIVERED', '2025-10-14T16:30:00', 'Delivered successfully', 'Signed by client'),
    (5, '500003', '300005', '400002', 6, 12, 'DELIVERED', '2025-10-14T17:00:00', 'Left at reception', 'Signed by receptionist');

-- ==================== BORDEREAU TRANSFERS ====================
INSERT INTO bordereau_transfer (id, bordereau_bordereau_number, from_driver_id, to_driver_id, transferred_at, transfer_barcode, reason, status)
VALUES
    (1, '500001', 7, 8, '2025-10-15T10:30:00', 'TRF001', 'Driver unavailable', 'ACCEPTED'),
    (2, '500002', 8, 7, '2025-10-15T11:00:00', 'TRF002', 'Route optimization', 'PENDING'),
    (3, '500004', 10, 9, '2025-10-15T12:00:00', 'TRF003', 'Emergency transfer', 'REJECTED');

-- ==================== RESET SEQUENCES (PostgreSQL) ====================
-- Use this if you're using PostgreSQL
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('delivery_item_id_seq', (SELECT MAX(id) FROM delivery_item));
SELECT setval('bordereau_transfer_id_seq', (SELECT MAX(id) FROM bordereau_transfer));

-- ==================== RESET AUTO_INCREMENT (MySQL/MariaDB) ====================
-- Use this if you're using MySQL/MariaDB
-- ALTER TABLE users AUTO_INCREMENT = 11;
-- ALTER TABLE delivery_item AUTO_INCREMENT = 6;
-- ALTER TABLE bordereau_transfer AUTO_INCREMENT = 4;