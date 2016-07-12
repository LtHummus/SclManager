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
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8;

-- Dumping data for table scl.bout: ~120 rows (approximately)
DELETE FROM `bout`;
/*!40000 ALTER TABLE `bout` DISABLE KEYS */;
INSERT INTO `bout` (`id`, `week`, `division`, `player1`, `player2`, `status`, `winner`, `match_url`) VALUES
	(1, 1, 'Diamond', 'krazycaley', 'bloom', 0, NULL, NULL),
	(2, 1, 'Diamond', 'canadianbacon', 'kcmmmmm', 0, NULL, NULL),
	(3, 1, 'Diamond', 'scientist', 'magician1099', 0, NULL, NULL),
	(4, 2, 'Diamond', 'krazycaley', 'canadianbacon', 0, NULL, NULL),
	(5, 2, 'Diamond', 'scientist', 'bloom', 0, NULL, NULL),
	(6, 2, 'Diamond', 'magician1099', 'kcmmmmm', 0, NULL, NULL),
	(7, 3, 'Diamond', 'krazycaley', 'scientist', 0, NULL, NULL),
	(8, 3, 'Diamond', 'magician1099', 'canadianbacon', 0, NULL, NULL),
	(9, 3, 'Diamond', 'kcmmmmm', 'bloom', 0, NULL, NULL),
	(10, 4, 'Diamond', 'krazycaley', 'magician1099', 0, NULL, 'None'),
	(11, 4, 'Diamond', 'kcmmmmm', 'scientist', 0, NULL, NULL),
	(12, 4, 'Diamond', 'bloom', 'canadianbacon', 0, NULL, NULL),
	(13, 5, 'Diamond', 'krazycaley', 'kcmmmmm', 0, NULL, NULL),
	(14, 5, 'Diamond', 'bloom', 'magician1099', 0, NULL, NULL),
	(15, 5, 'Diamond', 'canadianbacon', 'scientist', 0, NULL, NULL),
	(16, 6, 'Diamond', 'scientist', 'krazycaley', 0, NULL, NULL),
	(17, 6, 'Diamond', 'magician1099', 'kcmmmmm', 0, NULL, NULL),
	(18, 6, 'Diamond', 'bloom', 'canadianbacon', 0, NULL, NULL),
	(19, 7, 'Diamond', 'scientist', 'magician1099', 0, NULL, NULL),
	(20, 7, 'Diamond', 'bloom', 'krazycaley', 0, NULL, NULL),
	(21, 7, 'Diamond', 'canadianbacon', 'kcmmmmm', 0, NULL, NULL),
	(22, 8, 'Diamond', 'scientist', 'bloom', 0, NULL, NULL),
	(23, 8, 'Diamond', 'canadianbacon', 'magician1099', 0, NULL, NULL),
	(24, 8, 'Diamond', 'kcmmmmm', 'krazycaley', 0, NULL, NULL),
	(25, 9, 'Diamond', 'scientist', 'canadianbacon', 0, NULL, NULL),
	(26, 9, 'Diamond', 'kcmmmmm', 'bloom', 0, NULL, NULL),
	(27, 9, 'Diamond', 'krazycaley', 'magician1099', 0, NULL, NULL),
	(28, 10, 'Diamond', 'scientist', 'kcmmmmm', 0, NULL, NULL),
	(29, 10, 'Diamond', 'krazycaley', 'canadianbacon', 0, NULL, NULL),
	(30, 10, 'Diamond', 'magician1099', 'bloom', 0, NULL, NULL),
	(31, 1, 'Platinum', 'royalflush', 'cleetose', 0, NULL, NULL),
	(32, 1, 'Platinum', 'sharper', 'kikar', 0, NULL, NULL),
	(33, 1, 'Platinum', 'varanas', 'slappydavis', 0, NULL, NULL),
	(34, 2, 'Platinum', 'royalflush', 'sharper', 0, NULL, NULL),
	(35, 2, 'Platinum', 'varanas', 'cleetose', 0, NULL, NULL),
	(36, 2, 'Platinum', 'slappydavis', 'kikar', 0, NULL, NULL),
	(37, 3, 'Platinum', 'royalflush', 'varanas', 0, NULL, NULL),
	(38, 3, 'Platinum', 'slappydavis', 'sharper', 0, NULL, NULL),
	(39, 3, 'Platinum', 'kikar', 'cleetose', 0, NULL, NULL),
	(40, 4, 'Platinum', 'royalflush', 'slappydavis', 0, NULL, NULL),
	(41, 4, 'Platinum', 'kikar', 'varanas', 0, NULL, NULL),
	(42, 4, 'Platinum', 'cleetose', 'sharper', 0, NULL, NULL),
	(43, 5, 'Platinum', 'royalflush', 'kikar', 0, NULL, NULL),
	(44, 5, 'Platinum', 'cleetose', 'slappydavis', 0, NULL, NULL),
	(45, 5, 'Platinum', 'sharper', 'varanas', 0, NULL, NULL),
	(46, 6, 'Platinum', 'cleetose', 'kikar', 0, NULL, NULL),
	(47, 6, 'Platinum', 'royalflush', 'varanas', 0, NULL, NULL),
	(48, 6, 'Platinum', 'sharper', 'slappydavis', 0, NULL, NULL),
	(49, 7, 'Platinum', 'cleetose', 'royalflush', 0, NULL, NULL),
	(50, 7, 'Platinum', 'sharper', 'kikar', 0, NULL, NULL),
	(51, 7, 'Platinum', 'slappydavis', 'varanas', 0, NULL, NULL),
	(52, 8, 'Platinum', 'cleetose', 'sharper', 0, NULL, NULL),
	(53, 8, 'Platinum', 'slappydavis', 'royalflush', 0, NULL, NULL),
	(54, 8, 'Platinum', 'varanas', 'kikar', 0, NULL, NULL),
	(55, 9, 'Platinum', 'cleetose', 'slappydavis', 0, NULL, NULL),
	(56, 9, 'Platinum', 'varanas', 'sharper', 0, NULL, NULL),
	(57, 9, 'Platinum', 'kikar', 'royalflush', 0, NULL, NULL),
	(58, 10, 'Platinum', 'cleetose', 'varanas', 0, NULL, NULL),
	(59, 10, 'Platinum', 'kikar', 'slappydavis', 0, NULL, NULL),
	(60, 10, 'Platinum', 'royalflush', 'sharper', 0, NULL, NULL),
	(61, 1, 'Gold', 'warningtrack', 'briguy', 0, NULL, NULL),
	(62, 1, 'Gold', 'cameraman', 'pires', 0, NULL, NULL),
	(63, 1, 'Gold', 'scallions', 'james1221', 0, NULL, NULL),
	(64, 2, 'Gold', 'warningtrack', 'cameraman', 0, NULL, NULL),
	(65, 2, 'Gold', 'scallions', 'briguy', 0, NULL, NULL),
	(66, 2, 'Gold', 'james1221', 'pires', 0, NULL, NULL),
	(67, 3, 'Gold', 'warningtrack', 'scallions', 0, NULL, NULL),
	(68, 3, 'Gold', 'james1221', 'cameraman', 0, NULL, NULL),
	(69, 3, 'Gold', 'pires', 'briguy', 0, NULL, NULL),
	(70, 4, 'Gold', 'warningtrack', 'james1221', 0, NULL, NULL),
	(71, 4, 'Gold', 'pires', 'scallions', 0, NULL, NULL),
	(72, 4, 'Gold', 'briguy', 'cameraman', 0, NULL, NULL),
	(73, 5, 'Gold', 'warningtrack', 'pires', 0, NULL, NULL),
	(74, 5, 'Gold', 'briguy', 'james1221', 0, NULL, NULL),
	(75, 5, 'Gold', 'cameraman', 'scallions', 0, NULL, NULL),
	(76, 6, 'Gold', 'scallions', 'briguy', 0, NULL, NULL),
	(77, 6, 'Gold', 'james1221', 'cameraman', 0, NULL, NULL),
	(78, 6, 'Gold', 'pires', 'warningtrack', 0, NULL, NULL),
	(79, 7, 'Gold', 'scallions', 'james1221', 0, NULL, NULL),
	(80, 7, 'Gold', 'pires', 'briguy', 0, NULL, NULL),
	(81, 7, 'Gold', 'warningtrack', 'cameraman', 0, NULL, NULL),
	(82, 8, 'Gold', 'scallions', 'pires', 0, NULL, NULL),
	(83, 8, 'Gold', 'warningtrack', 'james1221', 0, NULL, NULL),
	(84, 8, 'Gold', 'cameraman', 'briguy', 0, NULL, NULL),
	(85, 9, 'Gold', 'scallions', 'warningtrack', 0, NULL, NULL),
	(86, 9, 'Gold', 'cameraman', 'pires', 0, NULL, NULL),
	(87, 9, 'Gold', 'briguy', 'james1221', 0, NULL, NULL),
	(88, 10, 'Gold', 'scallions', 'cameraman', 0, NULL, NULL),
	(89, 10, 'Gold', 'briguy', 'warningtrack', 0, NULL, NULL),
	(90, 10, 'Gold', 'james1221', 'pires', 0, NULL, NULL),
	(91, 1, 'Silver', 'lthummus', 'checker', 0, NULL, NULL),
	(92, 1, 'Silver', 'fearfulferret', 'sgnurf', 0, NULL, NULL),
	(93, 1, 'Silver', 'iceman', 'elvisnake', 0, NULL, NULL),
	(94, 2, 'Silver', 'lthummus', 'fearfulferret', 0, NULL, NULL),
	(95, 2, 'Silver', 'iceman', 'checker', 0, NULL, NULL),
	(96, 2, 'Silver', 'elvisnake', 'sgnurf', 0, NULL, NULL),
	(97, 3, 'Silver', 'lthummus', 'iceman', 0, NULL, NULL),
	(98, 3, 'Silver', 'elvisnake', 'fearfulferret', 0, NULL, NULL),
	(99, 3, 'Silver', 'sgnurf', 'checker', 0, NULL, NULL),
	(100, 4, 'Silver', 'lthummus', 'elvisnake', 0, NULL, NULL),
	(101, 4, 'Silver', 'sgnurf', 'iceman', 0, NULL, NULL),
	(102, 4, 'Silver', 'checker', 'fearfulferret', 0, NULL, NULL),
	(103, 5, 'Silver', 'lthummus', 'sgnurf', 0, NULL, NULL),
	(104, 5, 'Silver', 'checker', 'elvisnake', 0, NULL, NULL),
	(105, 5, 'Silver', 'fearfulferret', 'iceman', 0, NULL, NULL),
	(106, 6, 'Silver', 'elvisnake', 'sgnurf', 0, NULL, NULL),
	(107, 6, 'Silver', 'checker', 'iceman', 0, NULL, NULL),
	(108, 6, 'Silver', 'fearfulferret', 'lthummus', 0, NULL, NULL),
	(109, 7, 'Silver', 'elvisnake', 'checker', 0, NULL, NULL),
	(110, 7, 'Silver', 'fearfulferret', 'sgnurf', 0, NULL, NULL),
	(111, 7, 'Silver', 'lthummus', 'iceman', 0, NULL, NULL),
	(112, 8, 'Silver', 'elvisnake', 'fearfulferret', 0, NULL, NULL),
	(113, 8, 'Silver', 'lthummus', 'checker', 0, NULL, NULL),
	(114, 8, 'Silver', 'iceman', 'sgnurf', 0, NULL, NULL),
	(115, 9, 'Silver', 'elvisnake', 'lthummus', 0, NULL, NULL),
	(116, 9, 'Silver', 'iceman', 'fearfulferret', 0, NULL, NULL),
	(117, 9, 'Silver', 'sgnurf', 'checker', 0, NULL, NULL),
	(118, 10, 'Silver', 'elvisnake', 'iceman', 0, NULL, NULL),
	(119, 10, 'Silver', 'sgnurf', 'lthummus', 0, NULL, NULL),
	(120, 10, 'Silver', 'checker', 'fearfulferret', 0, NULL, NULL);
