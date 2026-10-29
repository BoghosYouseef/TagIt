package com.tagit.tagging.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tagit.TagItApp;
import com.tagit.file.domain.FileModel;
import com.tagit.tag.domain.TagModel;
import com.tagit.tagging.repository.FileTagRepository;

@Service
public class FileTagRelationshipService {
    private static final Logger logger = LoggerFactory.getLogger(TagItApp.class);

    private FileTagRepository fileTagRepository;

    public FileTagRelationshipService(
        FileTagRepository fileTagRepository
    ){
        this.fileTagRepository = fileTagRepository;
    }

    @Transactional
    public void addTagToFile(FileModel fileModel, TagModel tagModel){
        fileTagRepository.saveFileTagRelationship(fileModel, tagModel);
    };

    
    @Transactional
    public void purgeRelationshipsForTag(TagModel tag) {
        fileTagRepository.deleteAllByTag(tag);
        logger.info("Successfully dropped all file mappings for tag ID: {}", tag.getId());
    }

}
