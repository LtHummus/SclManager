-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.7.13-log - MySQL Community Server (GPL)
-- Server OS:                    Win64
-- HeidiSQL Version:             9.4.0.5125
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Dumping structure for table scl.bout
CREATE TABLE IF NOT EXISTS `bout` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `week` int(11) NOT NULL DEFAULT '0',
  `division` varchar(50) NOT NULL DEFAULT '0',
  `player1` varchar(50) NOT NULL DEFAULT '0',
  `player2` varchar(50) NOT NULL DEFAULT '0',
  `status` int(11) NOT NULL DEFAULT '0',
  `winner` varchar(50) DEFAULT '0',
  `match_url` text,
  `draft` int(11) DEFAULT NULL,
  `bout_type` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_player1` (`player1`),
  KEY `fk_player2` (`player2`),
  KEY `fk_winner` (`winner`),
  KEY `fk_divison` (`division`),
  KEY `fk_draft` (`draft`),
  CONSTRAINT `fk_divison` FOREIGN KEY (`division`) REFERENCES `division` (`name`),
  CONSTRAINT `fk_draft` FOREIGN KEY (`draft`) REFERENCES `draft` (`id`),
  CONSTRAINT `fk_player1` FOREIGN KEY (`player1`) REFERENCES `player` (`name`),
  CONSTRAINT `fk_player2` FOREIGN KEY (`player2`) REFERENCES `player` (`name`),
  CONSTRAINT `fk_winner` FOREIGN KEY (`winner`) REFERENCES `player` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=379 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table scl.division
CREATE TABLE IF NOT EXISTS `division` (
  `name` varchar(32) NOT NULL,
  `precedence` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table scl.draft
CREATE TABLE IF NOT EXISTS `draft` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `room_code` text NOT NULL,
  `player1` text NOT NULL,
  `player2` text NOT NULL,
  `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `payload` mediumtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table scl.game
CREATE TABLE IF NOT EXISTS `game` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `spy` varchar(50) NOT NULL DEFAULT '0',
  `sniper` varchar(50) NOT NULL DEFAULT '0',
  `bout` int(11) NOT NULL DEFAULT '0',
  `result` int(11) NOT NULL DEFAULT '0',
  `sequence` int(11) NOT NULL DEFAULT '0',
  `venue` text NOT NULL,
  `gametype` text NOT NULL,
  `uuid` varchar(50) NOT NULL,
  `timestamp` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_uuid` (`uuid`),
  KEY `fk_match` (`bout`),
  KEY `fk_spy` (`spy`),
  KEY `fk_sniper` (`sniper`),
  CONSTRAINT `fk_match` FOREIGN KEY (`bout`) REFERENCES `bout` (`id`),
  CONSTRAINT `fk_sniper` FOREIGN KEY (`sniper`) REFERENCES `player` (`name`),
  CONSTRAINT `fk_spy` FOREIGN KEY (`spy`) REFERENCES `player` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=498 DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
-- Dumping structure for table scl.player
CREATE TABLE IF NOT EXISTS `player` (
  `name` varchar(50) NOT NULL,
  `division` varchar(50) NOT NULL,
  `wins` int(11) NOT NULL DEFAULT '0',
  `draws` int(11) NOT NULL DEFAULT '0',
  `losses` int(11) NOT NULL DEFAULT '0',
  `country` char(2) NOT NULL DEFAULT 'sp',
  PRIMARY KEY (`name`),
  KEY `fk_league` (`division`),
  CONSTRAINT `fk_league` FOREIGN KEY (`division`) REFERENCES `division` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
