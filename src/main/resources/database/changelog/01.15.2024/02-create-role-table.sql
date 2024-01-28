--liquibase formatted sql

--changeset bnedza:2
CREATE TABLE role
(
    id   INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(30)              NOT NULL,
    CONSTRAINT UQ_ROLE_NAME UNIQUE (name)
);