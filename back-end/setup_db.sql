-- MariaDB 데이터베이스 초기화 스크립트

-- 1. 기존 데이터베이스 삭제 (테스트용)
DROP DATABASE IF EXISTS `local-connect`;

-- 2. 데이터베이스 생성
CREATE DATABASE `local-connect` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 3. 사용자 권한 부여
GRANT ALL PRIVILEGES ON `local-connect`.* TO 'root'@'localhost';
FLUSH PRIVILEGES;

-- 4. 데이터베이스 사용
USE `local-connect`;

-- 완료
SELECT 'Database setup complete!' as result;
