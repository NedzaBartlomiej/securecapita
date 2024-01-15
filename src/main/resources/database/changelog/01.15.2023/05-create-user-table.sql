--liquibase formatted sql

--changeset bnedza:5
DROP TABLE IF EXISTS user;

CREATE TABLE user
(
    id            BIGINT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT,
    role_id       INT UNSIGNED                NOT NULL,
    first_name    VARCHAR(50)                 NOT NULL,
    last_name     VARCHAR(50)                 NOT NULL,
    email         VARCHAR(100)                NOT NULL,
    password      VARCHAR(255)                NOT NULL,
    address       VARCHAR(255) DEFAULT NULL,
    phone_number  VARCHAR(30)  DEFAULT NULL,
    job_title     VARCHAR(50)  DEFAULT NULL,
    bio           VARCHAR(255) DEFAULT NULL,
    image_url     VARCHAR(255) DEFAULT 'https://i.stack.imgur.com/l60Hf.png',
    created_at    DATETIME     DEFAULT CURRENT_TIMESTAMP,
    is_enabled    BOOLEAN      DEFAULT FALSE,
    is_not_locked BOOLEAN      DEFAULT TRUE,
    using_mfa     BOOLEAN      DEFAULT FALSE,
    FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);