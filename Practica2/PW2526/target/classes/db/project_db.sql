-- Desactivar verificaciones de claves foráneas
SET FOREIGN_KEY_CHECKS = 0;

-- Eliminar tablas en el orden correcto (primero las que tienen claves foráneas)
DROP TABLE IF EXISTS `Alquiler`;
DROP TABLE IF EXISTS `Reserva`;
DROP TABLE IF EXISTS `Socio`;
DROP TABLE IF EXISTS `Embarcacion`;
DROP TABLE IF EXISTS `Inscripcion`;
DROP TABLE IF EXISTS `Patron`;

-- Reactivar verificaciones de claves foráneas
SET FOREIGN_KEY_CHECKS = 1;

-- Ahora crear las tablas en el orden correcto
CREATE TABLE IF NOT EXISTS `Patron` (
  `id` varchar(9) NOT NULL,
  `name` varchar(50) NOT NULL,
  `surname` varchar(100) NOT NULL,
  `birthDate` date NOT NULL,
  `titleIssueDate` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `Embarcacion` (
  `registrationNumber` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `type` ENUM('VELERO', 'YATE', 'CATAMARAN', 'LANCHA') NOT NULL,
  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL,
  `numSeats` int(11) unsigned NOT NULL,
  `patronId` varchar(9) COLLATE utf8_unicode_ci NULL,
  `length` decimal(8,2) unsigned NOT NULL,
  `width` decimal(8,2) unsigned NOT NULL,
  `height` decimal(8,2) unsigned NOT NULL,
  PRIMARY KEY (`registrationNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `Socio` (
    `id` varchar(9) COLLATE utf8_unicode_ci NOT NULL,
    `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
    `surname` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
    `address` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
    `birthdate` DATE NOT NULL,
    `inscriptionDate` DATE NOT NULL,
    `isHolderInscription` BOOLEAN NOT NULL,
    `isBoatDriver` BOOLEAN NOT NULL,
    `isAdult` BOOLEAN NOT NULL,
    `inscriptionId` INT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `Inscripcion` (
    `id` INT AUTO_INCREMENT,
    `userId` varchar(9) COLLATE utf8_unicode_ci NULL,
    `date` DATE NOT NULL,
    `totalAmount` INT NOT NULL,
    `registeredAdults` INT NOT NULL,
    `registeredKids` INT NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `Reserva` (
    `id` INT AUTO_INCREMENT,
    `registrationNumber` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
    `userId` varchar(9) COLLATE utf8_unicode_ci NOT NULL,
    `purpose` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
    `date` DATE NOT NULL,
    `numSeats` INT NOT NULL,
    `totalAmount` INT NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE IF NOT EXISTS `Alquiler` (
  `id` int(11) AUTO_INCREMENT,
  `userId` varchar(9) COLLATE utf8_unicode_ci NOT NULL,
  `startDate` date NOT NULL,
  `endDate` date NOT NULL,
  `registrationNumber` varchar(20) COLLATE utf8_unicode_ci NOT NULL,
  `numSeats` int(11) NOT NULL,
  `totalAmount` INT NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- CLAVES FORÁNEAS
ALTER TABLE `Embarcacion`
ADD FOREIGN KEY (`patronId`) REFERENCES `Patron`(`id`);

ALTER TABLE `Inscripcion`
ADD FOREIGN KEY (`userId`) REFERENCES `Socio`(`id`);

ALTER TABLE `Socio`
ADD FOREIGN KEY (`inscriptionId`) REFERENCES `Inscripcion`(`id`);

ALTER TABLE `Reserva`
ADD FOREIGN KEY (`userId`) REFERENCES `Socio`(`id`),
ADD FOREIGN KEY (`registrationNumber`) REFERENCES `Embarcacion`(`registrationNumber`);

ALTER TABLE `Alquiler`
ADD FOREIGN KEY (`userId`) REFERENCES `Socio`(`id`),
ADD FOREIGN KEY (`registrationNumber`) REFERENCES `Embarcacion`(`registrationNumber`);

-- INSERCIONES CORREGIDAS
INSERT INTO `Patron` (`id`, `name`, `surname`, `birthDate`, `titleIssueDate`) VALUES
('12345678A', 'Carlos', 'Navarro Gómez', '1980-05-14', '2005-06-20'),
('87654321B', 'Lucía', 'Martínez Ruiz', '1985-10-02', '2010-04-15');

INSERT INTO `Embarcacion` (`registrationNumber`, `type`, `name`, `numSeats`, `patronId`, `length`, `width`, `height`) VALUES
('REG001', 'VELERO', 'Mar Azul', 6, '12345678A', 8.50, 2.40, 3.10),
('REG002', 'YATE', 'Rayo del Sur', 4, '87654321B', 5.20, 1.80, 2.00);

INSERT INTO `Socio` (`id`, `name`, `surname`, `address`, `birthdate`, `inscriptionDate`, `isHolderInscription`, `isBoatDriver`, `isAdult`, `inscriptionId`) VALUES
('11111111C', 'Ana', 'López Pérez', 'Calle Sol 45, Valencia', '1990-07-12', '2020-10-10', TRUE, TRUE, TRUE, NULL),
('22222222D', 'Pedro', 'Santos Díaz', 'Av. Mar 10, Alicante', '2002-09-23', '2025-05-11', TRUE, FALSE, TRUE, NULL);

INSERT INTO `Inscripcion` (`userId`, `date`, `totalAmount`, `registeredAdults`, `registeredKids`) VALUES
('11111111C', '2020-10-10', 300, 1, 0),
('22222222D', '2025-05-11', 300, 1, 0);

-- Actualizar los socios con los IDs de inscripción
UPDATE `Socio` SET `inscriptionId` = 1 WHERE `id` = '11111111C';
UPDATE `Socio` SET `inscriptionId` = 2 WHERE `id` = '22222222D';

INSERT INTO `Reserva` (`registrationNumber`, `userId`, `purpose`, `date`, `numSeats`, `totalAmount`) VALUES
('REG001', '11111111C', 'Paseo marítimo familiar', '2025-06-12', 4, 160),
('REG002', '22222222D', 'Excursión con amigos', '2025-07-05', 3, 120);

INSERT INTO `Alquiler` (`userId`, `startDate`, `endDate`, `registrationNumber`, `numSeats`, `totalAmount`) VALUES
('11111111C', '2025-08-01', '2025-08-05', 'REG001', 4, 320),
('22222222D', '2025-09-10', '2025-09-12', 'REG002', 3, 120);