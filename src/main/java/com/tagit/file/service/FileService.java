package com.tagit.file.service;

import java.util.List;
import java.util.Set;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import java.time.Instant;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import com.tagit.TagItApp;
import com.tagit.file.FileMetaDataUtils;
import com.tagit.file.domain.FileModel;
import com.tagit.file.explorer.FileExplorerFactory;
import com.tagit.file.repository.FileRepository;
import com.tagit.tagging.service.AutoTagService;


@Service
public class FileService {
    private static final Logger logger = LoggerFactory.getLogger(TagItApp.class);
    
    
    private FileRepository fileRepository;
    private AutoTagService autoTagService;
    
    
    public FileService(
        FileRepository fileRepository,
        AutoTagService autoTagService
    ){
        this.fileRepository = fileRepository;
        this.autoTagService = autoTagService;
        logger.info("FileService initialized");
    }
    
    public void extractInfoAndSaveToDataBase(File fileToHandle) {

        // logger.info("File imported: " + fileToHandle.toString());
        
        long fileLastModifiedAtAsLong = fileToHandle.lastModified(); // Returns a primitive long
        Path fileAbsPathString = fileToHandle.toPath();
        String fileNameString = fileToHandle.getName();

        String fileTypeString = getFileType(fileToHandle);
        String fileSizeString = FileMetaDataUtils.formatSize(fileToHandle.length());
        String fileExtensionString = FileMetaDataUtils.getExtensionOf(fileNameString);
        
        Instant fileLastModifiedAtInstant = Instant.ofEpochMilli(fileLastModifiedAtAsLong); 

        FileModel fileModel = fileRepository.saveFileRecord(
                                            fileNameString,
                                                fileSizeString,
                                                fileAbsPathString,
                                                fileTypeString,
                                                fileExtensionString,
                                            fileLastModifiedAtInstant);            
        // logger.info("File saved: " + fileModel.getName());

        autoTagService.applyPossibleTags(fileModel);
    }

    public void extractInfoAndSaveToDataBase(List <File> filesToHandle) {

        
        logger.info("Number of files to handle: " + String.valueOf(filesToHandle.size()));

        filesToHandle.stream()
                     .forEach(this::extractInfoAndSaveToDataBase);;

    }

    public List<FileModel> retrieveAllFilesAsObjects(){

        List<FileModel> files = fileRepository.findAll();

        // logger.info("Repository returned {}", files.size());
        return files;
    }

    public List<FileModel> retrieveTagFilteredFilesAsObjects(Set<Long> tagIds){

        List<FileModel> files = fileRepository.findFilesWithAllTags(tagIds, tagIds.size());
        return files;
    }

    public void openAndSelectFile(Path filePath) {
        
        if (filePath == null || !Files.exists(filePath)) {
            throw new IllegalArgumentException("Cannot open file: File does not exist on disk.");
        }

        // Delegate to the platform-specific explorer
        FileExplorerFactory.create().openAndSelectFile(filePath);
    }

    
    private String getFileType(File file){
        // logger.info("Determining file type for: " + file.getAbsolutePath());
        String fileAbsPath = file.getAbsolutePath();
        Path path = Paths.get(fileAbsPath);
        String rawFileType = "unkown/unkown";
        try {
            String fileType = Files.probeContentType(path);
            // logger.info("file Type: " + fileType);
            if(fileType != null){
                rawFileType = fileType;
            }
        } catch (Exception e) {
            logger.error("Error determining file type for: " + fileAbsPath, e);
        }

        String regex = "[/]";
        String[] fileType = rawFileType.split(regex);
        return fileType[0];
    }
}
