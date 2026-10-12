-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3307
-- Généré le : lun. 12 oct. 2026 à 13:53
-- Version du serveur : 10.10.2-MariaDB
-- Version de PHP : 8.0.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `springfieldbd`
--

-- --------------------------------------------------------

--
-- Structure de la table `equipement`
--

DROP TABLE IF EXISTS `equipement`;
CREATE TABLE IF NOT EXISTS `equipement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `reference` varchar(30) NOT NULL,
  `libelle` varchar(100) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `type_id` bigint(20) NOT NULL,
  `station_id` bigint(20) NOT NULL,
  `etat` enum('EN_SERVICE','DEGRADE','HORS_SERVICE') NOT NULL DEFAULT 'EN_SERVICE',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_equipement_reference` (`reference`),
  KEY `fk_equipement_type` (`type_id`),
  KEY `fk_equipement_station` (`station_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- --------------------------------------------------------

--
-- Structure de la table `evaluation`
--

DROP TABLE IF EXISTS `evaluation`;
CREATE TABLE IF NOT EXISTS `evaluation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `incident_id` bigint(20) NOT NULL,
  `note` tinyint(4) NOT NULL,
  `commentaire` varchar(500) DEFAULT NULL,
  `date_evaluation` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_evaluation_incident` (`incident_id`)
) ;

-- --------------------------------------------------------

--
-- Structure de la table `incident`
--

DROP TABLE IF EXISTS `incident`;
CREATE TABLE IF NOT EXISTS `incident` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `equipement_id` bigint(20) NOT NULL,
  `description` varchar(500) NOT NULL,
  `date_ouverture` datetime NOT NULL DEFAULT current_timestamp(),
  `statut` enum('OUVERT','AFFECTE','RESOLU') NOT NULL DEFAULT 'OUVERT',
  `prestataire_id` bigint(20) DEFAULT NULL,
  `date_resolution` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_incident_equipement` (`equipement_id`),
  KEY `fk_incident_prestataire` (`prestataire_id`),
  KEY `idx_incident_statut` (`statut`)
) ;

-- --------------------------------------------------------

--
-- Structure de la table `prestataire`
--

DROP TABLE IF EXISTS `prestataire`;
CREATE TABLE IF NOT EXISTS `prestataire` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) NOT NULL,
  `email` varchar(150) DEFAULT NULL,
  `telephone` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ;

-- --------------------------------------------------------

--
-- Structure de la table `station`
--

DROP TABLE IF EXISTS `station`;
CREATE TABLE IF NOT EXISTS `station` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_station_nom` (`nom`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- --------------------------------------------------------

--
-- Structure de la table `type_equipement`
--

DROP TABLE IF EXISTS `type_equipement`;
CREATE TABLE IF NOT EXISTS `type_equipement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `libelle` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_type_equipement_libelle` (`libelle`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- --------------------------------------------------------

--
-- Structure de la table `utilisateur`
--

DROP TABLE IF EXISTS `utilisateur`;
CREATE TABLE IF NOT EXISTS `utilisateur` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `login` varchar(50) NOT NULL,
  `mot_de_passe_hash` varchar(100) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `profil` enum('ADMIN','GESTIONNAIRE','CONSULTATION') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_utilisateur_login` (`login`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
