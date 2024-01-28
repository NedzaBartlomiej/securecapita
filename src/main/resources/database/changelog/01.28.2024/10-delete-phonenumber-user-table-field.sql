--liquibase formatted sql

--changeset bnedza:10
ALTER TABLE user DROP COLUMN phone_number;