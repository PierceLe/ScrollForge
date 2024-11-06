package org.scrollSystem.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.scrollSystem.service.S3Service;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@Transactional
public class FileStorageTest {

    @MockBean
    private S3Service s3Service;

    private FileStorage fileStorage;

    @BeforeEach
    void setUp() {
        // Initialize the FileStorage object
        fileStorage = FileStorage.builder()
                .title("Test File")
                .filePath("/path/to/file")
                .fileSize(1024L)
                .fileType("text/plain")
                .downloadAmount(0)
                .build();
    }

    @Test
    void testIncreaseDownload() {
        // Initial download amount should be 0
        assertEquals(0, fileStorage.getDownloadAmount(), "Initial download amount should be 0");

        // Call increaseDownload()
        fileStorage.increaseDownload();

        // Download amount should increase to 1
        assertEquals(1, fileStorage.getDownloadAmount(), "Download amount should be 1 after first increase");

        // Call increaseDownload() again
        fileStorage.increaseDownload();

        // Download amount should now be 2
        assertEquals(2, fileStorage.getDownloadAmount(), "Download amount should be 2 after second increase");
    }
}
