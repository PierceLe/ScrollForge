package org.scrollSystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.scrollSystem.exception.ValidationException;
import org.scrollSystem.request.FileRequest;
import org.scrollSystem.request.Timer;
import org.scrollSystem.response.*;
import org.scrollSystem.service.ScrollService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/scroll")
@RequiredArgsConstructor
public class ScrollController {
    private final ScrollService scrollService;

    // API for upload the file into AWS S3
    @PostMapping({"/upload"})
    public ResponseEntity<DefaultResponse<FileResponse>> uploadScroll(
        @RequestParam("title") String title,
        @RequestParam("file") MultipartFile file
    ) throws IOException {
        FileResponse fileResponse = scrollService.uploadFile(file, title);
        return DefaultResponse.success(fileResponse);
    }

    // API for delete the file
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<DefaultResponse<Integer>> deleteFile(@PathVariable Integer id) {
        try {
            Integer id_deleted = scrollService.deleteFile(id);
            return DefaultResponse.success("File deleted successfully", id_deleted);
        } catch (Exception e) {
            return DefaultResponse.error("Error deleting file: " + e.getMessage());
        }
    }

    // API for get scrolls and filter depend on the params such as title, file_type, owner
    @GetMapping("")
    public ResponseEntity<DefaultResponse<List<FileResponse>>> getSearchFilter(@RequestParam("title") @Nullable String title,
                                                                               @RequestParam("file_type") @Nullable String fileType,
                                                                               @RequestParam("owner") @Nullable String ownerUsername,
                                                                               @RequestParam("file_id") @Nullable Integer fileId,
                                                                               @RequestParam("From") @Nullable Timestamp fromDate,
                                                                               @RequestParam("To") @Nullable Timestamp toDate) throws ValidationException {
//        System.out.println(fromDate + " " + toDate);
        return DefaultResponse.success(scrollService.getSearchFilter(title, fileType, ownerUsername,fileId, fromDate, toDate));
    }

//    @GetMapping("")
//    public ResponseEntity<DefaultListResponse<FileResponse>> getScrolls(
//    ) {
//        return DefaultListResponse.success(scrollService.getScrolls());
//    }

    @GetMapping("/download/{id}")
    public ResponseEntity<DefaultResponse<String>> download(@PathVariable Integer id) {
        return DefaultResponse.success(scrollService.download(id));
    }

    @PutMapping("/update")
    public ResponseEntity<DefaultResponse<FileResponse>> update(
            @RequestParam("id") Integer id,
            @RequestParam("title") String title,
            @RequestParam("file") MultipartFile file,
            @RequestParam("timer") Integer timer
    ) {
        try {
            FileRequest request = new FileRequest();
            request.setId(id);
            request.setTitle(title);
            request.setFile(file);
            request.setTimer(timer);

            return DefaultResponse.success(scrollService.update(request));
        } catch (Exception e) {
            return DefaultResponse.error(e.getMessage());
        }
    }

    @GetMapping("/logs")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<DefaultListResponse<Map<String, Object>>> getLogs() {
        try {
            return DefaultListResponse.success("success", scrollService.getLogs());
        } catch (Exception e) {
            return DefaultListResponse.error(e.getMessage());
        }
    }

    @PutMapping("default-time")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<DefaultResponse<String>> setDefaultTime(
            @RequestBody @Valid Timer request
    ) {
        return DefaultResponse.success(scrollService.updateTime(request));
    }


}