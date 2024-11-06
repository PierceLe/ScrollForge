package org.scrollSystem.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileRequest {
    private Integer id;
    private String title;
    private MultipartFile file;
    private Integer timer;
}
