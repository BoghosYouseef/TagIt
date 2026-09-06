package com.tagit.file;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.tagit.TagItApp;
import com.tagit.file.domain.FileModel;

import jakarta.annotation.PostConstruct;

@Service
public class FileService {
    private static final Logger logger = LoggerFactory.getLogger(TagItApp.class);
    
    @Autowired
    private FileRepository fileRepository;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    public FileService(){
        logger.info("FileService initialized");
    }
    
    public void extractInfoAndSaveToDataBase(File fileToHandle) {

        logger.info("File imported: " + fileToHandle.toString());
        
        String fileTypeString = getFileType(fileToHandle);
        String fileExtensionString = getFileExtensionInLowerCase(fileToHandle.getName());
        String fileNameString = getFileName(fileToHandle);
        String fileSizeString = getFileSize(fileToHandle);
        
        long fileLastModifiedAtAsLong = fileToHandle.lastModified(); // Returns a primitive long
        Instant fileLastModifiedAtInstant = Instant.ofEpochMilli(fileLastModifiedAtAsLong); 

        String fileAbsPathString = getFileAbsolutePathAsString(fileToHandle);
        FileModel fileModel = fileRepository.saveFileRecord(
                                            fileNameString,
                                                fileSizeString,
                                                fileAbsPathString,
                                                fileTypeString,
                                                fileExtensionString,
                                            fileLastModifiedAtInstant);            
        logger.info("File saved: " + fileModel.getName());
    }

    public void extractInfoAndSaveToDataBase(List <File> filesToHandle) {

        
        logger.info("Number of files to handle: " + String.valueOf(filesToHandle.size()));

        filesToHandle.stream()
                     .forEach(this::extractInfoAndSaveToDataBase);;

    }

    public List<FileModel> retrieveAllFilesAsObjects(){

        // List<FileModel> files = fileRepository.findAllWithTags();
        List<FileModel> files = fileRepository.findAll();

        logger.info("Repository returned {}", files.size());
        // files.stream().forEach(file -> {
            // logger.info("FILE: " + file.getName() + " has the following tags:");
            // file.getTags().forEach(tag -> 
                // logger.info("tag: " + tag.getText())
            // );
        // });
        return files;
    }

    public List<FileModel> retrieveTagFilteredFilesAsObjects(Set<Long> tagIds){

        // List<FileModel> files = fileRepository.findAllWithTags();
        List<FileModel> files = fileRepository.findFilesWithAllTags(tagIds, tagIds.size());

        // logger.info("Repository returned {}", files.size());
        // files.stream().forEach(file -> {
        //     // logger.info("FILE: " + file.getName() + " has the following tags:");
        //     file.getTags().forEach(tag -> 
        //         // logger.info("tag: " + tag.getText())
        //     );
        // });
        return files;
    }

    
    private String getFileType(File file){
        logger.info("Determining file type for: " + file.getAbsolutePath());
        String fileAbsPath = file.getAbsolutePath();
        Path path = Paths.get(fileAbsPath);
        String rawFileType = "unkown/unkown";
        try {
            String fileType = Files.probeContentType(path);
            logger.info("file Type: " + fileType);
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
    

    private String getFileExtensionInLowerCase(String fileName) {
        int lastDot = fileName.lastIndexOf('.');

        if (lastDot <= 0 || lastDot == fileName.length() - 1) {
            return null;
        }

        return fileName.substring(lastDot + 1).toLowerCase(Locale.ROOT);
    }

    private String getFileName(File file) {
        logger.info("file Name: " + file.getName());
        String fileName = file.getName();
        return fileName;
    }

    private String getFileSize(File file) {
        logger.info("file Size: " + file.length());
        logger.info("OR : " + convertBytesToString(file.length()));
        return convertBytesToString(file.length());
    }

    private String getLastModifiedDateFromFileAsString(File file) {
        Instant instant = Instant.ofEpochMilli(file.lastModified());
        
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("dd-MM-yyyy HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        String dateAsString = formatter.format(instant);
        
        logger.info("file Last Modified: " + dateAsString);
        return dateAsString;
    }

    private String getFileAbsolutePathAsString(File file) {
        String absPathAsStr = file.getAbsolutePath();
        logger.info("file abs path: " + absPathAsStr);
        return absPathAsStr;
    }

    private String convertBytesToString(long bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("Byte value cannot be negative");
        }
        
        String unit;
        double value; 

        if (bytes >= 1_073_741_824) { // 1 GB
            value = bytes / 1_073_741_824.0;
            unit = "GB";
        } else if (bytes >= 1_048_576) { // 1 MB
            value = bytes / 1_048_576.0;
            unit = "MB";
        } else if (bytes >= 1_024) { // 1 KB
            value = bytes / 1_024.0;
            unit = "KB";
        } else {
            value = bytes;
            unit = "bytes";
        }
        
        return String.format("%.2f %s", value, unit);
    }
}
