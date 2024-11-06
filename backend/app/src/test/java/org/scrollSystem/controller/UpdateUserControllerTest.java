package org.scrollSystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.scrollSystem.config.security.JwtService;
import org.scrollSystem.exception.ValidationException;
import org.scrollSystem.models.User;
import org.scrollSystem.request.AuthenticationRequest;
import org.scrollSystem.request.UpdatePasswordRequest;
import org.scrollSystem.request.UserUpdateRequest;
import org.scrollSystem.response.DefaultResponse;
import org.scrollSystem.response.UserResponse;
import org.scrollSystem.service.S3Service;
import org.scrollSystem.service.UserUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.scrollSystem.repository.UserRepository;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UpdateUserControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private S3Service s3Service;

    @MockBean
    private UserUpdateService userUpdateService;

    @MockBean
    private JwtService jwtService;

    private String jwtToken;

    private UserResponse userResponse;


    @BeforeEach
    void init() throws Exception {
        userRepository.save(User.builder()
                .id(1)
                .username("hale0087")
                .email("hale0087@uni.sydney.edu.au")
                .firstName("Pierce")
                .lastName("Le")
                .role("ROLE_ADMIN")
                .phone("+61449510532")
                .password("867425e1b76b417afac933242947bf63a0f178422e360bb70f5ee2e75f7b6922")
                .salt("a")
                .avatarUrl(null)
                .build());

        userResponse = UserResponse.builder()
                .id(1)
                .username("hale0087")
                .firstName("Pierce")
                .lastName("Le")
                .email("hale0087@uni.sydney.edu.au")
                .build();

        AuthenticationRequest loginRequest = new AuthenticationRequest();
        loginRequest.setUsername("hale0087");
        loginRequest.setPassword("high_distinction_100");

        jwtToken = "mocked-jwt-token";

        Mockito.when(jwtService.generateToken(ArgumentMatchers.any(User.class)))
                .thenReturn(jwtToken);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @Order(1)
    void testUpdateValid() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setFirstName("Eistein");

        String content = objectMapper.writeValueAsString(userUpdateRequest);

        Mockito.when(userUpdateService.update(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(userResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/update/user/hale0087")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("hale0087@uni.sydney.edu.au"));

    }

    @Test
    @Order(2)
    void testUpdateInValid() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setFirstName("Eistein");

        String content = objectMapper.writeValueAsString(userUpdateRequest);

        Mockito.when(userUpdateService.update(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenThrow(new ValidationException("No editing right"));

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/update/user/hale00d7")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("No editing right"));

    }

    @Test
    @Order(3)
    @DirtiesContext
    void testUpdatePassword_Exception() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setOldPassword("high_distinction_100");
        updatePasswordRequest.setNewPassword("1");

        String content = objectMapper.writeValueAsString(updatePasswordRequest);

        Mockito.doThrow(new RuntimeException("Failed to update password"))
                .when(userUpdateService).updatePassword(ArgumentMatchers.anyString(), ArgumentMatchers.any(UpdatePasswordRequest.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/update/password/1")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(4)
    @DirtiesContext
    void testUpdatePassword() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setOldPassword("high_distinction_100");
        updatePasswordRequest.setNewPassword("VietnameseGang1@");

        String content = objectMapper.writeValueAsString(updatePasswordRequest);

        Mockito.when(userUpdateService.updatePassword(ArgumentMatchers.anyString(), ArgumentMatchers.any(UpdatePasswordRequest.class)))
                .thenReturn("Password updated successfully");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/update/password/1")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateAvatar() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "avatar.jpg",
                "image/jpeg",
                "image content".getBytes()
        );

        Mockito.when(userUpdateService.updateAvatar(Mockito.any(MockMultipartFile.class)))
                .thenReturn("https://s3.amazon.com/user-avatar/avatar.jpg");

        mockMvc.perform(multipart("/api/v1/update/avatar")
                .file(mockFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(request -> { request.setMethod("PUT"); return request; }))  ;
    }

    @Test
    public void testUpdateAvatar_InvalidFileType() throws Exception {

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "avatar.txt",
                "text/plain",
                "text content".getBytes()
        );

        Mockito.when(userUpdateService.updateAvatar(Mockito.any(MockMultipartFile.class)))
                .thenThrow(new IllegalArgumentException("File is not an image"));

        mockMvc.perform(multipart("/api/v1/update/avatar")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> { request.setMethod("PUT"); return request; }))
                .andExpect(status().isForbidden());
    }




}
