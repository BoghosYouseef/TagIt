package com.tagit.file;

import com.tagit.file.domain.FileModel;
import com.tagit.tag.TagModel;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Set;


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
