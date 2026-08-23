package com.tagit.repository.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.tagit.model.FileModel;
import com.tagit.model.FileTag;
import com.tagit.model.TagModel;

@NoRepositoryBean
interface BaseFileTagRepository <T extends FileTag, ID> extends JpaRepository<T, ID> {
    boolean existsByFileAndTag(FileModel file, TagModel tag);
}

public interface FileTagRepository extends BaseFileTagRepository <FileTag, Long>, FileTagRepositoryCustom {
}