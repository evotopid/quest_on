CREATE TABLE IF NOT EXISTS surveys (
    id VARCHAR(64) PRIMARY KEY,
    admin_id INT,
    data TEXT
);

CREATE TABLE IF NOT EXISTS admins (
    id INTEGER PRIMARY KEY,
    email TEXT,
    password_salt TEXT,
    password_hash TEXT
);

CREATE TABLE IF NOT EXISTS results (
    id INTEGER PRIMARY KEY,
    survey_id VARCHAR(64),
    admin_id INT,
    submitted_at TIMESTAMP WITH TIME ZONE
);
