package com.tagit.repository.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.tagit.model.TagModel;

@NoRepositoryBean
interface BaseTagRepository <T extends TagModel, ID> extends JpaRepository<T, ID> {
    
    List<TagModel> findByTextContaining(String text);
    // List<FileModel> findByType(String type);
    // <FileEntity extends FileModel> FileEntity save(FileEntity fileEntity);
}

public interface TagRepository extends BaseTagRepository <TagModel, Long>, TagRepositoryCustom{

    
}
