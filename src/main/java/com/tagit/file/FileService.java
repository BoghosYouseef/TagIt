package com.tagit.file;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


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
import com.tagit.file.explorer.FileExplorerFactory;


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

        Path fileAbsPathString = getFileAbsolutePathAsPathObject(fileToHandle);
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

        List<FileModel> files = fileRepository.findAll();

        logger.info("Repository returned {}", files.size());
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
        String fileName = file.getName();
        return fileName;
    }

    private String getFileSize(File file) {
        return convertBytesToString(file.length());
    }

    private String getFileAbsolutePathAsString(File file) {
        String absPathAsStr = file.getAbsolutePath();
        return absPathAsStr;
    }

    private Path getFileAbsolutePathAsPathObject(File file) {
        Path absPathAsPathObj = Path.of(file.getAbsolutePath());
        return absPathAsPathObj;
    }

    private String convertBytesToString(long bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("Byte value cannot be negative");
        }
        if (bytes < 1024) {
            return bytes + " bytes"; // Simple shortcut for small numbers
        }

        String[] units = {"bytes", "KB", "MB", "GB", "TB", "PB"};
        
        // Math.log calculation figures out which unit we need (KB, MB, GB, etc.)
        int unitIndex = (int) (Math.log(bytes) / Math.log(1024));
        double value = bytes / Math.pow(1024, unitIndex);

        return String.format("%.2f %s", value, units[unitIndex]);
    }
}
