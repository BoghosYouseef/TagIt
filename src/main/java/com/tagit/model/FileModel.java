package com.tagit.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;  
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "files")
public class FileModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String size;

    @Column
    private String absolutePath;

    @Column
    private String type; // e.g., "text/plain", "image/jpeg"

    public FileModel() {
        // Default constructor
    }

    public FileModel(String name, String size, String absolutePath, String type) {
        this.name = name;
        this.size = size;
        this.absolutePath = absolutePath;
        this.type = type;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public String getType() {
        return type;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public void setType(String type) {
        this.type = type;
    }
}
