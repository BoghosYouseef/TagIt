package com.tagit.model;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Temporal;
import jakarta.persistence.OneToMany;
import jakarta.persistence.TemporalType;
import jakarta.persistence.GeneratedValue;  
import jakarta.persistence.GenerationType;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


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

    @Column(name = "absolute_path")
    private String absolutePath;

    @Column(name = "file_type")
    private String type; // e.g., "text", "image"
    
    @Column(name = "file_extension")     
    private String extension; // e.g., "txt", "jpeg"

    // date information for last modified
    @Column(name = "last_modified_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Instant lastModifiedAt;

    @OneToMany(mappedBy = "file", fetch = FetchType.EAGER)
    private Set<FileTag> fileTags = new HashSet<>();

    public FileModel() {
        // Default constructor
    }

    public FileModel(
        String name,
        String size,
        String absolutePath,
        String fileType,
        String fileExtension,
        Instant lastModifiedAt) {

        this.name = name;
        this.size = size;
        this.absolutePath = absolutePath;
        this.type = fileType;
        this.extension = fileExtension;
        this.lastModifiedAt = lastModifiedAt;
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
    public String getExtension() {
        return extension;
    }

    public Instant getLastModifiedAt() {
        return lastModifiedAt;
    }

    public Set<TagModel> getTags(){
        return fileTags.stream()
            .map(fileTag -> fileTag.getTag())
            .collect(Collectors.toSet());
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

    public void setType(String fileType) {
        this.type = fileType;
    }
    public void setExtension(String fileExtension) {
        this.extension = fileExtension;
    }

    public void setLastModifiedAt(Instant lastModifiedAt){
        this.lastModifiedAt = lastModifiedAt;
    }

    public FileModel saveFileRecord(String name, String size, String absolutePath, String type) {
        // FileModel file = new FileModel();
        // file.setName(name);
        // file.setSize(size);
        // file.setAbsolutePath(absolutePath);
        // file.setType(type);
        // // entityManager.persist(file);
        // save(file);

        return this;
    }

    @Override
	public String toString() {
		return String.format(
				"File[name=%d, type='%s', absPath='%s']",
				this.getName(), this.getType(), this.getAbsolutePath());
	}
}
