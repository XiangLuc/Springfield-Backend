SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS springfield_bdd CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE springfield_bdd;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS evaluation;
DROP TABLE IF EXISTS incident;
DROP TABLE IF EXISTS equipement;
DROP TABLE IF EXISTS prestataire;
DROP TABLE IF EXISTS type_equipement;
DROP TABLE IF EXISTS station;
DROP TABLE IF EXISTS utilisateur;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE station (
    id INT NOT NULL AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_station_nom (nom)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE type_equipement (
    id INT NOT NULL AUTO_INCREMENT,
    libelle VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_type_equipement_libelle (libelle)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE prestataire (
    id INT NOT NULL AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(150) DEFAULT NULL,
    telephone VARCHAR(20) DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_prestataire_contact CHECK (email IS NOT NULL OR telephone IS NOT NULL)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE utilisateur (
    id INT NOT NULL AUTO_INCREMENT,
    login VARCHAR(50) NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    profil ENUM('AGENT', 'RESPONSABLE_EXPLOITATION') NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_utilisateur_login (login)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE equipement (
    id INT NOT NULL AUTO_INCREMENT,
    reference VARCHAR(30) NOT NULL,
    libelle VARCHAR(100) NOT NULL,
    description VARCHAR(255) DEFAULT NULL,
    type_id INT NOT NULL,
    station_id INT NOT NULL,
    etat ENUM('EN_SERVICE', 'DEGRADE', 'HORS_SERVICE') NOT NULL DEFAULT 'EN_SERVICE',
    PRIMARY KEY (id),
    UNIQUE KEY uq_equipement_reference (reference),
    CONSTRAINT fk_equipement_type FOREIGN KEY (type_id) REFERENCES type_equipement (id),
    CONSTRAINT fk_equipement_station FOREIGN KEY (station_id) REFERENCES station (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE incident (
    id INT NOT NULL AUTO_INCREMENT,
    equipement_id INT NOT NULL,
    description VARCHAR(500) NOT NULL,
    date_ouverture DATETIME NOT NULL,
    statut ENUM('OUVERT', 'AFFECTE', 'RESOLU') NOT NULL DEFAULT 'OUVERT',
    prestataire_id INT DEFAULT NULL,
    date_resolution DATETIME DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_incident_statut (statut),
    CONSTRAINT fk_incident_equipement FOREIGN KEY (equipement_id) REFERENCES equipement (id),
    CONSTRAINT fk_incident_prestataire FOREIGN KEY (prestataire_id) REFERENCES prestataire (id),
    CONSTRAINT ck_incident_date_resolution CHECK (date_resolution IS NULL OR date_resolution >= date_ouverture),
    CONSTRAINT ck_incident_statut CHECK (
        (statut = 'OUVERT' AND prestataire_id IS NULL AND date_resolution IS NULL)
        OR (statut = 'AFFECTE' AND prestataire_id IS NOT NULL AND date_resolution IS NULL)
        OR (statut = 'RESOLU' AND date_resolution IS NOT NULL)
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE evaluation (
    id INT NOT NULL AUTO_INCREMENT,
    incident_id INT NOT NULL,
    note TINYINT NOT NULL,
    commentaire VARCHAR(500) DEFAULT NULL,
    date_evaluation DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_evaluation_incident (incident_id),
    CONSTRAINT fk_evaluation_incident FOREIGN KEY (incident_id) REFERENCES incident (id),
    CONSTRAINT ck_evaluation_note CHECK (note BETWEEN 1 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO station (id, nom) VALUES
    (1, 'Springfield Centre'),
    (2, 'Evergreen Terrace'),
    (3, 'Centrale Nucléaire'),
    (4, 'Hôtel de Ville'),
    (5, 'Université'),
    (6, 'Port de Springfield'),
    (7, 'Aéroport');

INSERT INTO type_equipement (id, libelle) VALUES
    (1, 'Ascenseur'),
    (2, 'Escalier mécanique'),
    (3, 'Portique de validation'),
    (4, 'Distributeur de titres'),
    (5, 'Écran d''information'),
    (6, 'Trottoir roulant');

INSERT INTO prestataire (id, nom, email, telephone) VALUES
    (1, 'Élévation Services', 'interventions@elevation-services.fr', '01 99 00 10 01'),
    (2, 'Mécatrans Maintenance', 'astreinte@mecatrans.fr', '01 99 00 10 02'),
    (3, 'Billettique Express', 'support@billettique-express.fr', '01 99 00 10 03'),
    (4, 'Signalétique Pro', 'sav@signaletique-pro.fr', '01 99 00 10 04'),
    (5, 'Multitech Intervention', NULL, '01 99 00 10 05'),
    (6, 'Rapid''Élec Dépannage', 'contact@rapidelec.fr', NULL);

INSERT INTO utilisateur (id, login, mot_de_passe, prenom, profil) VALUES
    -- Mot de passe : motdepasse-Admin12345@?
    (1, 'resp.exploitation', '$argon2id$v=19$m=19456,t=2,p=1$w1aRxPSlAs3D6iczDZYijw$P12LEq9huYnC+arNdky2/+z3GavOzdQbcqiHKv5Zkk4', 'responsable', 'RESPONSABLE_EXPLOITATION'),
    -- Mot de passe : motdepasse-Admin12345@?
    (2, 'agent.terrain', '$argon2id$v=19$m=19456,t=2,p=1$w1aRxPSlAs3D6iczDZYijw$P12LEq9huYnC+arNdky2/+z3GavOzdQbcqiHKv5Zkk4', 'agent', 'AGENT');

INSERT INTO equipement (id, reference, libelle, description, type_id, station_id, etat) VALUES
    (1, 'ASC-CEN-01', 'Ascenseur quai A', 'Relie le hall au quai direction Port de Springfield', 1, 1, 'HORS_SERVICE'),
    (2, 'ESC-CEN-01', 'Escalier mécanique sortie principale', 'Montée vers la sortie Place centrale', 2, 1, 'DEGRADE'),
    (3, 'PTQ-CEN-01', 'Portique de validation hall nord', NULL, 3, 1, 'EN_SERVICE'),
    (4, 'DIS-CEN-01', 'Distributeur hall nord', 'Accepte carte bancaire et espèces', 4, 1, 'HORS_SERVICE'),
    (5, 'ECR-CEN-01', 'Écran des prochains départs', 'Écran principal du hall', 5, 1, 'EN_SERVICE'),
    (6, 'ASC-EVG-01', 'Ascenseur accès rue', 'Accès PMR depuis Evergreen Terrace', 1, 2, 'DEGRADE'),
    (7, 'PTQ-EVG-01', 'Portique entrée est', NULL, 3, 2, 'HORS_SERVICE'),
    (8, 'DIS-EVG-01', 'Distributeur quai central', NULL, 4, 2, 'EN_SERVICE'),
    (9, 'ESC-NUC-01', 'Escalier mécanique quai 1', 'Montée du quai 1 vers la passerelle', 2, 3, 'HORS_SERVICE'),
    (10, 'ESC-NUC-02', 'Escalier mécanique quai 2', 'Montée du quai 2 vers la passerelle', 2, 3, 'EN_SERVICE'),
    (11, 'ECR-NUC-01', 'Écran d''information quai 1', NULL, 5, 3, 'DEGRADE'),
    (12, 'PTQ-NUC-01', 'Portique accès principal', NULL, 3, 3, 'EN_SERVICE'),
    (13, 'ASC-HDV-01', 'Ascenseur parvis', 'Relie le parvis de l''Hôtel de Ville à la mezzanine', 1, 4, 'EN_SERVICE'),
    (14, 'DIS-HDV-01', 'Distributeur mezzanine', NULL, 4, 4, 'DEGRADE'),
    (15, 'DIS-HDV-02', 'Distributeur sortie mairie', NULL, 4, 4, 'EN_SERVICE'),
    (16, 'ECR-HDV-01', 'Écran d''information mezzanine', NULL, 5, 4, 'HORS_SERVICE'),
    (17, 'PTQ-UNI-01', 'Portique entrée campus', 'Portique large accessible aux fauteuils roulants', 3, 5, 'DEGRADE'),
    (18, 'PTQ-UNI-02', 'Portique sortie campus', NULL, 3, 5, 'EN_SERVICE'),
    (19, 'ESC-UNI-01', 'Escalier mécanique bibliothèque', 'Sortie côté bibliothèque universitaire', 2, 5, 'EN_SERVICE'),
    (20, 'ASC-PRT-01', 'Ascenseur gare maritime', 'Liaison directe avec le terminal des ferries', 1, 6, 'EN_SERVICE'),
    (21, 'ECR-PRT-01', 'Écran correspondances ferries', NULL, 5, 6, 'DEGRADE'),
    (22, 'DIS-PRT-01', 'Distributeur quai', NULL, 4, 6, 'EN_SERVICE'),
    (23, 'TRO-AER-01', 'Trottoir roulant terminal 1', 'Couloir de correspondance vers les départs', 6, 7, 'HORS_SERVICE'),
    (24, 'TRO-AER-02', 'Trottoir roulant terminal 2', 'Couloir de correspondance vers les arrivées', 6, 7, 'EN_SERVICE'),
    (25, 'ASC-AER-01', 'Ascenseur parking', 'Dessert les niveaux -2 à +1', 1, 7, 'DEGRADE'),
    (26, 'PTQ-AER-01', 'Portique navette aéroport', NULL, 3, 7, 'EN_SERVICE');

INSERT INTO incident (id, equipement_id, description, date_ouverture, statut, prestataire_id, date_resolution) VALUES
    (1, 3, 'Le portique ne lit plus les cartes sans contact.', NOW() - INTERVAL '75 03:20' DAY_MINUTE, 'RESOLU', 3, NOW() - INTERVAL '74 01:10' DAY_MINUTE),
    (2, 1, 'Cabine immobilisée au niveau quai, portes ouvertes.', NOW() - INTERVAL '60 08:45' DAY_MINUTE, 'RESOLU', 1, NOW() - INTERVAL '57 16:30' DAY_MINUTE),
    (3, 9, 'Marches désalignées en bas de l''escalier, risque de chute.', NOW() - INTERVAL '55 06:15' DAY_MINUTE, 'RESOLU', 2, NOW() - INTERVAL '50 10:00' DAY_MINUTE),
    (4, 11, 'Écran figé sur un message datant de 6 h du matin.', NOW() - INTERVAL '50 14:05' DAY_MINUTE, 'RESOLU', 4, NOW() - INTERVAL '49 09:40' DAY_MINUTE),
    (5, 8, 'Monnaie non rendue après un achat en espèces.', NOW() - INTERVAL '45 10:30' DAY_MINUTE, 'RESOLU', 3, NOW() - INTERVAL '44 15:20' DAY_MINUTE),
    (6, 13, 'Bruit de frottement important pendant la montée.', NOW() - INTERVAL '40 07:50' DAY_MINUTE, 'RESOLU', 1, NOW() - INTERVAL '38 11:15' DAY_MINUTE),
    (7, 5, 'Horaires affichés ne correspondant à aucun départ réel.', NOW() - INTERVAL '35 17:25' DAY_MINUTE, 'RESOLU', 4, NOW() - INTERVAL '34 12:00' DAY_MINUTE),
    (8, 19, 'Escalier qui s''arrête de lui-même toutes les dix minutes.', NOW() - INTERVAL '30 09:10' DAY_MINUTE, 'RESOLU', 2, NOW() - INTERVAL '26 14:45' DAY_MINUTE),
    (9, 7, 'Portique bloqué en position ouverte.', NOW() - INTERVAL '28 18:40' DAY_MINUTE, 'RESOLU', 3, NOW() - INTERVAL '27 20:15' DAY_MINUTE),
    (10, 24, 'Tapis qui avance par à-coups.', NOW() - INTERVAL '25 11:05' DAY_MINUTE, 'RESOLU', 5, NOW() - INTERVAL '24 08:30' DAY_MINUTE),
    (11, 23, 'Main courante déchirée sur plusieurs mètres.', NOW() - INTERVAL '21 13:35' DAY_MINUTE, 'RESOLU', 5, NOW() - INTERVAL '17 09:50' DAY_MINUTE),
    (12, 15, 'Rouleau de tickets vide, aucun titre délivré.', NOW() - INTERVAL '20 07:20' DAY_MINUTE, 'RESOLU', 3, NOW() - INTERVAL '19 16:10' DAY_MINUTE),
    (13, 10, 'Odeur de brûlé au niveau du moteur.', NOW() - INTERVAL '18 16:55' DAY_MINUTE, 'RESOLU', 2, NOW() - INTERVAL '15 10:25' DAY_MINUTE),
    (14, 20, 'Bouton d''appel d''urgence sans effet.', NOW() - INTERVAL '14 12:40' DAY_MINUTE, 'RESOLU', 1, NOW() - INTERVAL '13 18:05' DAY_MINUTE),
    (15, 16, 'Écran éteint, aucune alimentation détectée.', NOW() - INTERVAL '12 05:30' DAY_MINUTE, 'OUVERT', NULL, NULL),
    (16, 26, 'Lecteur de QR codes hors d''usage.', NOW() - INTERVAL '10 09:15' DAY_MINUTE, 'RESOLU', 3, NOW() - INTERVAL '9 14:50' DAY_MINUTE),
    (17, 12, 'Battants du portique qui se referment sur les usagers.', NOW() - INTERVAL '8 19:00' DAY_MINUTE, 'RESOLU', 5, NOW() - INTERVAL '6 11:30' DAY_MINUTE),
    (18, 22, 'Écran tactile qui ne répond plus par endroits.', NOW() - INTERVAL '6 08:20' DAY_MINUTE, 'RESOLU', 3, NOW() - INTERVAL '5 13:45' DAY_MINUTE),
    (19, 23, 'Bruit métallique suivi d''un arrêt complet du trottoir.', NOW() - INTERVAL '5 07:45' DAY_MINUTE, 'OUVERT', NULL, NULL),
    (20, 18, 'Portique qui reste fermé après une validation réussie.', NOW() - INTERVAL '4 15:30' DAY_MINUTE, 'RESOLU', 5, NOW() - INTERVAL '3 10:20' DAY_MINUTE),
    (21, 25, 'Bouton d''appel du niveau -1 inopérant.', NOW() - INTERVAL 80 HOUR, 'AFFECTE', 5, NULL),
    (22, 4, 'Billets imprimés illisibles.', NOW() - INTERVAL 78 HOUR, 'RESOLU', 3, NOW() - INTERVAL 70 HOUR),
    (23, 9, 'Arrêt d''urgence déclenché, l''escalier ne redémarre pas.', NOW() - INTERVAL 72 HOUR, 'OUVERT', NULL, NULL),
    (24, 17, 'Lecteur de carte sans contact intermittent.', NOW() - INTERVAL 71 HOUR, 'OUVERT', NULL, NULL),
    (25, 21, 'Horaires des ferries décalés d''une dizaine de minutes.', NOW() - INTERVAL 60 HOUR, 'OUVERT', NULL, NULL),
    (26, 2, 'Main courante à l''arrêt alors que les marches fonctionnent.', NOW() - INTERVAL 50 HOUR, 'AFFECTE', 2, NULL),
    (27, 14, 'Paiement par carte bancaire refusé systématiquement.', NOW() - INTERVAL 48 HOUR, 'OUVERT', NULL, NULL),
    (28, 7, 'Portique bloqué en position fermée, passage impossible.', NOW() - INTERVAL 47 HOUR, 'OUVERT', NULL, NULL),
    (29, 6, 'Portes qui se referment trop rapidement.', NOW() - INTERVAL 30 HOUR, 'AFFECTE', 1, NULL),
    (30, 5, 'Écran qui redémarre en boucle.', NOW() - INTERVAL 26 HOUR, 'RESOLU', 4, NOW() - INTERVAL 2 HOUR),
    (31, 11, 'Moitié basse de l''écran restée noire.', NOW() - INTERVAL 20 HOUR, 'OUVERT', NULL, NULL),
    (32, 1, 'Cabine bloquée entre deux niveaux, aucun usager à l''intérieur.', NOW() - INTERVAL 3 HOUR, 'AFFECTE', 1, NULL),
    (33, 4, 'Écran tactile figé, impossible de sélectionner un titre.', NOW() - INTERVAL 2 HOUR, 'OUVERT', NULL, NULL),
    (34, 10, 'Vibrations importantes au démarrage de l''escalier.', NOW() - INTERVAL '9 06:20' DAY_MINUTE, 'RESOLU', 2, NOW() - INTERVAL '8 09:10' DAY_MINUTE),
    (35, 19, 'Arrêt brutal de l''escalier aux heures de pointe.', NOW() - INTERVAL '7 14:00' DAY_MINUTE, 'RESOLU', 2, NOW() - INTERVAL '5 10:40' DAY_MINUTE),
    (36, 24, 'Bandeau lumineux du trottoir roulant éteint.', NOW() - INTERVAL '19 10:15' DAY_MINUTE, 'RESOLU', 5, NOW() - INTERVAL '18 15:30' DAY_MINUTE),
    (37, 26, 'Portique qui refuse les titres de transport valides.', NOW() - INTERVAL '11 09:05' DAY_MINUTE, 'RESOLU', 5, NOW() - INTERVAL '10 13:20' DAY_MINUTE);

INSERT INTO evaluation (id, incident_id, note, commentaire, date_evaluation) VALUES
    (1, 1, 4, 'Intervention rapide, portique remis en service dans la journée.', NOW() - INTERVAL '73 10:00' DAY_MINUTE),
    (2, 2, 5, 'Technicien très réactif, explications claires.', NOW() - INTERVAL '57 09:00' DAY_MINUTE),
    (3, 3, 2, 'Deux passages nécessaires, délai trop long pour un risque de chute.', NOW() - INTERVAL '49 15:00' DAY_MINUTE),
    (4, 4, 4, NULL, NOW() - INTERVAL '48 10:00' DAY_MINUTE),
    (5, 5, 5, 'Monnayeur remplacé, aucun incident depuis.', NOW() - INTERVAL '44 08:00' DAY_MINUTE),
    (6, 6, 4, 'Bon travail, mais intervention en heure de pointe.', NOW() - INTERVAL '38 09:30' DAY_MINUTE),
    (7, 7, 3, 'Problème corrigé, mais un second passage a été nécessaire pour le réglage.', NOW() - INTERVAL '33 14:00' DAY_MINUTE),
    (8, 8, 1, 'La panne est réapparue le lendemain de l''intervention.', NOW() - INTERVAL '25 10:00' DAY_MINUTE),
    (9, 9, 4, NULL, NOW() - INTERVAL '27 09:00' DAY_MINUTE),
    (10, 10, 4, 'Réglage efficace, tapis de nouveau fluide.', NOW() - INTERVAL '23 16:00' DAY_MINUTE),
    (11, 11, 3, 'Réparation provisoire, la main courante devra être remplacée.', NOW() - INTERVAL '16 11:00' DAY_MINUTE),
    (12, 12, 5, NULL, NOW() - INTERVAL '19 08:00' DAY_MINUTE),
    (13, 13, 3, 'Moteur remplacé, mais quatre jours d''immobilisation.', NOW() - INTERVAL '14 17:00' DAY_MINUTE),
    (14, 14, 5, 'Intervention exemplaire sur un équipement de sécurité.', NOW() - INTERVAL '13 09:00' DAY_MINUTE),
    (15, 16, 4, 'Lecteur changé rapidement.', NOW() - INTERVAL '8 10:00' DAY_MINUTE),
    (16, 20, 2, 'Le portique se bloque encore ponctuellement.', NOW() - INTERVAL '2 09:00' DAY_MINUTE),
    (17, 34, 2, 'Vibrations toujours présentes après l''intervention.', NOW() - INTERVAL '7 16:00' DAY_MINUTE),
    (18, 35, 2, 'Deux interventions nécessaires pour le même défaut.', NOW() - INTERVAL '4 11:00' DAY_MINUTE),
    (19, 36, 3, 'Réparation correcte, délai un peu long.', NOW() - INTERVAL '17 09:00' DAY_MINUTE),
    (20, 37, 3, NULL, NOW() - INTERVAL '9 10:00' DAY_MINUTE);
