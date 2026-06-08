CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    provider VARCHAR(20),
    provider_id VARCHAR(100),
    created_at DATETIME
);

CREATE TABLE IF NOT EXISTS favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content_id VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    addr VARCHAR(300),
    image VARCHAR(500),
    content_type_id VARCHAR(10),
    mapx VARCHAR(20),
    mapy VARCHAR(20),
    created_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content_id VARCHAR(50) NOT NULL,
    spot_title VARCHAR(200) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    rating INT NOT NULL,
    created_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS visit_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content_id VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    addr VARCHAR(300),
    image VARCHAR(500),
    visited_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    travel_date DATE NOT NULL,
    memo VARCHAR(500),
    created_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS schedule_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    schedule_id BIGINT NOT NULL,
    content_id VARCHAR(50),
    title VARCHAR(200) NOT NULL,
    addr VARCHAR(300),
    visit_time VARCHAR(10),
    item_order INT DEFAULT 0,
    memo VARCHAR(500),
    FOREIGN KEY (schedule_id) REFERENCES schedules(id)
);
