--liquibase formatted sql

--changeset bnedza:7
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