/*!40000 ALTER TABLE `bout` ENABLE KEYS */;


-- Dumping structure for table scl.division
CREATE TABLE IF NOT EXISTS `division` (
  `name` varchar(32) NOT NULL,
  `precedence` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table scl.division: ~4 rows (approximately)
DELETE FROM `division`;
/*!40000 ALTER TABLE `division` DISABLE KEYS */;
INSERT INTO `division` (`name`, `precedence`) VALUES
	('Diamond', 0),
	('Gold', 2),
	('Platinum', 1),
	('Silver', 3);
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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

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

-- Dumping data for table scl.player: ~24 rows (approximately)
DELETE FROM `player`;
/*!40000 ALTER TABLE `player` DISABLE KEYS */;
INSERT INTO `player` (`name`, `division`, `wins`, `draws`, `losses`) VALUES
	('bloom', 'Diamond', 0, 0, 0),
	('briguy', 'Gold', 0, 0, 0),
	('cameraman', 'Gold', 0, 0, 0),
	('canadianbacon', 'Diamond', 0, 0, 0),
	('checker', 'Silver', 0, 0, 0),
	('cleetose', 'Platinum', 0, 0, 0),
	('elvisnake', 'Silver', 0, 0, 0),
	('fearfulferret', 'Silver', 0, 0, 0),
	('iceman', 'Silver', 0, 0, 0),
	('james1221', 'Gold', 0, 0, 0),
	('kcmmmmm', 'Diamond', 0, 0, 0),
	('kikar', 'Platinum', 0, 0, 0),
	('krazycaley', 'Diamond', 0, 0, 2),
	('lthummus', 'Silver', 0, 0, 0),
	('magician1099', 'Diamond', 2, 0, 0),
	('pires', 'Gold', 0, 0, 0),
	('royalflush', 'Platinum', 0, 0, 0),
	('scallions', 'Gold', 0, 0, 0),
	('scientist', 'Diamond', 0, 0, 0),
	('sgnurf', 'Silver', 0, 0, 0),
	('sharper', 'Platinum', 0, 0, 0),
	('slappydavis', 'Platinum', 0, 0, 0),
	('varanas', 'Platinum', 0, 0, 0),
	('warningtrack', 'Gold', 0, 0, 0);
/*!40000 ALTER TABLE `player` ENABLE KEYS */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
