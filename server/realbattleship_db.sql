-- phpMyAdmin SQL Dump
-- version 3.3.2deb1
-- http://www.phpmyadmin.net
--
-- Host: sql03-dev.itac.at
-- Erstellungszeit: 21. Juni 2011 um 18:45
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
-- Tabellenstruktur für Tabelle `challenges`
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
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Daten für Tabelle `challenges`
--

INSERT INTO `challenges` (`id`, `name`, `locationLeftTop`, `locationRightBottom`, `cellsX`, `cellsY`, `location`, `active`) VALUES
(1, 'Klagenfurt City Challenge', '\0\0\0\0\0\0\0P�mzkPG@\0\0\0�y�,@', '\0\0\0\0\0\0\0�e�qnOG@\0\0\0��,@', 5, 5, 'Klagenfurt', 1),
(2, 'Uni Challenge', '\0\0\0\0\0\0\0��\0�NG@�E''K��,@', '\0\0\0\0\0\0\0�NG@|a2U�,@', 4, 3, 'Universitaet Klagenfurt', 1);

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
  `active` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `android_id` (`android_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=50 ;

--
-- Daten für Tabelle `participants`
--

INSERT INTO `participants` (`id`, `challenge_id`, `inet_addr`, `android_id`, `active`) VALUES
(1, 1, '127.0.0.5', 'f3c4e5a1', 0),
(2, 1, '127.0.0.4', 'b3c4a5e6', 0),
(4, 1, '127.0.0.9', 'a3c4e5a1', 0),
(5, 1, '10.0.2.15', 'e3f17bc89afe54fe', 1),
(6, 1, '92.248.54.136', '200145745bbb418b', 0);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ships`
--

DROP TABLE IF EXISTS `ships`;
CREATE TABLE IF NOT EXISTS `ships` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `destroyed` tinyint(1) NOT NULL,
  `challenge_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=7 ;

--
-- Daten für Tabelle `ships`
--

INSERT INTO `ships` (`id`, `destroyed`, `challenge_id`) VALUES
(1, 0, 1),
(2, 0, 1),
(3, 0, 1),
(4, 0, 2),
(5, 0, 2),
(6, 0, 2);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `ship_positions`
--

DROP TABLE IF EXISTS `ship_positions`;
CREATE TABLE IF NOT EXISTS `ship_positions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `row` int(11) NOT NULL,
  `column` int(11) NOT NULL,
  `uncovered` tinyint(1) NOT NULL,
  `ship_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=11 ;

--
-- Daten für Tabelle `ship_positions`
--

INSERT INTO `ship_positions` (`id`, `row`, `column`, `uncovered`, `ship_id`) VALUES
(1, 2, 2, 0, 1),
(2, 2, 3, 0, 1),
(3, 4, 4, 0, 2),
(4, 0, 2, 0, 3),
(5, 0, 0, 0, 4),
(6, 2, 0, 1, 5),
(7, 2, 1, 0, 5),
(8, 1, 3, 0, 6),
(10, 2, 3, 0, 6);
