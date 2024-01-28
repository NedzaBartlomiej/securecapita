--liquibase formatted sql

--changeset bnedza:11
CREATE TABLE phone
(
    id            INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT,
    country_prefix VARCHAR(5)  DEFAULT NULL,
    phone_number  VARCHAR(50) DEFAULT NULL,
    CONSTRAINT UQ_PHONE_PHONENUMBER UNIQUE (phone_number)
);