package com.tagit.tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tagit.TagItApp;
import com.tagit.exception.DuplicateTagException;
import com.tagit.exception.TagPersistenceException;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TagService {
    
    private static final Logger logger = LoggerFactory.getLogger(TagItApp.class);
    
    @Autowired
    private TagRepository tagRepository;

    public TagService(){
        logger.info("TagService initialized");
    }

    
    @Transactional
    public void saveToDataBase (
                    String text,
                    String description,  
                    String colorHashString,
                    Instant createdAt) {
        // TagModel tagModel = new TagModel.Builder()
        //                         .setText(text)
        //                         .setDescription(description)
        //                         .setColorHashString(colorHashString)
        //                         .build();
        try {
            tagRepository.saveTagModelRecord(
                text, 
                description, 
                colorHashString, 
                createdAt);

            logger.info("saved the following tag:" + text + description + colorHashString + createdAt.toString());
        
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
