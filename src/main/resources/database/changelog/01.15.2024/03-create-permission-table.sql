--liquibase formatted sql

--changeset bnedza:3
CREATE TABLE permission
(
    id   INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT UQ_PERMISSION_NAME UNIQUE (name)
);