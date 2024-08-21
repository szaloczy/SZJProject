package com.szj.common.fileServer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
@Entity
@Table(name = "file_entity")
public class FileEntity {
    @Id
    @JsonProperty
    private UUID id;

    @JsonProperty
    private String extension;

    @JsonProperty
    private LocalDateTime creationDate;

    @JsonProperty
    public boolean inUse;

    public FileEntity(UUID id, String extension, LocalDateTime creationDate) {
        this.id = id;
        this.extension = extension;
        this.creationDate = creationDate;
        this.inUse = false;
    }

    @Override
    public String toString() {
       return String.format("Asset{id=%s, extension=%s, creationDate=%s}", id, extension, creationDate);
    }
}
