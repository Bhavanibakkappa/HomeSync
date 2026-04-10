-- HomeSync seed data
-- Default admin password is: admin123  (BCrypt hash below)
-- Default resident password is: user123

INSERT IGNORE INTO users (username, password, email, full_name, role, active)
VALUES (
    'admin',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'admin@homesync.com',
    'System Admin',
    'ADMIN',
    true
);

INSERT IGNORE INTO users (username, password, email, full_name, role, active)
VALUES (
    'resident1',
    '$2a$10$Nq.eFdqfIh4OFPGo4bVxiuSfPsHm6zQvHGIZoO1fJ.6pBDuXBxlEe',
    'resident@homesync.com',
    'John Resident',
    'RESIDENT',
    true
);
