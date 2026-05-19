DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS reservation_time;
DROP TABLE IF EXISTS theme;
DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    name          VARCHAR(255) NOT NULL,
    login_id      VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(500) NOT NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'USER',
    PRIMARY KEY (id)
);

CREATE TABLE theme
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    name          VARCHAR(255) NOT NULL,
    thumbnail_url VARCHAR(255),
    description   VARCHAR(500),
    PRIMARY KEY (id)
);


CREATE TABLE reservation_time
(
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    start_at TIME NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE reservation
(
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    date     DATE NOT NULL,
    users_id BIGINT       NOT NULL,
    time_id  BIGINT       NOT NULL,
    theme_id BIGINT       NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (users_id) REFERENCES users (id) ON DELETE RESTRICT,
    FOREIGN KEY (time_id) REFERENCES reservation_time (id) ON DELETE RESTRICT,
    FOREIGN KEY (theme_id) REFERENCES theme (id) ON DELETE RESTRICT
);
