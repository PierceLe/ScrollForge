package org.scrollSystem.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "file_storage")
public class FileStorage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fileId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private Timestamp uploadDate;

    @Column(nullable = false)
    private Long fileSize;

    private String fileType;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private Integer downloadAmount;

    private Integer timer;

    @PrePersist
    protected void onCreate() {
        uploadDate = new Timestamp(System.currentTimeMillis());
        downloadAmount = 0;
    }

    public void increaseDownload() {
        this.downloadAmount++;
    }
}

