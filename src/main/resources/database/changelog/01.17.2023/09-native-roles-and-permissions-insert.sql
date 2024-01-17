--liquibase formatted sql

--changeset bnedza:9
INSERT INTO role (name) VALUES
("ROLE_USER"), ("ROLE_MANAGER"), ("ROLE_ADMIN"), ("ROLE_SYSADMIN");

INSERT INTO permission (name) VALUES
("READ:USER"),
("READ:CUSTOMER"),
("CREATE:USER"),
("CREATE:CUSTOMER"),
("UPDATE:USER"),
("UPDATE:CUSTOMER"),
("DELETE:USER"),
("DELETE:CUSTOMER");

insert into role_permission (role_id, permission_id) values
(1,1), (1,2), #user
(2,1), (2,2), (2,5), (2,6), #manager
(3,1), (3,2), (3,3), (3,4), (3,5), (3,6), #admin
(4,1), (4,2), (4,3), (4,4), (4,5), (4,6), (4,7), (4,8); #sysadmin
