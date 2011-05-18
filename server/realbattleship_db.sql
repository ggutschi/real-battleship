-- phpMyAdmin SQL Dump
-- version 3.3.9
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Erstellungszeit: 17. Mai 2011 um 18:43
-- Server Version: 5.5.8
-- PHP-Version: 5.3.5

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
-- Tabellenstruktur für Tabelle `challenges`
--

DROP TABLE IF EXISTS `challenges`;
CREATE TABLE IF NOT EXISTS `challenges` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `locationLeftTop` point NOT NULL,
  `locationRightBottom` point NOT NULL,
  `location` varchar(200) NOT NULL,
  `active` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `challenges`
--

INSERT INTO `challenges` (`id`, `name`, `locationLeftTop`, `locationRightBottom`, `location`, `active`) VALUES
(1, 'Klagenfurt City Challenge', 0x0000000001010000000395f1ef339e2c40d5ec815660504740, 0x000000000101000000b988efc4acc72c40b8921d1b81504740, 'Klagenfurt', 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `participants`
--

DROP TABLE IF EXISTS `participants`;
CREATE TABLE IF NOT EXISTS `participants` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `challenge_id` int(11) NOT NULL,
  `inet_addr` varchar(100) NOT NULL,
  `android_id` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `android_id` (`android_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `participants`
--

INSERT INTO `participants` (`id`, `challenge_id`, `inet_addr`, `android_id`) VALUES
(1, 1, '127.0.0.1', 'f3c4e5a1'),
(2, 1, '127.0.0.2', 'b3c4a5e6');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ship_positions`
--

DROP TABLE IF EXISTS `ship_positions`;
CREATE TABLE IF NOT EXISTS `ship_positions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `challenge_id` int(11) NOT NULL,
  `row` int(11) NOT NULL,
  `column` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE_SHIP_POSITIONS` (`challenge_id`,`row`,`column`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `ship_positions`
--

INSERT INTO `ship_positions` (`id`, `challenge_id`, `row`, `column`) VALUES
(1, 1, 2, 2),
(2, 1, 2, 3);
