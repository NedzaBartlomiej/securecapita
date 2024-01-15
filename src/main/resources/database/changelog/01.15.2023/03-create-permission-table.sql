--liquibase formatted sql

--changeset bnedza:3
DROP TABLE IF EXISTS permission;

CREATE TABLE permission
(
    id   INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(50)              NOT NULL,
    CONSTRAINT UQ_PERMISSION_NAME UNIQUE (name)
);