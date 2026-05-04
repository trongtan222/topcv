CREATE DATABASE IF NOT EXISTS form_manager
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE form_manager;

CREATE USER IF NOT EXISTS 'form_user'@'localhost' IDENTIFIED BY 'form_password';
GRANT ALL PRIVILEGES ON form_manager.* TO 'form_user'@'localhost';
FLUSH PRIVILEGES;
