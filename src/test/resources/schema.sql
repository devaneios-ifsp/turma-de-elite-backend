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
    role ENUM('ADMIN','MANAGER','STUDENT','TEACHER')
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

CREATE TABLE teacher (
    teacher_id BIGINT PRIMARY KEY,
    school_id BIGINT,
    FOREIGN KEY(teacher_id) REFERENCES user_credentials(id),
    FOREIGN KEY(school_id) REFERENCES school(id)
);

CREATE TABLE attachments(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    filename VARCHAR(100),
    bucket_key VARCHAR(100),
    file_md5 VARCHAR(40)
);

CREATE TABLE activity(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name CHAR(50) NOT NULL,
    description VARCHAR(240) NOT NULL,
    punctuation DOUBLE NOT NULL,
    is_visible BOOLEAN NOT NULL,
    is_active BOOLEAN NOT NULL,
    is_deliverable BOOLEAN NOT NULL,
    max_delivery_date DATETIME NOT NULL,
    teacher_id BIGINT NOT NULL,
    attachment_id BIGINT,
    FOREIGN KEY(teacher_id) REFERENCES teacher(teacher_id),
    FOREIGN KEY(attachment_id) REFERENCES attachments(id)
);

CREATE TABLE student(
    student_id BIGINT PRIMARY KEY,
    registry TEXT(10) NOT NULL,
    school_id BIGINT,
    FOREIGN KEY(school_id) REFERENCES school(id),
    FOREIGN KEY(student_id) REFERENCES user_credentials(id)
);

CREATE TABLE activity_delivery(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    delivery_timestamp DATETIME NOT NULL,
    grade_received DOUBLE,
    student_delivery_id BIGINT NOT NULL,
    activity_id BIGINT NOT NULL,
    attachment_id BIGINT NOT NULL,
    FOREIGN KEY(student_delivery_id) REFERENCES student(student_id),
    FOREIGN KEY(activity_id) REFERENCES activity(id),
    FOREIGN KEY(attachment_id) REFERENCES attachments(id)
);

CREATE TABLE achievement(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(240) NOT NULL,
    icon_name VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    before_at DATETIME,
    earlier_of INT,
    best_of INT,
    average_grade_greater_or_equals_than FLOAT,
    class_id BIGINT,
    activity_id BIGINT,
    teacher_id BIGINT,
    FOREIGN KEY(activity_id) REFERENCES activity(id),
    FOREIGN KEY (teacher_id) REFERENCES teacher(teacher_id),
    FOREIGN KEY (class_id) REFERENCES class(id)
);

CREATE TABLE class(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    school_id BIGINT,
    FOREIGN KEY(school_id) REFERENCES school(id),
    is_active BOOLEAN DEFAULT TRUE,
    is_done BOOLEAN DEFAULT TRUE
);

CREATE TABLE student_class_membership(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT,
    class_id BIGINT,
    FOREIGN KEY(student_id) REFERENCES student(student_id),
    FOREIGN KEY(class_id) REFERENCES class(id),
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE teacher_class_membership(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    teacher_id BIGINT,
    class_id BIGINT,
    FOREIGN KEY(teacher_id) REFERENCES teacher(teacher_id),
    FOREIGN KEY(class_id) REFERENCES class(id),
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE class_activities(
    class_id BIGINT NOT NULL,
    activity_id BIGINT NOT NULL,
    FOREIGN KEY(class_id) REFERENCES class(id),
    FOREIGN KEY (activity_ID) REFERENCES activity(id),
    PRIMARY KEY (class_id,activity_id)
);

CREATE TABLE student_achievements(
    student_id BIGINT NOT NULL,
    achievement_id BIGINT NOT NULL,
    FOREIGN KEY(student_id) REFERENCES student(student_id),
    FOREIGN KEY(achievement_id) REFERENCES achievement(id),
    PRIMARY KEY(student_id,achievement_id)
);