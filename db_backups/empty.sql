-- --------------------------------------------------------
-- Host:                         localhost
-- Server version:               5.7.13-log - MySQL Community Server (GPL)
-- Server OS:                    Win64
-- HeidiSQL Version:             9.3.0.4984
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8mb4 */;
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
  PRIMARY KEY (`id`),
  KEY `fk_player1` (`player1`),
  KEY `fk_player2` (`player2`),
  KEY `fk_winner` (`winner`),
  KEY `fk_divison` (`division`),
  CONSTRAINT `fk_divison` FOREIGN KEY (`division`) REFERENCES `division` (`name`),
  CONSTRAINT `fk_player1` FOREIGN KEY (`player1`) REFERENCES `player` (`name`),
  CONSTRAINT `fk_player2` FOREIGN KEY (`player2`) REFERENCES `player` (`name`),
  CONSTRAINT `fk_winner` FOREIGN KEY (`winner`) REFERENCES `player` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table scl.bout: ~0 rows (approximately)
DELETE FROM `bout`;
/*!40000 ALTER TABLE `bout` DISABLE KEYS */;
/*!40000 ALTER TABLE `bout` ENABLE KEYS */;


-- Dumping structure for table scl.division
CREATE TABLE IF NOT EXISTS `division` (
  `name` varchar(32) NOT NULL,
  `precedence` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table scl.division: ~0 rows (approximately)
DELETE FROM `division`;
/*!40000 ALTER TABLE `division` DISABLE KEYS */;
/*!40000 ALTER TABLE `division` ENABLE KEYS */;


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
  PRIMARY KEY (`id`),
  KEY `fk_match` (`bout`),
  KEY `fk_spy` (`spy`),
  KEY `fk_sniper` (`sniper`),
  CONSTRAINT `fk_match` FOREIGN KEY (`bout`) REFERENCES `bout` (`id`),
  CONSTRAINT `fk_sniper` FOREIGN KEY (`sniper`) REFERENCES `player` (`name`),
  CONSTRAINT `fk_spy` FOREIGN KEY (`spy`) REFERENCES `player` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table scl.game: ~0 rows (approximately)
DELETE FROM `game`;
/*!40000 ALTER TABLE `game` DISABLE KEYS */;
/*!40000 ALTER TABLE `game` ENABLE KEYS */;


-- Dumping structure for table scl.player
CREATE TABLE IF NOT EXISTS `player` (
  `name` varchar(50) NOT NULL,
  `division` varchar(50) NOT NULL,
  `wins` int(11) NOT NULL DEFAULT '0',
  `draws` int(11) NOT NULL DEFAULT '0',
  `losses` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`name`),
  KEY `fk_league` (`division`),
  CONSTRAINT `fk_league` FOREIGN KEY (`division`) REFERENCES `division` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table scl.player: ~0 rows (approximately)
DELETE FROM `player`;
/*!40000 ALTER TABLE `player` DISABLE KEYS */;
/*!40000 ALTER TABLE `player` ENABLE KEYS */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
