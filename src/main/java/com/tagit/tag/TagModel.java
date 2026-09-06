package com.tagit.tag;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Temporal;
import jakarta.persistence.OneToMany;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.tagit.file.domain.FileTag;





@Entity
@Table(name = "tags",
     uniqueConstraints = @UniqueConstraint(
        columnNames = {"text", "colorHashString"}
    )
)
public class TagModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String text;

    @Column
    private String description;

    @Column
    private String colorHashString;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Instant createdAt;

    @OneToMany(mappedBy = "tag")
    private Set<FileTag> fileTags = new HashSet<>();
    
    public TagModel(){};

    TagModel(Builder builder){
        this.text = builder.text;
        this.description = builder.description;
        this.colorHashString = builder.colorHashString;
        this.createdAt = builder.createdAt;
    }

    //// setters 
    public void setId(Long id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setColorHashString(String colorHashString) {
        this.colorHashString = colorHashString;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setFileTags(Set<FileTag> fileTags) {
        this.fileTags = fileTags;
    }

    //// getters
    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getDescription() {
        return description;
    }

    public String getColorHashString() {
        return colorHashString;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Set<FileTag> getFileTags() {
        return fileTags;
    }

    public static class Builder {
        private String text;
        private String description;
        private String colorHashString;
        private Instant createdAt;

        public Builder setText(String text){this.text = text; return this;};
        public Builder setDescription(String description){this.description = description; return this;};
        public Builder setColorHashString(String colorHashString){this.colorHashString = colorHashString; return this;};
        public Builder setCreatedAt(Instant createdAt){this.createdAt = createdAt; return this;};
        public TagModel build(){return new TagModel(this);};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TagModel other)) return false;
        return Objects.equals(text, other.text)
            && Objects.equals(colorHashString, other.colorHashString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, colorHashString);
    }
    
}
