package org.scrollSystem.repository;

import jakarta.transaction.Transactional;
import org.scrollSystem.models.User;
import org.scrollSystem.models.FileStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileStorageRepository extends JpaRepository<FileStorage, Integer> {

    Optional<FileStorage> getFileStorageByFileId(Integer id);
    Optional<FileStorage> getFileStorageByTitle(String title);

    // Get by field
    @Query("SELECT f FROM FileStorage f " +
            "WHERE (:title IS NULL OR f.title LIKE CONCAT('%', :title, '%')) " +
            "AND (:fileType IS NULL OR f.fileType LIKE CONCAT('%', :fileType, '%')) " +
            "AND (:ownerId IS NULL OR f.owner.id = :ownerId) " +
            "AND (:fileId IS NULL OR f.fileId = :fileId) " +
            "AND (:fromDate IS NULL OR f.uploadDate >= :fromDate) " +
            "AND (:toDate IS NULL OR f.uploadDate <= :toDate) " +
            "ORDER BY f.uploadDate DESC")
    List<FileStorage> filterbyField(
            String title,
            String fileType,
            Integer ownerId,
            Integer fileId,
            Timestamp fromDate,
            Timestamp toDate
    );


    @Modifying
    @Transactional
    @Query("UPDATE FileStorage f SET f.timer = :time")
    void updateScrollTimerForAll(Integer time);
}
