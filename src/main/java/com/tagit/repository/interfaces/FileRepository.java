package com.tagit.repository.interfaces;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import com.tagit.model.FileModel;

@NoRepositoryBean
interface BaseFileRepository <T extends FileModel, ID> extends JpaRepository<T, ID> {
    
    List<FileModel> findByNameContaining(String name);
    // List<FileModel> findByType(String type);
    // <FileEntity extends FileModel> FileEntity save(FileEntity fileEntity);
    @Query("""
        SELECT DISTINCT files
        FROM FileModel files
        LEFT JOIN FETCH files.fileTags file_tags
        LEFT JOIN FETCH file_tags.tag
        """)
    List<FileModel> findAllWithTags();

    @Query("""
        SELECT f
        FROM FileModel f
        JOIN f.fileTags ft
        WHERE ft.tag.id IN :tagIds
        GROUP BY f
        HAVING COUNT(DISTINCT ft.tag.id) = :tagCount
    """)
    List<FileModel> findFilesWithAllTags(
        @Param("tagIds") Set<Long> tagIds,
        @Param("tagCount") long tagCount
    );
    
}




public interface FileRepository extends BaseFileRepository <FileModel, Long>, FileRepositoryCustom {
    // FileModel saveFileRecord(String name, String size, String absolutePath, String type);
}