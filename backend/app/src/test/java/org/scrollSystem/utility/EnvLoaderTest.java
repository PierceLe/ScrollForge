package org.scrollSystem.utility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.scrollSystem.service.S3Service;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class    EnvLoaderTest {

    @TempDir
    Path tempDir;

    @MockBean
    private S3Service s3Service;

    private Path envFilePath;



    @BeforeEach
    public void setUp() throws IOException {
        envFilePath = tempDir.resolve("test.env");
        Files.write(envFilePath, String.join("\n",
                "# This is a comment",
                "AWS_ACCESS_KEY=access_key_value",
                "AWS_SECRET_KEY=secret_key_value",
                "DATABASE_URL=jdbc:mysql://localhost:3306/testdb",
                "INVALID_LINE_WITHOUT_EQUALS_SIGN"
        ).getBytes());
    }

    @Test
    public void testLoadEnv() {
        Map<String, String> envMap = EnvLoader.loadEnv(envFilePath.toString());

        assertEquals(3, envMap.size(), "There should be 3 valid entries in the envMap.");
        assertEquals("access_key_value", envMap.get("AWS_ACCESS_KEY"));
        assertEquals("secret_key_value", envMap.get("AWS_SECRET_KEY"));
        assertEquals("jdbc:mysql://localhost:3306/testdb", envMap.get("DATABASE_URL"));
    }

    @Test
    public void testLoadEnvIgnoreCommentsAndInvalidLines() {
        Map<String, String> envMap = EnvLoader.loadEnv(envFilePath.toString());
        assertFalse(envMap.containsKey("INVALID_LINE_WITHOUT_EQUALS_SIGN"),
                "Invalid lines without an '=' should be ignored.");
    }

    @Test
    public void testLoadEnvIOException() throws IOException {
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.lines(any(Path.class)))
                    .thenThrow(new IOException("Simulated IOException"));
            Map<String, String> envMap = EnvLoader.loadEnv(envFilePath.toString());
            assertTrue(envMap.isEmpty(), "The map should be empty if an IOException is thrown.");
        }
    }
}
