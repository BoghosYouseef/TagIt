package com.tagit.tag;

import java.sql.SQLException;
import java.time.Instant;


public interface TagRepositoryCustom {
    TagModel saveTagModelRecord (
                String text,
                String description,
                String colorHashString,
                Instant createdAt);
}