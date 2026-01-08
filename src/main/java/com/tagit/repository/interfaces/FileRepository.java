package com.tagit.repository.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tagit.model.FileModel;

public interface FileRepository extends JpaRepository<FileModel, Long> {
    
    List<FileModel> findByName(String name);
    List<FileModel> findByType(String type);
    // FileModel saveFileRecord(
    //     String name,
    //     String size,
    //     String absolutePath,
    //     String type);
}
