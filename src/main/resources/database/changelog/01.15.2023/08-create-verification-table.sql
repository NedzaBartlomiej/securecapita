--liquibase formatted sql

--changeset bnedza:8
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