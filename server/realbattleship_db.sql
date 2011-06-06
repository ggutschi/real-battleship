-- phpMyAdmin SQL Dump
-- version 3.3.2deb1
-- http://www.phpmyadmin.net
--
-- Host: sql03-dev.itac.at
-- Erstellungszeit: 06. Juni 2011 um 18:05
-- Server Version: 5.1.41
-- PHP-Version: 5.3.2-1ubuntu4.9

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Datenbank: `realbattleship_db`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur f√ºr Tabelle `challenges`
--

DROP TABLE IF EXISTS `challenges`;
CREATE TABLE IF NOT EXISTS `challenges` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `locationLeftTop` point NOT NULL,
  `locationRightBottom` point NOT NULL,
  `cellsX` int(11) NOT NULL,
  `cellsY` int(11) NOT NULL,
  `location` varchar(200) NOT NULL,
  `active` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=2 ;

--
-- Daten f√ºr Tabelle `challenges`
--

INSERT INTO `challenges` (`id`, `name`, `locationLeftTop`, `locationRightBottom`, `cellsX`, `cellsY`, `location`, `active`) VALUES
(1, 'Klagenfurt City Challenge', '\0\0\0\0\0\0\0PÂmzkPG@\0\0\0¿yö,@', '\0\0\0\0\0\0\0⁄eåqnOG@\0\0\0Ä°,@', 5, 5, 'Klagenfurt', 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur f√ºr Tabelle `participants`
--

DROP TABLE IF EXISTS `participants`;
CREATE TABLE IF NOT EXISTS `participants` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `challenge_id` int(11) NOT NULL,
  `inet_addr` varchar(100) NOT NULL,
  `android_id` varchar(100) NOT NULL,
  `active` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `android_id` (`android_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=6 ;

--
-- Daten f√ºr Tabelle `participants`
--

INSERT INTO `participants` (`id`, `challenge_id`, `inet_addr`, `android_id`, `active`) VALUES
(1, 1, '127.0.0.5', 'f3c4e5a1', 0),
(2, 1, '127.0.0.4', 'b3c4a5e6', 0),
(4, 1, '127.0.0.9', 'a3c4e5a1', 0),
(5, 1, '10.0.2.15', 'e3f17bc89afe54fe', 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur f√ºr Tabelle `ships`
--

DROP TABLE IF EXISTS `ships`;
CREATE TABLE IF NOT EXISTS `ships` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `destroyed` tinyint(1) NOT NULL,
  `challenge_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Daten f√ºr Tabelle `ships`
--

INSERT INTO `ships` (`id`, `destroyed`, `challenge_id`) VALUES
(1, 0, 1),
(2, 0, 1),
(3, 0, 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur f√ºr Tabelle `ship_positions`
--

DROP TABLE IF EXISTS `ship_positions`;
CREATE TABLE IF NOT EXISTS `ship_positions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `row` int(11) NOT NULL,
  `column` int(11) NOT NULL,
  `uncovered` tinyint(1) NOT NULL,
  `ship_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE_SHIP_POSITIONS` (`row`,`column`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

--
-- Daten f√ºr Tabelle `ship_positions`
--

INSERT INTO `ship_positions` (`id`, `row`, `column`, `uncovered`, `ship_id`) VALUES
(1, 2, 2, 0, 1),
(2, 2, 3, 0, 1),
(3, 4, 4, 0, 2),
(4, 0, 2, 1, 3);
