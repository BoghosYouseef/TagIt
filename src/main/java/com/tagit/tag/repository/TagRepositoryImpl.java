package com.tagit.tag.repository;

import java.time.Instant;

import org.springframework.stereotype.Repository;

import com.tagit.tag.domain.TagModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class TagRepositoryImpl implements TagRepositoryCustom{
    
    @PersistenceContext
    private EntityManager entityManager;

    
    public void save(TagModel tag) {
        entityManager.persist(tag);
    }

    
    @Override
    public TagModel saveTagModelRecord(
        String text,
        String description,
        String colorHashString,
        Instant createdAt) {

        TagModel tagModel = new TagModel.Builder()
                        .setText(text)
                        .setDescription(description)
                        .setColorHashString(colorHashString)
                        .setCreatedAt(createdAt)
                        .build();
        save(tagModel);

        return tagModel;
    }
}
