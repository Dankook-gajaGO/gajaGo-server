-- 새 MySQL 볼륨이 처음 만들어질 때 한 번만 실행됨 (docker-entrypoint-initdb.d)
-- 시드 데이터가 들어갈 테이블만 미리 만든다. 나머지 테이블(user, user_request 등)은 Spring(ddl-auto=update)이 만든다.
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS food_info (
  food_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
  food_nm   VARCHAR(255) NOT NULL,
  nation_nm VARCHAR(255),
  ty_nm     VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS ingredient_info (
  ingredient_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  food_id       BIGINT,
  ingredient_nm VARCHAR(255),
  ty_nm         VARCHAR(255),
  FOREIGN KEY (food_id) REFERENCES food_info (food_id)
);

CREATE TABLE IF NOT EXISTS allergy_mapping (
  mapping_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
  allergy_category VARCHAR(255) NOT NULL,
  ingredient_nm    VARCHAR(255) NOT NULL
);

-- 축제 데이터는 TourAPI 수집 스크립트로 채운다 (overview는 길어서 TEXT)
CREATE TABLE IF NOT EXISTS festival_info (
  contentid      INT PRIMARY KEY AUTO_INCREMENT,
  title          VARCHAR(255),
  addr1          VARCHAR(255),
  zipcode        INT,
  eventstartdate DATE,
  eventenddate   DATE,
  image          VARCHAR(500),
  tel            VARCHAR(255),
  fee            TEXT,
  eventhomepage  TEXT,
  overview       TEXT
);
