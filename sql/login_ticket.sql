DROP TABLE IF EXISTS `login_ticket`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
SET character_set_client = utf8mb4;
CREATE TABLE `login_ticket`
(
    `id`      INT(11)     NOT NULL AUTO_INCREMENT,
    `user_id` INT(11)     NOT NULL,
    `ticket`  VARCHAR(45) NOT NULL,
    `status`  INT(11) DEFAULT '0' COMMENT '0-有效; 1-无效;',
    `expired` TIMESTAMP   NOT NULL,
    PRIMARY KEY (`id`),
    KEY `index_ticket` (`ticket`(20))
) ENGINE = INNODB
  DEFAULT CHARSET = utf8;