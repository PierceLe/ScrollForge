package org.scrollSystem.repository;

import jakarta.validation.constraints.Size;
import org.scrollSystem.models.Setting;
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
public interface SettingRepo extends JpaRepository<Setting, Integer> {
    @Modifying
    @Query(value = "TRUNCATE TABLE setting", nativeQuery = true)
    void truncateSettingTable();

}
