CREATE TABLE `all_types` (
  `COLUMN_0` bigint NOT NULL,
  `COLUMN_1` binary(1) default NULL,
  `COLUMN_2` boolean default NULL,
  `COLUMN_3` blob,
  `COLUMN_4` bool default NULL,
  `COLUMN_5` char default NULL,
  `COLUMN_6` date default NULL,
  `COLUMN_8` double(20,15) default NULL,
  `COLUMN_9` double default NULL,
  `COLUMN_10` float(55,5) default NULL,
  `COLUMN_11` int default NULL,
  `COLUMN_12` longblob,
  `COLUMN_13` longtext,
  `COLUMN_14` mediumblob,
  `COLUMN_15` mediumint default NULL,
  `COLUMN_16` mediumtext,
  `COLUMN_17` char(1) default NULL,
  `COLUMN_18` smallint default NULL,
  `COLUMN_19` text,
  `COLUMN_20` time default NULL,
  `COLUMN_21` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `COLUMN_22` tinyblob,
  `COLUMN_23` tinyint(4) default NULL,
  `COLUMN_24` tinytext,
  `COLUMN_25` varbinary(20) default NULL,
  `COLUMN_26` varchar(65) default NULL,
  PRIMARY KEY  (`COLUMN_0`)
) ENGINE=InnoDB;

CREATE TABLE `tableA` (
  `id` int NOT NULL,
  `col01` varchar(20) default NULL,
  `COLUMN_2` int default NULL,
  `COLUMN_3` int default NULL,
  PRIMARY KEY  (`id`),
  KEY `indexname1` (`col01`)
) ENGINE=InnoDB;

CREATE TABLE `tableB` (
  `id` int NOT NULL,
  `col01` varchar(20) default NULL,
  `ref` int default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `indexname2` (`col01`),
  KEY `tableB_ibfk_1` (`ref`),
  CONSTRAINT `tableB_ibfk_1` FOREIGN KEY (`ref`) REFERENCES `tableA` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB;

CREATE TABLE `tableC` (
  `id` int(11) NOT NULL,
  `ref1` int(11) default NULL,
  `ref2` int(11) default NULL,
  PRIMARY KEY  (`id`),
  KEY `tableC_ibfk_1` (`ref1`),
  KEY `tableC_ibfk_2` (`ref2`),
  CONSTRAINT `tableC_ibfk_2` FOREIGN KEY (`ref2`) REFERENCES `tableB` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `tableC_ibfk_1` FOREIGN KEY (`ref1`) REFERENCES `tableA` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB;
