CREATE TABLE IF NOT EXISTS tags (
    id INTEGER PRIMARY KEY AUTOINCREMENT,

    text TEXT NOT NULL,

    description TEXT,

    color_hash_string TEXT NOT NULL,

    created_at DATETIME NOT NULL,

    CONSTRAINT uk_tags_text_color UNIQUE (text, color_hash_string)
);
