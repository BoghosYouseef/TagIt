CREATE TABLE IF NOT EXISTS file_tags (
    id INTEGER PRIMARY KEY AUTOINCREMENT,

    file_id INTEGER NOT NULL,

    tag_id INTEGER NOT NULL,

    CONSTRAINT fk_file_tags_file
        FOREIGN KEY (file_id)
        REFERENCES files(id),

    CONSTRAINT fk_file_tags_tag
        FOREIGN KEY (tag_id)
        REFERENCES tags(id),

    CONSTRAINT uq_file_tag
        UNIQUE (file_id, tag_id)
);