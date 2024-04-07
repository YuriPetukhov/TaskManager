-- liquibase formatted sql

-- changeset YuriPetukhov:1

CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(20) NOT NULL,
    description VARCHAR(255),
    due_date TIMESTAMP NOT NULL,
    completed BOOLEAN NOT NULL
);