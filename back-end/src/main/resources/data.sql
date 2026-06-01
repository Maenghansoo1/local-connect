-- 데이터베이스 생성 (이미 있으면 무시)
CREATE DATABASE IF NOT EXISTS `local-connect` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 데이터베이스 사용
USE `local-connect`;

-- users 테이블 생성
CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(255) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `nickname` VARCHAR(255) NOT NULL UNIQUE,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- user_schedule 테이블 생성 (내 일정 저장)
CREATE TABLE IF NOT EXISTS `user_schedule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `event_id` BIGINT NOT NULL,
  `event_title` VARCHAR(255),
  `start_date` VARCHAR(8),
  `end_date` VARCHAR(8),
  `region` VARCHAR(50),
  `image_url` VARCHAR(1000),
  `saved_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- event_translation 테이블 생성 (영문 번역 저장)
CREATE TABLE IF NOT EXISTS `event_translation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `content_id` VARCHAR(255) NOT NULL,
  `language` VARCHAR(10) NOT NULL,
  `title` VARCHAR(255),
  PRIMARY KEY (`id`),
  UNIQUE KEY uq_content_lang (`content_id`, `language`),
  INDEX idx_content_id (`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- event 테이블 생성
CREATE TABLE IF NOT EXISTS `event` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `content_id` VARCHAR(255) NOT NULL UNIQUE,
  `title` VARCHAR(255),
  `start_date` VARCHAR(8),
  `end_date` VARCHAR(8),
  `address` VARCHAR(500),
  `region` VARCHAR(50),
  `image_url` VARCHAR(1000),
  `tel` VARCHAR(50),
  `latitude` DOUBLE,
  `longitude` DOUBLE,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX idx_content_id (content_id),
  INDEX idx_region (region),
  INDEX idx_title (title)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
