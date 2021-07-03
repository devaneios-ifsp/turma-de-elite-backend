DROP DATABASE turma_de_elite_test;
CREATE DATABASE turma_de_elite_test;
USE turma_de_elite_test;

CREATE TABLE user_credentials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    auth_uuid VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    first_access_token VARCHAR(255) UNIQUE,
    is_active BOOLEAN DEFAULT TRUE,
    role ENUM('ADMIN','MANAGER')
);

CREATE TABLE school (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    identifier CHAR(20) UNIQUE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE manager (
    manager_id BIGINT,
    school_id BIGINT,
    FOREIGN KEY(manager_id) REFERENCES user_credentials(id)
);