CREATE TABLE IF NOT EXISTS tags (
    id INTEGER PRIMARY KEY AUTOINCREMENT,

    text TEXT NOT NULL,

    description TEXT,

    color_hash_string TEXT,

    created_at DATETIME NOT NULL
);