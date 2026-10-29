package com.tagit.tag.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import com.tagit.tag.domain.TagModel;

@NoRepositoryBean
interface BaseTagRepository <T extends TagModel, ID> extends JpaRepository<T, ID> {
    
    List<TagModel> findByTextContaining(String text);
    Boolean existsByTextAndColorHashString(String text, String colorHashString);
    Optional<TagModel> findByTextAndColorHashString(String text, String colorHashString);
    Boolean existsByTextAndColorHashStringAndIdNot(String text, String colorHashString, Long id);
    // List<FileModel> findByType(String type);
    // <FileEntity extends FileModel> FileEntity save(FileEntity fileEntity);
}

public interface TagRepository extends BaseTagRepository <TagModel, Long>, TagRepositoryCustom{

    
}
