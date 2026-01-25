-- UTENTE USER PER TESTING
-- username: pippo
-- password: password
INSERT INTO user_registered
(dtype, username, email, password, firstname, lastname, role, gender, city, address, telephone_number,
 tax_identification_number, failed_login_attempts, locked_until)
VALUES ('UserRegistered', 'pippo', 'test@test.it', '$2a$10$wPHxwfsfTnOJAdgYcerBt.utdAvC24B/DWfuXfzKBSDHO0etB1ica', 'Pippo', 'Pippi', 'USER', 'OTHER', 'Verona',
        'Via del cervo 1',
        '3333333333', 'ABCDEF00A01A000A', 0, NULL);

-- UTENTE ADMIN PER TESTING
-- username: admin
-- password: password
INSERT INTO user_registered
(dtype, username, email, password, firstname, lastname, role, gender, city, address, telephone_number,
 tax_identification_number, failed_login_attempts, locked_until)
VALUES ('UserRegistered', 'admin', 'admin@test.it', '$2a$10$wPHxwfsfTnOJAdgYcerBt.utdAvC24B/DWfuXfzKBSDHO0etB1ica', 'Admin', 'Root', 'ADMIN', 'OTHER', 'Verona',
        'Via Roma 2',
        '3333333334', 'ABCDEF00A01A000B', 0, NULL);