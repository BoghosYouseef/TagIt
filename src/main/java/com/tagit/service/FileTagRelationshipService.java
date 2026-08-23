package com.tagit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.tagit.TagItApp;
import com.tagit.model.FileModel;
import com.tagit.model.TagModel;
import com.tagit.repository.interfaces.FileTagRepository;

@Service
public class FileTagRelationshipService {
    private static final Logger logger = LoggerFactory.getLogger(TagItApp.class);

    @Autowired
    private FileTagRepository fileTagRepository;

    @Transactional
    public void addTagToFile(FileModel fileModel, TagModel tagModel){
        fileTagRepository.saveFileTagRelationship(fileModel, tagModel);
    };
}
