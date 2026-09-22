CREATE TABLE users (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email                VARCHAR(254) NOT NULL UNIQUE,
    password_hash        VARCHAR(100) NOT NULL,
    display_name         VARCHAR(60)  NOT NULL,
    weight_kg            NUMERIC(4, 1) NOT NULL DEFAULT 70.0,
    weekly_goal_minutes  INTEGER      NOT NULL DEFAULT 150,
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT now()
);
