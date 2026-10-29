package com.tagit.tag.repository;

import java.time.Instant;

import com.tagit.tag.domain.TagModel;


public interface TagRepositoryCustom {
    TagModel saveTagModelRecord (
                String text,
                String description,
                String colorHashString,
                Instant createdAt);
}