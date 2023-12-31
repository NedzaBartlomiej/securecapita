/*
                GENERAL RULES:

    - use underscore_names
    - singular table names
    - id fields like this: user_id, not: id
    - foreign key names should be same as equivalents
*/
SET NAMES 'UTF8MB4';


CREATE SCHEMA IF NOT EXISTS securecapita;

USE securecapita;

#ROLE =================================================================

DROP TABLE IF EXISTS role;

CREATE TABLE role
(
    id   INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(30)              NOT NULL,
    CONSTRAINT UQ_ROLE_NAME UNIQUE (name)
);

DROP TABLE IF EXISTS permission;

CREATE TABLE permission
(
    id   INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(50)              NOT NULL,
    CONSTRAINT UQ_PERMISSION_NAME UNIQUE (name)
);

DROP TABLE IF EXISTS role_permission;

CREATE TABLE role_permission
(
    id            BIGINT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT,
    role_id       INT UNSIGNED                NOT NULL,
    permission_id INT UNSIGNED                NOT NULL,
    FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permission (id) ON DELETE RESTRICT ON UPDATE CASCADE
);

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

#EVENT =================================================================

DROP TABLE IF EXISTS event;

CREATE TABLE event
(
    id          INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT,
    type        VARCHAR(50)              NOT NULL CHECK (type IN ('LOGIN_ATTEMPT', 'LOGIN_ATTEMPT_FAILURE',
                                                                  'LOGIN_ATTEMPT_SUCCESS', 'PROFILE_UPDATE',
                                                                  'PROFILE_PICTURE_UPDATE', 'ROLE_UPDATE',
                                                                  'ACCOUNT_SETTINGS_UPDATE', 'PASSWORD_UPDATE',
                                                                  'MFA_UPDATE')),
    description VARCHAR(255)             NOT NULL,
    CONSTRAINT UQ_EVENT_TYPE UNIQUE (type)
);

DROP TABLE IF EXISTS user_event;

CREATE TABLE user_event
(
    id         BIGINT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user_id    BIGINT UNSIGNED             NOT NULL,
    event_id   INT UNSIGNED                NOT NULL,
    device     VARCHAR(100) DEFAULT NULL,
    ip_address VARCHAR(45)  DEFAULT NULL,
    event_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (event_id) REFERENCES event (id) ON DELETE RESTRICT ON UPDATE CASCADE
);

#VERIFICATION =================================================================

DROP TABLE IF EXISTS verification;

CREATE TABLE verification
(
    id                      BIGINT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user_id                 BIGINT UNSIGNED             NOT NULL,
    verification_type       VARCHAR(100)                NOT NULL CHECK (verification_type IN ('EMAIL_VERIFICATION',
                                                                                              'RESET_PASSWORD_VERIFICATION',
                                                                                              'MFA_VERIFICATION')),
    verification_identifier VARCHAR(255)                NOT NULL,
    expiration_date         DATETIME DEFAULT NULL,
    FOREIGN KEY (user_id) REFERENCES User (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT UQ_ACCOUNTVERIFICATION_VERIFICATIONIDENTIFIER UNIQUE (verification_identifier)
);