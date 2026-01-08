package com.tagit.service;

import java.util.Date;
import java.util.List;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tagit.TagItApp;
import com.tagit.repository.interfaces.FileRepository;

@Service
public class FileService {
    private static final Logger logger = LoggerFactory.getLogger(TagItApp.class);

    @Autowired
    private FileRepository fileRepository;

    public void fileService(List <File> filesToHandle) {
        logger.info("FileService initialized");
        logger.info("Number of files to handle: " + filesToHandle.size());
        logger.info("Files imported: " + filesToHandle.toString());

        for (File file : filesToHandle) {
            getFileType(file);
            getFileName(file);
            getFileSize(file);
            getLastModifiedDateFromFile(file);
        }
    }

    private String getFileType(File file) {
        logger.info("Determining file type for: " + file.getAbsolutePath());
        String fileAbsPath = file.getAbsolutePath();
        Path path = Paths.get(fileAbsPath);

        try {
            String fileType = Files.probeContentType(path);
            logger.info("file Type: " + fileType);
            return fileType;
        } catch (Exception e) {
            logger.error("Error determining file type for: " + fileAbsPath, e);
            return "unknown";
        }
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

    private String getLastModifiedDateFromFile(File file) {
        String dateAsText = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                          .format(new Date(file.lastModified()));
        logger.info("file Last Modified: " + dateAsText);
        return dateAsText;
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

    // private void saveFileRecordToDatabase(
    //     String name,
    //     String size,
    //     String absolutePath,
    //     String type) {
            
    //         fileRepository.saveFileRecord(
    //             name,
    //             size,
    //             absolutePath,
    //             type);
    //     }

}
