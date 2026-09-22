CREATE TABLE workouts (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id           BIGINT        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    sport             VARCHAR(20)   NOT NULL,
    date              DATE          NOT NULL,
    duration_minutes  INTEGER       NOT NULL CHECK (duration_minutes BETWEEN 1 AND 600),
    distance_km       NUMERIC(6, 2)          CHECK (distance_km >= 0),
    effort            INTEGER       NOT NULL CHECK (effort BETWEEN 1 AND 10),
    calories          INTEGER       NOT NULL,
    notes             VARCHAR(500)
);

-- La requête la plus fréquente : « mes séances, les plus récentes d'abord ».
CREATE INDEX workouts_user_date_idx ON workouts (user_id, date DESC);
