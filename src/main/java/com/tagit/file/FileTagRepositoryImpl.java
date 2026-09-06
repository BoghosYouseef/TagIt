package com.tagit.file;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tagit.file.domain.FileModel;
import com.tagit.file.domain.FileTag;
import com.tagit.tag.TagModel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Repository
public class FileTagRepositoryImpl implements FileTagRepositoryCustom{
    

    @PersistenceContext
    private EntityManager entityManager;

    
    public void save(FileTag fileTag) {
        entityManager.persist(fileTag);
    }

    @Override
    public FileTag saveFileTagRelationship(FileModel file, TagModel tag){
        FileTag fileTag = new FileTag();
        fileTag.setFile(file);
        fileTag.setTag(tag);
        save(fileTag);
        return fileTag;
    };
}
