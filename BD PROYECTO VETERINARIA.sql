-- 1) DROP DATABASE veterinaria_db

CREATE DATABASE IF NOT EXISTS veterinaria_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;
USE veterinaria_db;

SET FOREIGN_KEY_CHECKS = 0;

DROP VIEW IF EXISTS v_mascotas_con_cliente;
DROP VIEW IF EXISTS v_citas_detalle;

DROP TABLE IF EXISTS citas;
DROP TABLE IF EXISTS mascota;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS roles;

CREATE TABLE roles (
  id      INT AUTO_INCREMENT PRIMARY KEY,
  nombre  VARCHAR(40) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- 2.2) Usuarios
CREATE TABLE usuarios (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  nombre       VARCHAR(120) NOT NULL,
  email        VARCHAR(160) NOT NULL UNIQUE,
  pass_hash    VARCHAR(100) NOT NULL,
  activo       TINYINT(1)   NOT NULL DEFAULT 1,
  rol_id       INT          NOT NULL,
  created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_usuarios_roles
    FOREIGN KEY (rol_id) REFERENCES roles(id)
      ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE cliente (
  id_cliente  INT AUTO_INCREMENT PRIMARY KEY,
  nombre      VARCHAR(50)  NOT NULL,
  apellido    VARCHAR(50)  NOT NULL,
  direccion   VARCHAR(100) NOT NULL,
  telefono    VARCHAR(20)  NOT NULL,
  created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE mascota (
  id               INT AUTO_INCREMENT PRIMARY KEY,
  nombre           VARCHAR(100) NOT NULL,
  raza             VARCHAR(60)  NULL,
  especie          VARCHAR(50)  NULL,
  fecha_registro   DATE         NULL,
  fecha_nacimiento DATE         NULL,
  cliente_id       INT          NULL,       
  foto             LONGBLOB     NULL,       
  CONSTRAINT fk_mascota_cliente
    FOREIGN KEY (cliente_id) REFERENCES cliente(id_cliente)
      ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

-- 2.5) Citas (apunta a mascota.id)
CREATE TABLE citas (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  mascota_id  INT NOT NULL,
  fecha_hora  DATETIME NOT NULL,
  motivo      VARCHAR(255) NOT NULL,
  estado      ENUM('Pendiente','Realizada','Cancelada') NOT NULL DEFAULT 'Pendiente',
  created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_citas_mascota
    FOREIGN KEY (mascota_id) REFERENCES mascota(id)
      ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3) ÍNDICES
CREATE INDEX idx_roles_nombre         ON roles(nombre);
CREATE INDEX idx_usuarios_email       ON usuarios(email);

CREATE INDEX idx_cliente_nombre       ON cliente(nombre);
CREATE INDEX idx_cliente_apellido     ON cliente(apellido);
CREATE INDEX idx_cliente_telefono     ON cliente(telefono);

CREATE INDEX idx_mascota_nombre       ON mascota(nombre);
CREATE INDEX idx_mascota_cliente      ON mascota(cliente_id);

CREATE INDEX idx_citas_mascota        ON citas(mascota_id);
CREATE INDEX idx_citas_fecha          ON citas(fecha_hora);

-- 4) DATOS INICIALES
INSERT IGNORE INTO roles (id, nombre) VALUES
  (1,'ADMIN'), (2,'VETERINARIO'), (3,'RECEPCION');

INSERT INTO usuarios (nombre, email, pass_hash, rol_id, activo)
VALUES ('Admin', 'admin@vet.com', '$2a$10$AWk1j1jM81.ZotDiOL7VS.raqgqUVFd3WfCxqQRaEk6WXNXycx31O', 1, 1)
ON DUPLICATE KEY UPDATE email = email;

INSERT INTO cliente (id_cliente, nombre, apellido, direccion, telefono) VALUES
(1,'Carlos','Pérez','Av. Lima 123','999111222'),
(2,'María','Gómez','Jr. Arequipa 456','988777666'),
(3,'Juan','Rojas','Calle Cusco 789','955444333')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

INSERT INTO mascota (id, nombre, raza, especie, fecha_registro, fecha_nacimiento, cliente_id)
VALUES
(1,'Firulais','Labrador','Perro',  CURDATE(),                  '2020-05-10', 1),
(2,'Mishi',   'Siames',  'Gato',    CURDATE() - INTERVAL 5 DAY, '2021-08-20', 2)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

INSERT INTO citas (mascota_id, fecha_hora, motivo, estado) VALUES
(1, DATE_ADD(NOW(), INTERVAL 1 DAY), 'Vacunación anual', 'Pendiente'),
(2, DATE_ADD(NOW(), INTERVAL 2 DAY), 'Control general',  'Pendiente')
ON DUPLICATE KEY UPDATE fecha_hora = VALUES(fecha_hora);

-- 5) VISTAS
CREATE OR REPLACE VIEW v_mascotas_con_cliente AS
SELECT
  m.id                 AS mascota_id,
  m.nombre             AS mascota,
  m.especie,
  m.raza,
  m.fecha_registro,
  m.fecha_nacimiento,
  c.id_cliente         AS cliente_id,
  CONCAT(c.nombre,' ',c.apellido) AS cliente,
  c.telefono
FROM mascota m
LEFT JOIN cliente c ON c.id_cliente = m.cliente_id;

CREATE OR REPLACE VIEW v_citas_detalle AS
SELECT
  ct.id,
  ct.fecha_hora,
  ct.motivo,
  ct.estado,
  m.nombre AS mascota,
  m.especie,
  CONCAT(cli.nombre,' ',cli.apellido) AS cliente,
  cli.telefono AS tel_cliente
FROM citas ct
JOIN mascota m    ON m.id = ct.mascota_id
LEFT JOIN cliente cli ON cli.id_cliente = m.cliente_id;

-- 6) PROCEDIMIENTOS
DELIMITER $$

CREATE PROCEDURE sp_crear_cita(
  IN p_mascota_id INT,
  IN p_fecha_hora DATETIME,
  IN p_motivo     VARCHAR(255)
)
BEGIN
  IF NOT EXISTS(SELECT 1 FROM mascota WHERE id = p_mascota_id) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La mascota no existe';
  END IF;

  INSERT INTO citas(mascota_id, fecha_hora, motivo, estado)
  VALUES (p_mascota_id, p_fecha_hora, p_motivo, 'Pendiente');
END$$

CREATE PROCEDURE sp_buscar_clientes(IN p_q VARCHAR(100))
BEGIN
  SELECT * FROM cliente
  WHERE nombre   LIKE CONCAT('%', p_q, '%')
     OR apellido LIKE CONCAT('%', p_q, '%')
     OR telefono LIKE CONCAT('%', p_q, '%');
END$$

DELIMITER ;

SET FOREIGN_KEY_CHECKS = 1;

SELECT COUNT(*) AS roles    FROM roles;
SELECT id, nombre, email, activo, rol_id FROM usuarios;
SELECT COUNT(*) AS clientes FROM cliente;
SELECT COUNT(*) AS mascotas FROM mascota;
SELECT COUNT(*) AS citas    FROM citas;

SELECT * FROM usuarios;
SELECT * FROM mascota;
SELECT * FROM cliente;
SELECT * FROM citas;
