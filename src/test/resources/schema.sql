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
    role ENUM('ADMIN','MANAGER','STUDENT')
);

CREATE TABLE school (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    identifier CHAR(20) UNIQUE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE manager (
    manager_id BIGINT PRIMARY KEY,
    school_id BIGINT,
    FOREIGN KEY(manager_id) REFERENCES user_credentials(id),
    FOREIGN KEY(school_id) REFERENCES school(id)
);

CREATE TABLE activity(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name CHAR(50) NOT NULL,
    description VARCHAR(240) NOT NULL,
    is_deliverable BOOLEAN,
    expire_date DATETIME NOT NULL,
    max_value DOUBLE NOT NULL
);

CREATE TABLE student(
    student_id BIGINT PRIMARY KEY,
    FOREIGN KEY(student_id) REFERENCES user_credentials(id)
);

CREATE TABLE activity_delivery(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    delivery_timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    grade_received DOUBLE,
    student_delivery_id BIGINT NOT NULL,
    activity_id BIGINT NOT NULL,
    FOREIGN KEY(student_delivery_id) REFERENCES student(student_id),
    FOREIGN KEY(activity_id) REFERENCES activity(id)
);

CREATE TABLE achievement(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(240) NOT NULL,
    before_at DATETIME,
    earlier_of INT,
    best_of INT,
    average_grade_greater_or_equals_than FLOAT,
    class_id BIGINT,
    activity_id BIGINT,
    FOREIGN KEY(activity_id) REFERENCES activity(id)
);