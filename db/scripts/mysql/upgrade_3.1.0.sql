SET NAMES utf8;
USE sphinx-console;

CREATE TABLE IF NOT EXISTS `SNIPPET_CONFIGURATION` (
  `snippet_configuration_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `collection_id` bigint(20) DEFAULT NULL,
  `scheduled_task_id` bigint(20) DEFAULT NULL,
  `datasource_id` bigint(20) DEFAULT NULL,
  `last_buildsnippet` timestamp NULL DEFAULT NULL,
  `next_buildsnippet` timestamp NULL DEFAULT NULL,
  `pre_query` text,
  `post_query` text,
  `main_query` text,
  `full_pre_query` text,
  `full_post_query` text,
  `full_main_query` text,
  PRIMARY KEY (`snippet_configuration_id`),
  KEY `collection_id` (`collection_id`),
  KEY `datasource_id` (`datasource_id`),
  KEY `scheduled_task_id` (`scheduled_task_id`),
 CONSTRAINT `FK_SNIPPET_CONFIGURATION_COLLECTION` FOREIGN KEY (`collection_id`) REFERENCES `COLLECTION` (`collection_id`),
 CONSTRAINT `FK_SNIPPET_CONFIGURATION_SCHEDULE` FOREIGN KEY (`scheduled_task_id`) REFERENCES `SCHEDULED_TASK` (`scheduled_task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `SNIPPET_CONFIGURATION_FIELD` (
  `snippet_configuration_field_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `snippet_configuration_id` bigint(20) DEFAULT NULL,
  `field_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`snippet_configuration_field_id`),
  KEY `snippet_configuration_id` (`snippet_configuration_id`),
  CONSTRAINT `FK_SNIPPET_CONFIGURATION_FIELD_CONFIGURATION` FOREIGN KEY (`snippet_configuration_id`) REFERENCES `SNIPPET_CONFIGURATION` (`snippet_configuration_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

ALTER TABLE SPHINX_PROCESS MODIFY COLUMN generated_sphinx_conf LONGBLOB;
