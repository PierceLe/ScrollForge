package org.scrollSystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.scrollSystem.config.ApplicationConfig;
import org.scrollSystem.config.security.JwtService;
import org.scrollSystem.exception.ValidationException;
import org.scrollSystem.models.FileStorage;
import org.scrollSystem.models.User;
import org.scrollSystem.repository.FileStorageRepository;
import org.scrollSystem.repository.UserRepository;
import org.scrollSystem.request.AuthenticationRequest;
import org.scrollSystem.request.FileRequest;
import org.scrollSystem.request.Timer;
import org.scrollSystem.request.AuthenticationRequest;
import org.scrollSystem.response.DefaultResponse;
import org.scrollSystem.response.FileResponse;
import org.scrollSystem.response.UserResponse;
import org.scrollSystem.service.S3Service;
import org.scrollSystem.service.ScrollService;
import org.scrollSystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.isNull;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.*;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class ScrollControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScrollService scrollService;

    @MockBean
    private S3Service s3Service;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FileStorageRepository fileStorageRepository;

    private FileResponse fileResponseSuccess;

    private UserResponse userResponseSuccess;

    private String jwtToken;



    @MockBean
    protected UserService userService;


    // Init data
    static User admin;
    static FileStorage fileStorage;
    private List<FileResponse> listFileResponse = new ArrayList<>();

    @MockBean
    private JwtService jwtService;

    @BeforeEach
    public void setUpDataBase(){

        admin = User.builder()
                .id(1)
                .username("hale0087")
                .email("hale0087@uni.sydney.edu.au")
                .firstName("Pierce")
                .lastName("Le")
                .role("ROLE_ADMIN")
                .phone("+61449510532")
                .password("f707fdda7c874ff49ebfb2c88a2860c5ff4ce3d94a21efb76566ad0f92c9ad57")
                        .salt("a")
                .avatarUrl(null)
                .build();

        AuthenticationRequest loginRequest = new AuthenticationRequest();
        loginRequest.setUsername("hale0087");
        loginRequest.setPassword("high_distinction_100");

        jwtToken = "mocked-jwt-token";

        Mockito.when(jwtService.generateToken(ArgumentMatchers.any(User.class)))
                .thenReturn(jwtToken);

        userRepository.save(admin);


        fileStorage = FileStorage.builder()
                .title("title")
                .filePath("filePath")
                .fileSize(0L)
                .fileType("type")
                .owner(admin)
                .build();

        fileStorageRepository.save(fileStorage);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        userResponseSuccess = UserResponse.builder()
                .id(2)
                .firstName("Quang Huy")
                .lastName("Nguyen")
                .email("qngu0806@uni.sydney.edu.au")
                .username("qngu0806")
                .role("ROLE_ADMIN")
                .phone("+61411591768")
                .avatarUrl(null)
                .uploadNumber(0)
                .downloadNumber(0)
                .build();

        fileResponseSuccess = FileResponse.builder()
                .fileId(1)
                .title("success")
                .filePath("pathSuccess")
                .fileSize(0L)
                .fileType("typeSuccess")
                .downloadAmount(0)
                .timer(5)
                .owner(userResponseSuccess)
                .build();

    }


    private void setUpFilter() {

        // Create listFileResponse
        FileResponse response = FileResponse.builder()
                .fileId(fileStorage.getFileId())
                .title(fileStorage.getTitle())
                .filePath(fileStorage.getFilePath())
                .fileSize(fileStorage.getFileSize())
                .fileType(fileStorage.getFileType())
                .uploadDate(fileStorage.getUploadDate())
                .downloadAmount(fileStorage.getDownloadAmount())
                .build();

        UserResponse ownerResponse = UserResponse.builder()
                .id(admin.getId())
                .firstName(admin.getFirstName())
                .lastName(admin.getLastName())
                .email(admin.getEmail())
                .username(admin.getUsername())
                .phone(admin.getPhone())
                .role(admin.getRole())
                .build();

        response.setOwner(ownerResponse);

        listFileResponse.add(response);

    }

    @Test
    void testGetSearchFilterValid() throws Exception {
        this.setUpFilter();

        Mockito.when(scrollService.getSearchFilter(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(listFileResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/scroll")
                .param("title", "title")
                .param("fileType", "type"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(listFileResponse.size()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].title").value(listFileResponse.get(0).getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].fileType").value(listFileResponse.get(0).getFileType()));

    }

    @Test
    void testGetSearchFilterInvalid() throws Exception {
        Mockito.when(scrollService.getSearchFilter(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenThrow(new ValidationException("Username is not exists") );

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/scroll")
                        .param("owner", "does not exist"))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false));

    }

    @Test
    void testDeleteFileSuccess() throws Exception {
        Mockito.when(scrollService.deleteFile(ArgumentMatchers.anyInt())).thenReturn(1);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/scroll/delete/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("File deleted successfully"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(1));
    }

    @Test
    void testDeleteFileError() throws Exception {
        Mockito.when(scrollService.deleteFile(ArgumentMatchers.anyInt())).thenThrow(new RuntimeException("Deletion failed"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/scroll/delete/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isForbidden())  // Expecting a 500 error
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Error deleting file: Deletion failed"));
    }

    @Test
    @WithMockUser(username = "hale0087", roles = {"ADMIN"})
    void testSetDefaultTimeSuccess() throws Exception {

        Timer timerRequest = new Timer();
        timerRequest.setTimer(300);

        Mockito.when(scrollService.updateTime(ArgumentMatchers.any(Timer.class)))
                .thenReturn("SUCCESS");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(timerRequest);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/scroll/default-time")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("SUCCESS"));
    }

    @Test
    @WithMockUser(username = "hale0087", roles = {"ADMIN"})
    void testGetLogsSuccess() throws Exception {
        List<Map<String, Object>> mockLogs = new ArrayList<>();

        Map<String, Object> logEntry1 = new HashMap<>();
        logEntry1.put("log1", "First log entry");

        Map<String, Object> logEntry2 = new HashMap<>();
        logEntry2.put("log2", "Second log entry");

        mockLogs.add(logEntry1);
        mockLogs.add(logEntry2);

        Mockito.when(scrollService.getLogs()).thenReturn(mockLogs);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/scroll/logs")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].log1").value("First log entry"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].log2").value("Second log entry"));
    }


    @Test
    @WithMockUser(username = "hale0087", roles = {"ADMIN"})
    void testGetLogsError() throws Exception {
        Mockito.when(scrollService.getLogs()).thenThrow(new RuntimeException("Failed to retrieve logs"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/scroll/logs")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Failed to retrieve logs"));
    }

    @Test
    void testUpdateSuccess() throws Exception {

        MockMultipartFile mockFile = new MockMultipartFile("file", "testfile.txt", "text/plain", "test file content".getBytes());
        FileResponse mockFileResponse = FileResponse.builder()
                .fileId(1)
                .title("Updated Title")
                .filePath("updated/file/path")
                .fileSize(1024L)
                .fileType("text/plain")
                .build();

        Mockito.when(scrollService.update(ArgumentMatchers.any(FileRequest.class)))
                .thenReturn(mockFileResponse);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/scroll/update")
                        .file(mockFile)
                        .param("id", "1")
                        .param("title", "Updated Title")
                        .param("timer", "300")
                        .with(request -> {
                            request.setMethod("PUT");  // Since multipart defaults to POST, we explicitly set it to PUT
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.fileId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value("Updated Title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.filePath").value("updated/file/path"));
    }

    @Test
    void testUpdateError() throws Exception {

        MockMultipartFile mockFile = new MockMultipartFile("file", "testfile.txt", "text/plain", "test file content".getBytes());
        Mockito.when(scrollService.update(ArgumentMatchers.any(FileRequest.class)))
                .thenThrow(new RuntimeException("Update failed"));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/scroll/update")
                        .file(mockFile)
                        .param("id", "1")
                        .param("title", "Updated Title")
                        .param("timer", "300")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Update failed"));
    }

    @Test
    void testDownloadSuccess() throws Exception {
        Mockito.when(scrollService.download(ArgumentMatchers.anyInt()))
                .thenReturn("path/to/file");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/scroll/download/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("path/to/file"));
    }


    @Test
    void uploadScrollTestInvalid() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "sample.txt",
                "text/plain",
                "Sample file content".getBytes()
        );

        Mockito.when(scrollService.uploadFile(ArgumentMatchers.any(), ArgumentMatchers.eq(fileStorage.getTitle())))
                .thenThrow(new IOException("File upload failed"));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/scroll/upload")
                        .file(mockFile)
                        .param("title", fileStorage.getTitle())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void uploadScrollTestSuccess() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Sample file content".getBytes()
        );
        Mockito.when(scrollService.uploadFile(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(fileResponseSuccess);

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/api/v1/scroll/upload")
                        .file(mockFile)
                        .param("title", "test-title")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


}
