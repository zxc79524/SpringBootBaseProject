CREATE TABLE `account` (
	`uid` CHAR(36) NOT NULL COLLATE 'utf8mb3_general_ci',
	`account` VARCHAR(20) NOT NULL COLLATE 'utf8mb3_general_ci',
	`create_datetime` TIMESTAMP NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
	`passowrd` CHAR(64) NOT NULL COLLATE 'utf8mb3_general_ci',
	PRIMARY KEY (`uid`) USING BTREE
)
COLLATE='utf8mb3_general_ci'
ENGINE=InnoDB
;
CREATE TABLE `role` (
	`role_id` CHAR(32) NOT NULL COMMENT '角色ID' COLLATE 'utf8mb3_general_ci',
	`role_name` CHAR(32) NOT NULL COMMENT '角色名稱' COLLATE 'utf8mb3_general_ci',
	PRIMARY KEY (`role_id`) USING BTREE
)
COMMENT='角色'
COLLATE='utf8mb3_general_ci'
ENGINE=InnoDB
;
CREATE TABLE `account_role` (
	`uid` CHAR(36) NOT NULL DEFAULT '' COLLATE 'utf8mb3_general_ci',
	`role_id` CHAR(32) NOT NULL COLLATE 'utf8mb3_general_ci',
	PRIMARY KEY (`uid`) USING BTREE,
	INDEX `fk_account_role_by_role` (`role_id`) USING BTREE,
	CONSTRAINT `fk_account_role_by_account` FOREIGN KEY (`uid`) REFERENCES `account` (`uid`) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT `fk_account_role_by_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`) ON UPDATE CASCADE ON DELETE CASCADE
)
COLLATE='utf8mb3_general_ci'
ENGINE=InnoDB
;
CREATE TABLE `permission` (
	`permission_id` CHAR(8) NOT NULL COLLATE 'utf8mb3_general_ci',
	`api_url` VARCHAR(255) NOT NULL COLLATE 'utf8mb3_general_ci',
	`http_method` VARCHAR(5) NOT NULL COLLATE 'utf8mb3_general_ci',
	PRIMARY KEY (`permission_id`) USING BTREE
)
COLLATE='utf8mb3_general_ci'
ENGINE=InnoDB
;
CREATE TABLE `refresh_token` (
	`token_id` CHAR(32) NOT NULL COLLATE 'utf8mb3_general_ci',
	`create_datetime` TIMESTAMP NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
	`expired_datetime` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
	`uid` CHAR(36) NOT NULL COLLATE 'utf8mb3_general_ci',
	PRIMARY KEY (`token_id`) USING BTREE,
	INDEX `fk_refresh_token` (`uid`) USING BTREE,
	CONSTRAINT `fk_refresh_token` FOREIGN KEY (`uid`) REFERENCES `account` (`uid`) ON UPDATE CASCADE ON DELETE CASCADE
)
COLLATE='utf8mb3_general_ci'
ENGINE=InnoDB
;

CREATE TABLE `token` (
	`token_id` CHAR(32) NOT NULL COLLATE 'utf8mb3_general_ci',
	`create_datetime` TIMESTAMP NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
	`expired_datetime` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00',
	`uid` CHAR(36) NOT NULL COLLATE 'utf8mb3_general_ci',
	PRIMARY KEY (`token_id`) USING BTREE,
	INDEX `fk_token` (`uid`) USING BTREE,
	CONSTRAINT `fk_token` FOREIGN KEY (`uid`) REFERENCES `account` (`uid`) ON UPDATE CASCADE ON DELETE CASCADE
)
COLLATE='utf8mb3_general_ci'
ENGINE=InnoDB
;
CREATE TABLE `role_permission` (
	`permission_id` CHAR(8) NOT NULL COLLATE 'utf8mb3_general_ci',
	`role_id` CHAR(32) NOT NULL COLLATE 'utf8mb3_general_ci',
	`is_allow` BIT(1) NOT NULL COMMENT '是否允許',
	PRIMARY KEY (`permission_id`, `role_id`) USING BTREE,
	INDEX `fk_role_permission_by_role` (`role_id`) USING BTREE,
	CONSTRAINT `fk_role_permission_by_permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`) ON UPDATE CASCADE ON DELETE CASCADE,
	CONSTRAINT `fk_role_permission_by_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`) ON UPDATE CASCADE ON DELETE CASCADE
)
COMMENT='角色和權限對應'
COLLATE='utf8mb3_general_ci'
ENGINE=InnoDB
;
