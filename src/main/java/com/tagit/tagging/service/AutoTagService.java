package com.tagit.tagging.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tagit.TagItApp;
import com.tagit.file.domain.FileModel;
import com.tagit.tag.domain.TagModel;
import com.tagit.tag.service.TagService;
import com.tagit.utils.ColorUtils;


@Service
public class AutoTagService {
    
    private static final Logger logger = LoggerFactory.getLogger(TagItApp.class);

    private final TagService tagService;
    private final FileTagRelationshipService fileTagRelationshipService;
    
    private AutoTagService(
        TagService tagService,
        FileTagRelationshipService fileTagRelationshipService){
        
        this.tagService = tagService;
        this.fileTagRelationshipService = fileTagRelationshipService;

    }

    public void applyPossibleTags(FileModel fileModel){
        Set<TagModel> tags = createTags(fileModel);

        tags.stream().forEach( tagModel ->
            fileTagRelationshipService.addTagToFile(fileModel, tagModel)
        );
    }

    private Set<TagModel> createTags(FileModel fileModel){
        Set<TagModel> tags = new HashSet<TagModel>();
        Optional<TagModel> extensionTagOptional = createTagFromExtension(fileModel);
        TagModel fileTypeTag = createTagFromType(fileModel);

        if (extensionTagOptional.isPresent()) {
            TagModel extensionTag = extensionTagOptional.get();
            tags.add(extensionTag);
        }

        tags.add(fileTypeTag);
        
        return tags;
    }

    private Optional<TagModel> createTagFromExtension(FileModel fileModel){

        String tagText = fileModel.getExtension();
        String colorHashString = ColorUtils.generateColorFromText(tagText);

        // logger.info("extension TagModelCreated: " + tagText + " color: " + colorHashString);

        if (tagText == null || tagText.isBlank()) {
            return Optional.empty();
        }
        TagModel extensionTag = tagService.getOrCreateTag(tagText, colorHashString);
        return Optional.of(extensionTag);
    }

    private TagModel createTagFromType(FileModel fileModel){

        String tagText = fileModel.getType();
        String colorHashString = ColorUtils.generateColorFromText(tagText);
        TagModel fileTypeTag = tagService.getOrCreateTag(tagText, colorHashString);
        return fileTypeTag;
    }




}
