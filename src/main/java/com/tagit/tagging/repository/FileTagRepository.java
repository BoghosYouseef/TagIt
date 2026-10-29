package com.tagit.tagging.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import com.tagit.file.domain.FileModel;
import com.tagit.file.domain.FileTag;
import com.tagit.tag.domain.TagModel;

@NoRepositoryBean
interface BaseFileTagRepository <T extends FileTag, ID> extends JpaRepository<T, ID> {
    boolean existsByFileAndTag(FileModel file, TagModel tag);
    Optional<FileTag> findByFileAndTag(FileModel file, TagModel tag);

    @Modifying 
    @Query("DELETE FROM FileTag ft WHERE ft.tag = :tag")
    void deleteAllByTag(@Param("tag") TagModel tag);

}

public interface FileTagRepository extends BaseFileTagRepository <FileTag, Long>, FileTagRepositoryCustom {
}