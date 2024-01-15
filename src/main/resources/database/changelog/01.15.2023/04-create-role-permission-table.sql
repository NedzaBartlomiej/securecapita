--liquibase formatted sql

--changeset bnedza:4
DROP TABLE IF EXISTS role_permission;

CREATE TABLE role_permission
(
    id            BIGINT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT,
    role_id       INT UNSIGNED                NOT NULL,
    permission_id INT UNSIGNED                NOT NULL,
    FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permission (id) ON DELETE RESTRICT ON UPDATE CASCADE
);