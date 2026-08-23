CREATE TABLE IF NOT EXISTS files (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    size INTEGER,
    absolute_path TEXT NOT NULL,
    file_type TEXT NOT NULL,
    file_extension TEXT,
    last_modified_at DATETIME NOT NULL )