package com.tagit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tagit.TagItApp;
import com.tagit.model.TagModel;
import com.tagit.repository.interfaces.TagRepository;

import java.lang.reflect.Array;
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
    public void saveToDataBase(
                    String text,
                    String description,  
                    String colorHashString,
                    Instant createdAt){
        // TagModel tagModel = new TagModel.Builder()
        //                         .setText(text)
        //                         .setDescription(description)
        //                         .setColorHashString(colorHashString)
        //                         .build();
        
        tagRepository.saveTagModelRecord(text, description, colorHashString, createdAt);
        logger.info("saved the following tag:" + text + description + colorHashString + createdAt.toString());
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
}
