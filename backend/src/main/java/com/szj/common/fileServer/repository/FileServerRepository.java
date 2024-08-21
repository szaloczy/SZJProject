package com.szj.common.fileServer.repository;

import com.szj.common.fileServer.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface for managing FileEntities in a repository.
 */
@Repository
public interface FileServerRepository extends JpaRepository<FileEntity, String> {
    Optional<FileEntity> findFileEntityById(UUID id);
    void deleteFileEntityById(UUID id);
    List<FileEntity> findFileEntitiesByInUse(boolean inUse);
}
