package com.tagit.file.repository;

import com.tagit.file.domain.FileModel;

import java.nio.file.Path;
import java.time.Instant;


public interface FileRepositoryCustom {
    FileModel saveFileRecord(
                String name,
                String size,
                Path absolutePath,
                String fileType,
                String fileExtension,
                Instant fileDateLastModifeiString);

    
    // public List<FileModel> findFilesWithAllTags(
    //     Set<Long> tagIds,
    //     long tagCount);
}
