--liquibase formatted sql

--changeset bnedza:12
ALTER TABLE user
    ADD COLUMN phone_id INT UNSIGNED DEFAULT NULL,
    ADD FOREIGN KEY (phone_id) REFERENCES phone (id) ON DELETE CASCADE;