CREATE DATABASE IF NOT EXISTS bd_techservices CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bd_techservices;
CREATE TABLE IF NOT EXISTS usuarios_api (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    api_key_hash VARCHAR(100) NOT NULL,
    token_hash VARCHAR(100) NOT NULL,
    tokens_disponibles INT NOT NULL DEFAULT 0,
    estado BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni VARCHAR(20) NOT NULL UNIQUE,
    correo VARCHAR(150) NOT NULL,
    telefono VARCHAR(30) NOT NULL
);
-- Usuario 1: MI_API_KEY_2026 / TOKEN_SEGURO_123456 / saldo 10
-- Usuario 2: API_KEY_SEGURA_2_2026 / TOKEN_SEGUNDO_654321 / saldo 2
INSERT INTO usuarios_api (nombre, api_key_hash, token_hash, tokens_disponibles, estado) VALUES
('Integración principal', '$2a$10$Z1nZmY5t8Tnc9ghTPZYg0uZ/zQG205ZzWUdOeyfVlkXxZbPHvSWce', '$2a$10$A67fFLP5Wh4rGNLCsBAyQOeB25Q9I3728tE04kU/qW8wzsfTkVs5K', 10, TRUE),
('Integración secundaria', '$2a$10$445k8l62J6KrH6eTfXfvrevHCZiJkaEqjKQ1HQjgPzn5DowE0Dx0.', '$2a$10$9vlLwPMQVs6VuxRZqTahA.fLJf7.KS.7and.lJiB4cJ.yrB6NeDnq', 2, TRUE);
INSERT INTO clientes (nombres, apellidos, dni, correo, telefono) VALUES
('Ana', 'Torres Flores', '70000001', 'ana.torres@example.com', '999111111'),
('Luis', 'Quispe Rojas', '70000002', 'luis.quispe@example.com', '999222222'),
('María', 'García Paredes', '70000003', 'maria.garcia@example.com', '999333333'),
('Carlos', 'Mendoza Díaz', '70000004', 'carlos.mendoza@example.com', '999444444'),
('Sofía', 'Vargas León', '70000005', 'sofia.vargas@example.com', '999555555');
