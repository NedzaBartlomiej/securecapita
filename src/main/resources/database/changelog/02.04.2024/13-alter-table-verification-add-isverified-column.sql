--liquibase formatted sql

--changeset bnedza:12
ALTER TABLE verification ADD COLUMN is_verified BOOLEAN DEFAULT FALSE;