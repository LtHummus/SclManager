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

-- Dumping structure for table scl.game
CREATE TABLE IF NOT EXISTS `game` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `spy` int(11) NOT NULL DEFAULT '0',
  `sniper` int(11) NOT NULL DEFAULT '0',
  `match` int(11) NOT NULL DEFAULT '0',
  `result` int(11) NOT NULL DEFAULT '0',
  `sequence` int(11) NOT NULL DEFAULT '0',
  `level` text NOT NULL,
  `gameType` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_spy` (`spy`),
  KEY `fk_sniper` (`sniper`),
  KEY `fk_match` (`match`),
  CONSTRAINT `fk_match` FOREIGN KEY (`match`) REFERENCES `match` (`id`),
  CONSTRAINT `fk_sniper` FOREIGN KEY (`sniper`) REFERENCES `player` (`id`),
  CONSTRAINT `fk_spy` FOREIGN KEY (`spy`) REFERENCES `player` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=latin1;

-- Dumping data for table scl.game: ~7 rows (approximately)
DELETE FROM `game`;
/*!40000 ALTER TABLE `game` DISABLE KEYS */;
INSERT INTO `game` (`id`, `spy`, `sniper`, `match`, `result`, `sequence`, `level`, `gameType`) VALUES
	(8, 2, 1, 2, 2, 1, 'Panopticon', 'a5/7'),
	(9, 1, 2, 2, 2, 2, 'Panopticon', 'a5/7'),
	(10, 2, 1, 2, 2, 3, 'Balcony', 'a2/3'),
	(11, 1, 2, 2, 2, 4, 'Balcony', 'a2/3'),
	(12, 2, 1, 2, 2, 5, 'Veranda', 'a5/7'),
	(13, 1, 2, 2, 3, 6, 'Veranda', 'a5/7'),
	(14, 2, 1, 2, 2, 7, 'Crowded Pub', 'a4/7');
/*!40000 ALTER TABLE `game` ENABLE KEYS */;


-- Dumping structure for table scl.league
CREATE TABLE IF NOT EXISTS `league` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

-- Dumping data for table scl.league: ~4 rows (approximately)
DELETE FROM `league`;
/*!40000 ALTER TABLE `league` DISABLE KEYS */;
INSERT INTO `league` (`id`, `name`) VALUES
	(1, 'Diamond'),
	(2, 'Platinum'),
	(3, 'Gold'),
	(4, 'Silver');
/*!40000 ALTER TABLE `league` ENABLE KEYS */;


-- Dumping structure for table scl.match
CREATE TABLE IF NOT EXISTS `match` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `week` int(11) NOT NULL DEFAULT '0',
  `league` int(11) NOT NULL DEFAULT '0',
  `player1` int(11) NOT NULL DEFAULT '0',
  `player2` int(11) NOT NULL DEFAULT '0',
  `status` int(11) NOT NULL DEFAULT '0',
  `winner` int(11) DEFAULT NULL,
  `match_url` text,
  PRIMARY KEY (`id`),
  KEY `fk_player1` (`player1`),
  KEY `fk_player2` (`player2`),
  KEY `fk_league` (`league`),
  KEY `fk_winner` (`winner`),
  CONSTRAINT `fk_league` FOREIGN KEY (`league`) REFERENCES `league` (`id`),
  CONSTRAINT `fk_player1` FOREIGN KEY (`player1`) REFERENCES `player` (`id`),
  CONSTRAINT `fk_player2` FOREIGN KEY (`player2`) REFERENCES `player` (`id`),
  CONSTRAINT `fk_winner` FOREIGN KEY (`winner`) REFERENCES `player` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=latin1;

-- Dumping data for table scl.match: ~120 rows (approximately)
DELETE FROM `match`;
/*!40000 ALTER TABLE `match` DISABLE KEYS */;
INSERT INTO `match` (`id`, `week`, `league`, `player1`, `player2`, `status`, `winner`, `match_url`) VALUES
	(1, 1, 1, 6, 3, 0, NULL, NULL),
	(2, 1, 1, 2, 1, 1, 1, 'https://s3-us-west-2.amazonaws.com/scl-replays-season3/SpyPartyReplay-20160611-17-03-00-krazycaley-vs-magician1099-QFaOf6HZRFubm3LBicu27Q-v18.zip'),
	(3, 1, 1, 5, 4, 0, NULL, NULL),
	(4, 2, 1, 6, 2, 0, NULL, NULL),
	(5, 2, 1, 5, 3, 0, NULL, NULL),
	(6, 2, 1, 4, 1, 0, NULL, NULL),
	(7, 3, 1, 6, 5, 0, NULL, NULL),
	(8, 3, 1, 4, 2, 0, NULL, NULL),
	(9, 3, 1, 1, 3, 0, NULL, NULL),
	(10, 4, 1, 6, 4, 0, NULL, NULL),
	(11, 4, 1, 1, 5, 0, NULL, NULL),
	(12, 4, 1, 3, 2, 0, NULL, NULL),
	(13, 5, 1, 6, 1, 0, NULL, NULL),
	(14, 5, 1, 3, 4, 0, NULL, NULL),
	(15, 5, 1, 2, 5, 0, NULL, NULL),
	(16, 6, 1, 5, 2, 0, NULL, NULL),
	(17, 6, 1, 6, 4, 0, NULL, NULL),
	(18, 6, 1, 1, 3, 0, NULL, NULL),
	(19, 7, 1, 5, 6, 0, NULL, NULL),
	(20, 7, 1, 1, 2, 0, NULL, NULL),
	(21, 7, 1, 3, 4, 0, NULL, NULL),
	(22, 8, 1, 5, 1, 0, NULL, NULL),
	(23, 8, 1, 3, 6, 0, NULL, NULL),
	(24, 8, 1, 4, 2, 0, NULL, NULL),
	(25, 9, 1, 5, 3, 0, NULL, NULL),
	(26, 9, 1, 4, 1, 0, NULL, NULL),
	(27, 9, 1, 2, 6, 0, NULL, NULL),
	(28, 10, 1, 5, 4, 0, NULL, NULL),
	(29, 10, 1, 2, 3, 0, NULL, NULL),
	(30, 10, 1, 6, 1, 0, NULL, NULL),
	(31, 1, 2, 8, 11, 0, NULL, NULL),
	(32, 1, 2, 9, 7, 0, NULL, NULL),
	(33, 1, 2, 12, 10, 0, NULL, NULL),
	(34, 2, 2, 8, 9, 0, NULL, NULL),
	(35, 2, 2, 12, 11, 0, NULL, NULL),
	(36, 2, 2, 10, 7, 0, NULL, NULL),
	(37, 3, 2, 8, 12, 0, NULL, NULL),
	(38, 3, 2, 10, 9, 0, NULL, NULL),
	(39, 3, 2, 7, 11, 0, NULL, NULL),
	(40, 4, 2, 8, 10, 0, NULL, NULL),
	(41, 4, 2, 7, 12, 0, NULL, NULL),
	(42, 4, 2, 11, 9, 0, NULL, NULL),
	(43, 5, 2, 8, 7, 0, NULL, NULL),
	(44, 5, 2, 11, 10, 0, NULL, NULL),
	(45, 5, 2, 9, 12, 0, NULL, NULL),
	(46, 6, 2, 10, 8, 0, NULL, NULL),
	(47, 6, 2, 12, 11, 0, NULL, NULL),
	(48, 6, 2, 9, 7, 0, NULL, NULL),
	(49, 7, 2, 10, 12, 0, NULL, NULL),
	(50, 7, 2, 9, 8, 0, NULL, NULL),
	(51, 7, 2, 7, 11, 0, NULL, NULL),
	(52, 8, 2, 10, 9, 0, NULL, NULL),
	(53, 8, 2, 7, 12, 0, NULL, NULL),
	(54, 8, 2, 11, 8, 0, NULL, NULL),
	(55, 9, 2, 10, 7, 0, NULL, NULL),
	(56, 9, 2, 11, 9, 0, NULL, NULL),
	(57, 9, 2, 8, 12, 0, NULL, NULL),
	(58, 10, 2, 10, 11, 0, NULL, NULL),
	(59, 10, 2, 8, 7, 0, NULL, NULL),
	(60, 10, 2, 12, 9, 0, NULL, NULL),
	(61, 1, 3, 15, 17, 0, NULL, NULL),
	(62, 1, 3, 13, 14, 0, NULL, NULL),
	(63, 1, 3, 18, 16, 0, NULL, NULL),
	(64, 2, 3, 15, 13, 0, NULL, NULL),
	(65, 2, 3, 18, 17, 0, NULL, NULL),
	(66, 2, 3, 16, 14, 0, NULL, NULL),
	(67, 3, 3, 15, 18, 0, NULL, NULL),
	(68, 3, 3, 16, 13, 0, NULL, NULL),
	(69, 3, 3, 14, 17, 0, NULL, NULL),
	(70, 4, 3, 15, 16, 0, NULL, NULL),
	(71, 4, 3, 14, 18, 0, NULL, NULL),
	(72, 4, 3, 17, 13, 0, NULL, NULL),
	(73, 5, 3, 15, 14, 0, NULL, NULL),
	(74, 5, 3, 17, 16, 0, NULL, NULL),
	(75, 5, 3, 13, 18, 0, NULL, NULL),
	(76, 6, 3, 14, 17, 0, NULL, NULL),
	(77, 6, 3, 13, 15, 0, NULL, NULL),
	(78, 6, 3, 16, 18, 0, NULL, NULL),
	(79, 7, 3, 14, 13, 0, NULL, NULL),
	(80, 7, 3, 16, 17, 0, NULL, NULL),
	(81, 7, 3, 18, 15, 0, NULL, NULL),
	(82, 8, 3, 14, 16, 0, NULL, NULL),
	(83, 8, 3, 18, 13, 0, NULL, NULL),
	(84, 8, 3, 15, 17, 0, NULL, NULL),
	(85, 9, 3, 14, 18, 0, NULL, NULL),
	(86, 9, 3, 15, 16, 0, NULL, NULL),
	(87, 9, 3, 17, 13, 0, NULL, NULL),
	(88, 10, 3, 14, 15, 0, NULL, NULL),
	(89, 10, 3, 17, 18, 0, NULL, NULL),
	(90, 10, 3, 13, 16, 0, NULL, NULL),
	(91, 1, 4, 22, 19, 0, NULL, NULL),
	(92, 1, 4, 23, 21, 0, NULL, NULL),
	(93, 1, 4, 20, 24, 0, NULL, NULL),
	(94, 2, 4, 22, 23, 0, NULL, NULL),
	(95, 2, 4, 20, 19, 0, NULL, NULL),
	(96, 2, 4, 24, 21, 0, NULL, NULL),
	(97, 3, 4, 22, 20, 0, NULL, NULL),
	(98, 3, 4, 24, 23, 0, NULL, NULL),
	(99, 3, 4, 21, 19, 0, NULL, NULL),
	(100, 4, 4, 22, 24, 0, NULL, NULL),
	(101, 4, 4, 21, 20, 0, NULL, NULL),
	(102, 4, 4, 19, 23, 0, NULL, NULL),
	(103, 5, 4, 22, 21, 0, NULL, NULL),
	(104, 5, 4, 19, 24, 0, NULL, NULL),
	(105, 5, 4, 23, 20, 0, NULL, NULL),
	(106, 6, 4, 22, 21, 0, NULL, NULL),
	(107, 6, 4, 20, 23, 0, NULL, NULL),
	(108, 6, 4, 19, 24, 0, NULL, NULL),
	(109, 7, 4, 22, 20, 0, NULL, NULL),
	(110, 7, 4, 19, 21, 0, NULL, NULL),
	(111, 7, 4, 24, 23, 0, NULL, NULL),
	(112, 8, 4, 22, 19, 0, NULL, NULL),
	(113, 8, 4, 24, 20, 0, NULL, NULL),
	(114, 8, 4, 23, 21, 0, NULL, NULL),
	(115, 9, 4, 22, 24, 0, NULL, NULL),
	(116, 9, 4, 23, 19, 0, NULL, NULL),
	(117, 9, 4, 21, 20, 0, NULL, NULL),
	(118, 10, 4, 22, 23, 0, NULL, NULL),
	(119, 10, 4, 21, 24, 0, NULL, NULL),
	(120, 10, 4, 20, 19, 0, NULL, NULL);
/*!40000 ALTER TABLE `match` ENABLE KEYS */;


-- Dumping structure for table scl.player
CREATE TABLE IF NOT EXISTS `player` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  `league` int(11) NOT NULL,
  `wins` int(11) NOT NULL DEFAULT '0',
  `draws` int(11) NOT NULL DEFAULT '0',
  `losses` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `league` (`league`),
  CONSTRAINT `league` FOREIGN KEY (`league`) REFERENCES `league` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=latin1;

-- Dumping data for table scl.player: ~24 rows (approximately)
DELETE FROM `player`;
/*!40000 ALTER TABLE `player` DISABLE KEYS */;
INSERT INTO `player` (`id`, `name`, `league`, `wins`, `draws`, `losses`) VALUES
	(1, 'magician1099', 1, 1, 0, 0),
	(2, 'krazycaley', 1, 0, 0, 1),
	(3, 'canadianbacon', 1, 0, 0, 0),
	(4, 'kcmmmmm', 1, 0, 0, 0),
	(5, 'bloom', 1, 0, 0, 0),
	(6, 'scientist', 1, 0, 0, 0),
	(7, 'cleetose', 2, 0, 0, 0),
	(8, 'slappydavis', 2, 0, 0, 0),
	(9, 'kikar', 2, 0, 0, 0),
	(10, 'sharper', 2, 0, 0, 0),
	(11, 'varanas', 2, 0, 0, 0),
	(12, 'royalflush', 2, 0, 0, 0),
	(13, 'warningtrack', 3, 0, 0, 0),
	(14, 'pires', 3, 0, 0, 0),
	(15, 'cameraman', 3, 0, 0, 0),
	(16, 'scallions', 3, 0, 0, 0),
	(17, 'james1221', 3, 0, 0, 0),
	(18, 'briguy', 3, 0, 0, 0),
	(19, 'fearfulferret', 4, 0, 0, 0),
	(20, 'lthummus', 4, 0, 0, 0),
	(21, 'elvisnake', 4, 0, 0, 0),
	(22, 'iceman', 4, 0, 0, 0),
	(23, 'sgnurf', 4, 0, 0, 0),
	(24, 'checker', 4, 0, 0, 0);
/*!40000 ALTER TABLE `player` ENABLE KEYS */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
