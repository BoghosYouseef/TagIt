package com.tagit.repository.interfaces;

import com.tagit.model.FileModel;
import com.tagit.model.TagModel;

import java.time.Instant;
import java.util.List;
import java.util.Set;


public interface FileRepositoryCustom {
    FileModel saveFileRecord(
                String name,
                String size,
                String absolutePath,
                String fileType,
                String fileExtension,
                Instant fileDateLastModifeiString);

    
    // public List<FileModel> findFilesWithAllTags(
    //     Set<Long> tagIds,
    //     long tagCount);
}
