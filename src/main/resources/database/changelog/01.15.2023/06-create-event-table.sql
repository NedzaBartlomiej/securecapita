--liquibase formatted sql

--changeset bnedza:6
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