package com.tagit.tag.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tagit.TagItApp;
import com.tagit.exception.DuplicateTagException;
import com.tagit.exception.TagPersistenceException;
import com.tagit.tag.domain.TagModel;
import com.tagit.tag.event.ExistingTagDeletedEvent;
import com.tagit.tag.event.ExistingTagModifiedEvent;
import com.tagit.tag.event.NewTagCreatedEvent;
import com.tagit.tag.repository.TagRepository;
import com.tagit.tagging.service.FileTagRelationshipService;

import jakarta.persistence.EntityNotFoundException;

import java.time.Instant;
import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TagService {
    
    private static final Logger logger = LoggerFactory.getLogger(TagItApp.class);
    private TagRepository tagRepository;
    private FileTagRelationshipService fileTagRelationshipService;
    private final ApplicationEventPublisher eventPublisher;

    public TagService(
        TagRepository tagRepository,
        ApplicationEventPublisher eventPublisher,
        FileTagRelationshipService fileTagRelationshipService){
        logger.info("TagService initialized");
        this.tagRepository = tagRepository;
        this.eventPublisher = eventPublisher;
        this.fileTagRelationshipService = fileTagRelationshipService;
    }

    
    @Transactional
    public TagModel saveToDataBase (
                    String text,
                    String description,  
                    String colorHashString,
                    Instant createdAt) {

        try {

            return tagRepository.findByTextAndColorHashString(text, colorHashString)
            .orElseGet(() -> {
                
                TagModel newTag = tagRepository.saveTagModelRecord(text, description, colorHashString, createdAt);
                eventPublisher.publishEvent(new NewTagCreatedEvent(newTag));
                return newTag;
            });
        
        } catch (JpaSystemException e) {
            if (isDuplicateTagException(e)) {
                throw new DuplicateTagException(
                    "A tag with the same text and color already exists.",
                    e
                );
            }

            throw new TagPersistenceException(
                "Could not save tag.",
                e
            );
        }
    }

    public List<TagModel> getAllTags(){
        logger.info("INSIDE getAllTags()!!");
        List<TagModel> allTags = tagRepository.findAll();
        logger.info("alltags.size(): " + allTags.size());
        logger.info("[TagService.getAllTags] allTags: ");
        allTags.stream().forEach(tag -> logger.info(tag.getText() + "\n"));
        return allTags;
    };

    public List<TagModel> findTagsMatching(String matchingString){
         return tagRepository.findByTextContaining(matchingString);
    }

    @Transactional 
    public TagModel getOrCreateTag(
        String tagText,
        String colorHashString
    ){
        Instant createdAt = Instant.now();
        return saveToDataBase(tagText, tagText, colorHashString, createdAt);
    };

     
    public TagModel getTagbyId(
        Long id
    ){
        return tagRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Tag not found with ID: " + id));
    }

    public TagModel editTag(
        String existingTagText,
        String existingTagDescription,
        String existingColorHashString,
        String newTagText,
        String newTagDescription,
        String newColorHashString
    ){
        // 1. Locate the exact record using its unique current Text + Color combination
        TagModel existingTag = tagRepository.findByTextAndColorHashString(existingTagText, existingColorHashString)
            .orElseThrow(() -> new EntityNotFoundException(
                String.format("Could not find original tag with text '%s' and color '%s'", existingTagText, existingColorHashString)
            ));

        // 2. Business Rule: Check if the NEW combination already exists on a DIFFERENT tag
        // This uses a repository method that explicitly checks for text AND color, excluding this tag's ID.
        boolean duplicateExists = tagRepository.existsByTextAndColorHashStringAndIdNot(
            newTagText, 
            newColorHashString, 
            existingTag.getId()
        );
        
        if (duplicateExists) {
            throw new DuplicateTagException("A tag with this text and color combination already exists!");
        }

        // 3. Update the fields safely
        existingTag.setText(newTagText);
        existingTag.setDescription(newTagDescription);
        existingTag.setColorHashString(newColorHashString);

        // 4. Save back to SQLite
        TagModel updatedTag = tagRepository.save(existingTag);
        eventPublisher.publishEvent(new ExistingTagModifiedEvent(existingTag));

        return updatedTag;

    }

    @SuppressWarnings("null")
    public void deleteTag(
        String tagText,
        String colorHashString
    ){
        tagRepository.findByTextAndColorHashString(tagText, colorHashString)
            .ifPresentOrElse(
                existingTag -> {
                    fileTagRelationshipService.purgeRelationshipsForTag(existingTag);
                    tagRepository.delete(existingTag);
                    eventPublisher.publishEvent(new ExistingTagDeletedEvent(existingTag));
                },
                () -> { 
                    throw new IllegalArgumentException("Tag not found"); 
                }
            );
    }

    private boolean isDuplicateTagException(Throwable exception) {
        Throwable current = exception;

        while (current != null) {
            String message = current.getMessage();

            if (message != null
                    && message.contains("UNIQUE constraint failed: tags.text, tags.color_hash_string")) {
                return true;
            }

            current = current.getCause();
        }

        return false;
    }
}
