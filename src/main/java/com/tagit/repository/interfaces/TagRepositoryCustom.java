package com.tagit.repository.interfaces;

import java.time.Instant;

import com.tagit.model.TagModel;


public interface TagRepositoryCustom {
    TagModel saveTagModelRecord(
                String text,
                String description,
                String colorHashString,
                Instant createdAt);
}