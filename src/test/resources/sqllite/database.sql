DROP TABLE IF EXISTS users;

CREATE TABLE `users` (
  `id`               INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  `username`         VARCHAR(255)       DEFAULT NULL,
  `active`           TINYINT(1)         DEFAULT '1',
  `salary`           DECIMAL(7, 2)      DEFAULT NULL,
  `comments`         TEXT,
  `last_access_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
