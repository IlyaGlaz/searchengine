-- liquibase formatted sql

-- changeset iliaglaz:1
CREATE TABLE IF NOT EXISTS site
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    status      VARCHAR(32)  NOT NULL,
    status_time DATETIME     NOT NULL,
    last_error  TEXT,
    url         VARCHAR(255) NOT NULL,
    name        VARCHAR(64)  NOT NULL
);

-- changeset iliaglaz:2
CREATE TABLE IF NOT EXISTS page
(
    id      INT PRIMARY KEY AUTO_INCREMENT,
    site_id INT        NOT NULL,
    path    TEXT       NOT NULL,
    code    INT        NOT NULL,
    content MEDIUMTEXT NOT NULL,
    FULLTEXT INDEX path_idx (path),
    FOREIGN KEY (site_id) REFERENCES site (id)
);