DROP SCHEMA IF EXISTS test CASCADE;
CREATE SCHEMA test;
SET search_path TO test;

CREATE TABLE user_credentials (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    auth_uuid VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    first_access_token VARCHAR(255) UNIQUE,
    is_active BOOL DEFAULT TRUE,
    role CHAR(12)
);

CREATE TABLE school (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    identifier CHAR(20) UNIQUE NOT NULL,
    is_active BOOL DEFAULT TRUE
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
    id BIGSERIAL PRIMARY KEY,
    filename VARCHAR(100),
    bucket_key VARCHAR(100),
    file_md5 VARCHAR(40)
);

CREATE TABLE activity(
    id BIGSERIAL PRIMARY KEY,
    name CHAR(50) NOT NULL,
    description VARCHAR(240) NOT NULL,
    punctuation FLOAT NOT NULL,
    is_visible BOOL NOT NULL,
    is_active BOOL NOT NULL,
    max_delivery_date TIMESTAMP NOT NULL,
    teacher_id BIGINT NOT NULL,
    attachment_id BIGINT,
    FOREIGN KEY(teacher_id) REFERENCES teacher(teacher_id),
    FOREIGN KEY(attachment_id) REFERENCES attachments(id)
);

CREATE TABLE student(
    student_id BIGINT PRIMARY KEY,
    registry CHAR(10) NOT NULL,
    school_id BIGINT,
    FOREIGN KEY(school_id) REFERENCES school(id),
    FOREIGN KEY(student_id) REFERENCES user_credentials(id)
);

CREATE TABLE activity_delivery(
    id BIGSERIAL PRIMARY KEY,
    delivery_timestamp TIMESTAMP NOT NULL,
    grade_received FLOAT,
    student_delivery_id BIGINT NOT NULL,
    activity_id BIGINT NOT NULL,
    attachment_id BIGINT NOT NULL,
    FOREIGN KEY(student_delivery_id) REFERENCES student(student_id),
    FOREIGN KEY(activity_id) REFERENCES activity(id),
    FOREIGN KEY(attachment_id) REFERENCES attachments(id)
);

CREATE TABLE class(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50),
    school_id BIGINT,
    FOREIGN KEY(school_id) REFERENCES school(id),
    is_active BOOL DEFAULT TRUE,
    is_done BOOL DEFAULT TRUE
);

CREATE TABLE achievement(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(240) NOT NULL,
    icon_name VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    before_at TIMESTAMP,
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

CREATE TABLE student_class_membership(
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT,
    class_id BIGINT,
    FOREIGN KEY(student_id) REFERENCES student(student_id),
    FOREIGN KEY(class_id) REFERENCES class(id),
    is_active BOOL DEFAULT TRUE
);

CREATE TABLE teacher_class_membership(
    id BIGSERIAL PRIMARY KEY,
    teacher_id BIGINT,
    class_id BIGINT,
    FOREIGN KEY(teacher_id) REFERENCES teacher(teacher_id),
    FOREIGN KEY(class_id) REFERENCES class(id),
    is_active BOOL DEFAULT TRUE
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

CREATE TABLE tier_config(
    class_id BIGINT NOT NULL,
    gold_percent FLOAT NOT NULL,
    silver_percent FLOAT NOT NULL,
    bronze_percent FLOAT NOT NULL,
    PRIMARY KEY(class_id),
    FOREIGN KEY(class_id) REFERENCES class(id)
);