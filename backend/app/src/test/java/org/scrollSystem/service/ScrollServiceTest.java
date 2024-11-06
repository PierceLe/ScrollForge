package org.scrollSystem.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.scrollSystem.models.Setting;
import org.scrollSystem.repository.SettingRepo;
import org.scrollSystem.request.Timer;
import org.scrollSystem.exception.ValidationException;
import org.scrollSystem.repository.SettingRepo;
import org.scrollSystem.request.FileRequest;
import org.scrollSystem.request.Timer;
import org.scrollSystem.repository.SettingRepo;
import org.scrollSystem.request.Timer;
import org.scrollSystem.config.security.JwtService;
import org.scrollSystem.models.FileStorage;
import org.scrollSystem.models.User;
import org.scrollSystem.repository.FileStorageRepository;
import org.scrollSystem.repository.UserRepository;
import org.scrollSystem.request.AuthenticationRequest;
import org.scrollSystem.response.FileResponse;
import org.scrollSystem.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ScrollServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private UserRepository userRepositoryMock;

    @MockBean
    private S3Service s3Service;


    @Autowired
    private FileStorageRepository fileStorageRepository;

    @Mock
    private FileStorageRepository fileStorageRepositoryMock;

    @InjectMocks
    private ScrollService scrollService;

    @Autowired
    private ScrollService scrollServiceAutoWired;

    @Mock
    private SettingRepo settingRepo;

    // Init data
    private User admin;
    private FileStorage fileStorage;
    private String jwtToken;
    private FileStorage fileStorage1;
    private FileStorage fileStorage2;
    private FileStorage existingFile;
    private User authenticatedUser;
    private FileStorage fileStorageSuccess;

    @BeforeEach
    public void setUpDataBase() throws Exception
    {
        // Set up the db for user
        admin = User.builder()
                .id(1)
                .username("hale0087")
                .email("hale0087@uni.sydney.edu.au")
                .firstName("Pierce")
                .lastName("Le")
                .role("ROLE_USER")
                .phone("+61449510532")
                .password("54b908a8be404cb42e3f05400128704d0c7d760b42fb8a2a5049e359eb9b0050")
                .salt("vYDTe/LHlrhN2jOp7BypCg==")
                .avatarUrl(null)
                .build();

        userRepository.save(admin);

        User user1 = User.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .username("johndoe")
                .phone("+123456789")
                .role("ROLE_USER")
                .build();

        User user2 = User.builder()
                .id(2)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .username("janesmith")
                .phone("+987654321")
                .role("ROLE_USER")
                .build();

        fileStorage1 = FileStorage.builder()
                .fileId(1)
                .title("File 1")
                .filePath("/files/file1.pdf")
                .fileSize(1024L)
                .fileType("pdf")
                .uploadDate(new Timestamp(System.currentTimeMillis()))
                .downloadAmount(5)
                .owner(user1)
                .build();

        fileStorage2 = FileStorage.builder()
                .fileId(2)
                .title("File 2")
                .filePath("/files/file2.doc")
                .fileSize(2048L)
                .fileType("doc")
                .uploadDate(new Timestamp(System.currentTimeMillis()))
                .downloadAmount(10)
                .owner(user2)
                .build();

        fileStorage = FileStorage.builder()
                .fileId(1)
                .title("title")
                .filePath("filePath")
                .fileSize(0L)
                .fileType("type")
                .owner(admin)
                .build();

        existingFile = FileStorage.builder()
                .fileId(1)
                .title("Old Title")
                .filePath("/old/path/file.txt")
                .build();

        authenticatedUser = User.builder()
                .id(1)
                .username("testUser")
                .build();

        fileStorageRepository.save(fileStorage);

        fileStorageSuccess = FileStorage.builder()
                .fileId(1)
                .title("any1")
                .filePath("https://huyngu.s3.ap-southeast-2.amazonaws.com/1.bin")
                .fileSize(12L)
                .fileType("application/octet-stream")
                .owner(admin)
                .build();

        // Send login request
        AuthenticationRequest loginRequest = new AuthenticationRequest();
        loginRequest.setUsername("hale0087");
        loginRequest.setPassword("high_distinction_100");

        ObjectMapper objectMapper = new ObjectMapper();
        String loginContent = objectMapper.writeValueAsString(loginRequest);

        jwtToken = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(loginContent))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .split("\"token\":\"")[1]
                .split("\"")[0];

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        // Mock authenticated user details
        User authenticatedUser = new User();
        authenticatedUser.setUsername("testUser");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(authenticatedUser);

        // Set the security context with the mocked authentication
        SecurityContextHolder.setContext(securityContext);
    }


    @Test
    @Order(4)
    void testGetSearchFilterValid1() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/scroll")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("title", "title")
                        .param("file_type", "type"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].title").value("title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].fileType").value("type"));

    }


    @Test
    @Order(2)
    void testGetSearchFilterValid2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/scroll")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("owner", "hale0087"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].title").value("title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].fileType").value("type"));

    }


    @Test
    @Order(3)
    void testGetSearchFilterInValid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/scroll")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("owner", "does not exist"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.length()").value(0));

    }


    @Test
    public void testUpdateTimeForAllScrolls() {
        Integer time = 30;
        scrollService.updateTimeForAllScrolls(time);
        assertEquals(time, 30);
    }

    @Test
    public void testUpdateTime() {
        Timer request = new Timer();
        request.setTimer(30);
        String result = scrollService.updateTime(request);
        assertEquals(result, "SUCESS");
    }

    @Test
    public void testDownload_FileNotFound() {
        int fileId = 1;

        when(fileStorageRepositoryMock.getFileStorageByFileId(fileId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(ValidationException.class, () -> {
            scrollService.download(fileId);
        });

        assertEquals("File id " + fileId + " is not exist", exception.getMessage());
        verify(fileStorageRepositoryMock, times(1)).getFileStorageByFileId(fileId);

        verifyNoMoreInteractions(fileStorageRepositoryMock);
        verifyNoInteractions(userRepositoryMock);
    }

    @Test
    public void testDownload_Success() {
        FileStorage mockFile = new FileStorage();
        mockFile.setFilePath("test/file/path");
        mockFile.setFileId(1);
        mockFile.setDownloadAmount(0);

        User mockUser = new User();
        mockUser.setDownloadNumber(10);

        when(fileStorageRepositoryMock.getFileStorageByFileId(1)).thenReturn(Optional.of(mockFile));

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        SecurityContextHolder.setContext(securityContext);

        String filePath = scrollService.download(1);

        assertEquals("test/file/path", filePath);
        assertEquals(1, mockFile.getDownloadAmount());
        assertEquals(11, mockUser.getDownloadNumber());
    }

    @Test
    public void testGetScrolls() {

        when(fileStorageRepositoryMock.findAll()).thenReturn(Arrays.asList(fileStorage1, fileStorage2));
        List<FileResponse> result = scrollService.getScrolls();
        assertEquals(2, result.size());

        FileResponse fileResponse1 = result.get(0);
        assertEquals(1, fileResponse1.getFileId());
        assertEquals("File 1", fileResponse1.getTitle());
        assertEquals("/files/file1.pdf", fileResponse1.getFilePath());
        assertEquals(1024L, fileResponse1.getFileSize());
        assertEquals("pdf", fileResponse1.getFileType());
        assertEquals(5, fileResponse1.getDownloadAmount());

        UserResponse owner1 = fileResponse1.getOwner();
        assertEquals(1, owner1.getId());
        assertEquals("John", owner1.getFirstName());
        assertEquals("Doe", owner1.getLastName());
        assertEquals("john.doe@example.com", owner1.getEmail());
        assertEquals("johndoe", owner1.getUsername());
        assertEquals("+123456789", owner1.getPhone());
        assertEquals("ROLE_USER", owner1.getRole());

        FileResponse fileResponse2 = result.get(1);
        assertEquals(2, fileResponse2.getFileId());
        assertEquals("File 2", fileResponse2.getTitle());
        assertEquals("/files/file2.doc", fileResponse2.getFilePath());
        assertEquals(2048L, fileResponse2.getFileSize());
        assertEquals("doc", fileResponse2.getFileType());
        assertEquals(10, fileResponse2.getDownloadAmount());

        UserResponse owner2 = fileResponse2.getOwner();
        assertEquals(2, owner2.getId());
        assertEquals("Jane", owner2.getFirstName());
        assertEquals("Smith", owner2.getLastName());
        assertEquals("jane.smith@example.com", owner2.getEmail());
        assertEquals("janesmith", owner2.getUsername());
        assertEquals("+987654321", owner2.getPhone());
        assertEquals("ROLE_USER", owner2.getRole());
    }

    @Test
    public void testUpdate_Success() throws IOException {

        when(fileStorageRepositoryMock.getFileStorageByFileId(1)).thenReturn(Optional.of(existingFile));
        when(fileStorageRepositoryMock.getFileStorageByTitle("New Title")).thenReturn(Optional.empty());
        when(s3Service.uploadFile(any())).thenReturn("/new/path/file.txt");

        doAnswer(invocation -> {
            FileStorage fileStorage = invocation.getArgument(0);
            fileStorage.setTitle("New Title");
            fileStorage.setTimer(30);
            fileStorage.setFilePath("/new/path/file.txt");
            return fileStorage;
        }).when(fileStorageRepositoryMock).save(any(FileStorage.class));

        FileRequest request = new FileRequest();
        request.setId(1);
        request.setTitle("New Title");
        request.setFile(mock(org.springframework.web.multipart.MultipartFile.class));
        request.setTimer(30);

        FileResponse response = scrollServiceAutoWired.update(request);

        fileStorageRepositoryMock.getFileStorageByFileId(1);
        fileStorageRepositoryMock.save(existingFile);
        s3Service.uploadFile(any());

        assertEquals("New Title", existingFile.getTitle());
        assertEquals(30, existingFile.getTimer());
        assertEquals("/new/path/file.txt", existingFile.getFilePath());
        assertEquals("New Title", response.getTitle());
    }

//    @Test
//    public void testUpdate_FileTitleExists() throws IOException {
//        // Mock the existing file by ID
//        when(fileStorageRepositoryMock.getFileStorageByFileId(1)).thenReturn(Optional.of(existingFile));
//
//        // Mock the scenario where another file with the same title exists
//        FileStorage anotherFile = FileStorage.builder()
//                .fileId(2)  // Different ID to simulate a conflict
//                .title("New Title")
//                .build();
//
//        // Mock the conflict scenario: another file with the same title exists
//        when(fileStorageRepositoryMock.getFileStorageByTitle("New Title")).thenReturn(Optional.of(anotherFile));
//
//        // Prepare the request
//        FileRequest request = new FileRequest();
//        request.setId(1);  // This is the ID of the file we want to update
//        request.setTitle("New Title");  // This title conflicts with the existing file
//        request.setFile(mock(org.springframework.web.multipart.MultipartFile.class));
//        request.setTimer(30);
//
//        // Expect a ValidationException due to the title conflict
//        Exception exception = assertThrows(ValidationException.class, () -> {
//            scrollServiceAutoWired.update(request);
//        });
//
//        // Verify that the exception message is as expected
//        assertEquals("File title is exists", exception.getMessage());
//
//        // Verify interactions
//        verify(fileStorageRepositoryMock).getFileStorageByFileId(1);
//        verify(fileStorageRepositoryMock).getFileStorageByTitle("New Title");
//
//        // Ensure that the save method is never called, since the update fails
//        verify(fileStorageRepositoryMock, never()).save(any(FileStorage.class));
//    }

    @Test
    public void testDeleteFile_Success() throws IOException {
        when(fileStorageRepositoryMock.getFileStorageByFileId(1)).thenReturn(Optional.of(existingFile));
        User authenticatedUser = User.builder()
                .id(1)
                .username("testUser")
                .role("ROLE_USER")
                .build();

        existingFile.setOwner(authenticatedUser);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        SecurityContextHolder.setContext(securityContext);

        doNothing().when(s3Service).deleteFile(existingFile.getTitle());
        Integer deletedFileId = scrollServiceAutoWired.deleteFile(1);

        fileStorageRepositoryMock.getFileStorageByFileId(1);
        s3Service.deleteFile(existingFile.getTitle());
        fileStorageRepositoryMock.delete(existingFile);

        assertEquals(1, deletedFileId);
    }

//    @Test
//    public void testUploadFile_Success() throws IOException {
//        // Initialize the authenticated user's upload number
//        authenticatedUser.setUploadNumber(5);  // Assuming the user starts with 5 uploads
//
//        // Mock no file with the same title exists
//        when(fileStorageRepositoryMock.getFileStorageByTitle("New Title")).thenReturn(Optional.empty());
//
//        // Mock the S3 upload returning a file path
//        when(s3Service.uploadFile(any(MultipartFile.class))).thenReturn("/new/path/file.txt");
//
//        // Mock the settingRepo to return a timer value
//        when(settingRepo.findAll()).thenReturn(List.of(new Setting(1, 30)));  // assuming 30 is the timer
//
//        // Mock fetching the authenticated user from the repository (ensure the user is managed)
//        when(userRepositoryMock.findById(authenticatedUser.getId())).thenReturn(Optional.of(authenticatedUser));
//
//        // Prepare a mock file
//        MultipartFile mockFile = mock(MultipartFile.class);
//        when(mockFile.getSize()).thenReturn(1024L);  // Mock file size
//        when(mockFile.getContentType()).thenReturn("pdf");  // Mock file type
//
//        // Mock saving the file storage entity
//        doAnswer(invocation -> {
//            FileStorage fileStorage = invocation.getArgument(0);
//            fileStorage.setOwner(authenticatedUser); // Ensure the owner is set to the authenticated user
//            return fileStorage;
//        }).when(fileStorageRepositoryMock).save(any(FileStorage.class));
//
//        // Call the uploadFile method
//        FileResponse response = scrollServiceAutoWired.uploadFile(mockFile, "New Title");

//        // Verify that the upload number is incremented
//        assertEquals(6, authenticatedUser.getUploadNumber());  // Assert that the upload number has been incremented by 1
//
//        // Assert the response content
//        assertEquals("New Title", response.getTitle());
//        assertEquals("/new/path/file.txt", response.getFilePath());
//        assertEquals(1024L, response.getFileSize());
//        assertEquals("pdf", response.getFileType());
//        assertEquals(30, response.getTimer());
//
//        // Verify interactions with the mocks
//        verify(fileStorageRepositoryMock).save(any(FileStorage.class));  // Ensure FileStorage was saved
//        verify(userRepositoryMock).save(authenticatedUser);  // Ensure the user is saved/updated
//    }

    @Test
    void testUploadFileSuccess() throws Exception {

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "1.bin",
                "application/octet-stream",
                "Sample file content".getBytes()
        );

        Mockito.when(s3Service.uploadFile(ArgumentMatchers.any())).thenReturn(fileStorage.getFilePath());
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/api/v1/scroll/upload")
                        .file(mockFile)
                        .param("title", "any1")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true));
    }

    @Test
    void testUploadFileFail() throws Exception {

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "sample.txt",
                "text/plain",
                "Sample file content".getBytes()
        );

        Mockito.when(s3Service.uploadFile(ArgumentMatchers.any())).thenReturn(fileStorage.getFilePath());
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/scroll/upload")
                        .file(mockFile)
                        .param("title", fileStorage.getTitle())
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Having errors, please try again in another time"));
    }


}
