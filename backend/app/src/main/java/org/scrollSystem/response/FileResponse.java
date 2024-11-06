package org.scrollSystem.response;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class FileResponse {
    private Integer fileId;
    private String title;
    private String filePath;
    private Timestamp uploadDate;
    private Long fileSize;
    private String fileType;
    private Integer downloadAmount;
    private Integer uploadAmount;
    private Integer timer;
    private UserResponse owner;
}
