package com.tagit.repository.implementation;

import java.time.Instant;

import org.springframework.stereotype.Repository;

import com.tagit.model.FileModel;
import com.tagit.model.TagModel;
import com.tagit.repository.interfaces.TagRepository;
import com.tagit.repository.interfaces.TagRepositoryCustom;

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
