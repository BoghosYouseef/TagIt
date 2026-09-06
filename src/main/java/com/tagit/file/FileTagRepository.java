package com.tagit.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.tagit.file.domain.FileModel;
import com.tagit.file.domain.FileTag;
import com.tagit.tag.TagModel;

@NoRepositoryBean
interface BaseFileTagRepository <T extends FileTag, ID> extends JpaRepository<T, ID> {
    boolean existsByFileAndTag(FileModel file, TagModel tag);
}

public interface FileTagRepository extends BaseFileTagRepository <FileTag, Long>, FileTagRepositoryCustom {
}