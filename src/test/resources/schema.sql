DROP DATABASE turma_de_elite_test;
CREATE DATABASE turma_de_elite_test;
USE turma_de_elite_test;
CREATE TABLE user_credentials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    auth_uuid VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    first_access_token VARCHAR(255) UNIQUE,
    role ENUM('ADMIN')
);