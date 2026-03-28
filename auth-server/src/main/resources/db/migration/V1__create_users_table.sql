CREATE SCHEMA IF NOT EXISTS auth_schema;

CREATE TABLE IF NOT EXISTS auth_schema.users (
     id BIGSERIAL PRIMARY KEY,
     username VARCHAR(50) NOT NULL UNIQUE,
     email VARCHAR(100) NOT NULL UNIQUE,
     password VARCHAR(255) NOT NULL,
     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Password hashed using BCrypt with a cost factor of 12
INSERT INTO auth_schema.users (username, email, password)
VALUES ('admin', 'admin@gmail.com', '$2a$12$7PmkNcI.xz5reIifDDHIdeNLupeVnHegOHXCFlkyUIlxbRdsamolu');