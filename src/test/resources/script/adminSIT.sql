-- UTENTE ADMIN PER TESTING
-- username: admin
-- password: password
INSERT INTO user_registered
(dtype, username, email, password, firstname, lastname, role, gender, city, address, telephone_number,
 tax_identification_number, failed_login_attempts, locked_until)
VALUES ('UserRegistered', 'admin', 'admin@test.it', '$2a$10$wPHxwfsfTnOJAdgYcerBt.utdAvC24B/DWfuXfzKBSDHO0etB1ica',
        'Admin', 'Root', 'ADMIN', 'OTHER', 'Verona',
        'Via Roma 2',
        '3333333334', 'ABCDEF00A01A000B', 0, NULL);


INSERT INTO device (dtype, name, status, shipment_id)
VALUES ('Device', 'Test GPS-001', 0, NULL);
