package com.tagit.repository.implementation;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tagit.model.FileModel;
import com.tagit.model.TagModel;
import com.tagit.repository.interfaces.FileRepositoryCustom;

import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Repository
public class FileRepositoryImpl implements FileRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    
    public void save(FileModel file) {
        entityManager.persist(file);
    }

    // @Override
    // public List<FileModel> findByName(String name) {
    //     String query = "SELECT f FROM FileModel f WHERE f.name = :name";
    //     return entityManager.createQuery(query, FileModel.class)
    //                         .setParameter("name", name)
    //                         .getResultList();
    // }

    // @Override
    // public List<FileModel> findByType(String type) {
    //     String query = "SELECT f FROM FileModel f WHERE f.type = :type";
    //     return entityManager.createQuery(query, FileModel.class)
    //                         .setParameter("type", type)
    //                         .getResultList();
    // }

    @Override
    @Transactional
    public FileModel saveFileRecord(
        String name,
        String size,
        String absolutePath,
        String fileType,
        String fileExtension,
        Instant lastModifiedAt) {

        FileModel file = new FileModel();
        file.setName(name);
        file.setSize(size);
        file.setAbsolutePath(absolutePath);
        file.setType(fileType);
        file.setExtension(fileExtension);
        file.setLastModifiedAt(lastModifiedAt);
        // entityManager.persist(file);
        save(file);

        return file;
    }



    
}