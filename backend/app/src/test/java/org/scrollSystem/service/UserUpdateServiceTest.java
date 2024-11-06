package org.scrollSystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.scrollSystem.config.security.JwtService;
import org.scrollSystem.exception.ValidationException;
import org.scrollSystem.models.User;
import org.scrollSystem.request.AuthenticationRequest;
import org.scrollSystem.request.UpdatePasswordRequest;
import org.scrollSystem.request.UserUpdateRequest;
import org.scrollSystem.response.DefaultResponse;
import org.scrollSystem.service.S3Service;
import org.scrollSystem.service.UserUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.scrollSystem.repository.UserRepository;

import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@Transactional
public class UserUpdateServiceTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private S3Service s3Service;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;


    @Autowired
    private UserUpdateService userUpdateService;

    private User mockUser;
//    private User mockUser2;




    private String jwtToken;

    @BeforeEach
    void initdata() throws Exception {
        mockUser = User.builder()
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

        userRepository.save(mockUser);

//        mockUser2 = User.builder()
//                .id(1)
//                .username("noname87")
//                .email("noname@uni.sydney.edu.au")
//                .firstName("Pierce")
//                .lastName("Le")
//                .role("ROLE_USER")
//                .phone("+61449510532")
//                .password("54b908a8be404cb42e3f05400128704d0c7d760b42fb8a2a5049e359eb9b0050")
//                .salt("vYDTe/LHlrhN2jOp7BypCg==")
//                .avatarUrl(null)
//                .build();
//
//        userRepository.save(mockUser2);

        AuthenticationRequest loginRequest = new AuthenticationRequest();
        loginRequest.setUsername("hale0087");
        loginRequest.setPassword("high_distinction_100");

        ObjectMapper objectMapper = new ObjectMapper();
        String loginContent = objectMapper.writeValueAsString(loginRequest);

        // Perform login and extract the JWT token
        jwtToken = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(loginContent))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data.token").exists()) // Check if token exists
                .andReturn()
                .getResponse()
                .getContentAsString()
                .split("\"token\":\"")[1]  // Extract token from JSON response
                .split("\"")[0];

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        SecurityContextHolder.setContext(securityContext);




    }

    @Test
    @Order(1)
    void testUpdateFirstNameValid() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setFirstName("Eistein");

        String content = objectMapper.writeValueAsString(userUpdateRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/update/user/hale0087")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.firstName").value("Eistein"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("hale0087@uni.sydney.edu.au"));

    }

    @Test
//    @Order(2)
    void testUpdateLastNameValid() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setLastName("Albert");

        String content = objectMapper.writeValueAsString(userUpdateRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/update/user/hale0087")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lastName").value("Albert"));

    }

    @Test
//    @Order(2)
    void testUpdateEmailValid() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setEmail("newemail@gmail.com");

        String content = objectMapper.writeValueAsString(userUpdateRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/update/user/hale0087")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.email").value("newemail@gmail.com"));

    }

    //    @Order(2)
    @Test
    void testUpdateUserNameValid() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setUsername("noname23");

        String content = objectMapper.writeValueAsString(userUpdateRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/update/user/hale0087")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value("noname23"));

    }

    @Test
    void testUpdatePhoneValid() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setPhoneNumber("+641231233546");

        String content = objectMapper.writeValueAsString(userUpdateRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/update/user/hale0087")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true));

    }

    @Test
    void testUpdateUserNameInValid() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setUsername("hale0087");

        String content = objectMapper.writeValueAsString(userUpdateRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/update/user/hale0087")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("The new username is already taken"));

    }



    @Test
//    @Order(2)
    void testUpdateInValid() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setFirstName("Eistein");

        String content = objectMapper.writeValueAsString(userUpdateRequest);

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
    public void testUpdatePassword() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setOldPassword("high_distinction_100");
        updatePasswordRequest.setNewPassword("VietnameseGang1@");

        String content = objectMapper.writeValueAsString(updatePasswordRequest);


        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/update/password/1")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUpdatePassword_UserIdMismatch() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setOldPassword("high_distinction_100");
        updatePasswordRequest.setNewPassword("1");

        String content = objectMapper.writeValueAsString(updatePasswordRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/update/password/2")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isForbidden()) ;

    }

    @Test
    public void testUpdatePassword_IncorrectOldPassword() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setOldPassword("wrong_old_password");
        updatePasswordRequest.setNewPassword("new_password");

        String content = objectMapper.writeValueAsString(updatePasswordRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/update/password/1")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void testUpdateAvatar_Success() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "avatar.jpg", "image/jpeg", "image content".getBytes()
        );

        String expectedFileUrl = "https://s3.amazon.com/user-avatar/avatar.jpg";
        Mockito.when(s3Service.uploadFile(Mockito.any(MockMultipartFile.class)))
                .thenReturn(expectedFileUrl);

        String result = userUpdateService.updateAvatar(mockFile);
    }

    @Test
    void testUpdateAvatar_InvalidFileType() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "document.txt", "text/plain", "file content".getBytes()
        );

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userUpdateService.updateAvatar(mockFile);
        });
    }








}