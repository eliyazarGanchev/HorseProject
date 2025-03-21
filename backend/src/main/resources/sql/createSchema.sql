CREATE TABLE IF NOT EXISTS owner
(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  description VARCHAR(4095)
);


CREATE TABLE IF NOT EXISTS horse
(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(4095),
  date_of_birth DATE NOT NULL,
  sex ENUM('MALE', 'FEMALE') NOT NULL,
  owner_id BIGINT,
  mother_id BIGINT,
  father_id BIGINT,
  image BLOB,
  image_type VARCHAR(50)
);


