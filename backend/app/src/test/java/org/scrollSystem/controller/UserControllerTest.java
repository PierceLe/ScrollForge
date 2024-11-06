package org.scrollSystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.scrollSystem.config.security.JwtService;
import org.scrollSystem.exception.ValidationException;
import org.scrollSystem.models.User;
import org.scrollSystem.request.AuthenticationRequest;
import org.scrollSystem.request.UpdatePasswordRequest;
import org.scrollSystem.response.DefaultResponse;
import org.scrollSystem.service.S3Service;
import org.scrollSystem.service.UserService;
import org.scrollSystem.service.UserUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.scrollSystem.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.scrollSystem.models.User;
import org.scrollSystem.repository.UserRepository;
import org.scrollSystem.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private S3Service s3Service;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    private UserResponse userResponseSuccess;


    private String jwtToken;

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

        AuthenticationRequest loginRequest = new AuthenticationRequest();
        loginRequest.setUsername("hale0087");
        loginRequest.setPassword("high_distinction_100");

        jwtToken = "mocked-jwt-token";

        Mockito.when(jwtService.generateToken(ArgumentMatchers.any(User.class)))
                .thenReturn(jwtToken);

        //mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        userRepository.save(User.builder()
                .id(2)
                .username("t3")
                .email("c@gmail.com")
                .firstName("hello")
                .lastName("hi")
                .role("ROLE_USER")
                .phone(null)
                .avatarUrl(null)
                .build());

        userRepository.save(User.builder()
                .id(3)
                .username("11111111")
                .email("s@gmail.com")
                .firstName("hello")
                .lastName("hi")
                .role("ROLE_USER")
                .phone("123456789")
                .password("867425e1b76b417afac933242947bf63a0f178422e360bb70f5ee2e75f7b6922")
                .salt("a")
                .avatarUrl(null)
                .build());

        userRepository.save(User.builder()
                .id(4)
                .username("john1234")
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .role("ROLE_USER")
                .phone("+1234567890")
                .password("867425e1b76b417afac933242947bf63a0f178422e360bb70f5ee2e75f7b6922")
                .salt("a")
                .avatarUrl(null)
                .build());

        userRepository.save(User.builder()
                .id(5)
                .username("nvuo0694")
                .email("nvuo0694@uni.sydney.edu.au")
                .firstName("Nhat Minh")
                .lastName("Vuong")
                .role("ROLE_USER")
                .phone("123456789")
                .password("867425e1b76b417afac933242947bf63a0f178422e360bb70f5ee2e75f7b6922")
                .salt("a")
                .avatarUrl(null)
                .build());

        userResponseSuccess = UserResponse.builder()
                .id(1)
                .firstName("Pierce")
                .lastName("Le")
                .email("hale0087@uni.sydney.edu.au")
                .username("hale0087")
                .role("ROLE_ADMIN")
                .phone("0449510532")
                .avatarUrl(null)
                .build();
    }
    @Test
    @Order(1)
    @WithMockUser(username = "hale0087", roles = {"ADMIN"})
    public void testGetUsersSuccess() throws Exception {
        // Perform the GET request and verify the response
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    @Order(2)
    @WithMockUser(username = "hale0087", roles = {"ADMIN"})
    public void deleteSuccessfully() throws Exception {

        Mockito.when(userService.delete(ArgumentMatchers.anyString())).thenReturn("User with username nvuo0694 deleted successfully");

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/users/nvuo0694")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    @Order(3)
    @WithMockUser(username = "hale0087", roles = {"ADMIN"})
    public void deleteFail() throws Exception {

        Mockito.when(userService.delete(ArgumentMatchers.any())).thenThrow(new EntityNotFoundException("User with username hihi not found"));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/users/hihi")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Order(4)
    @WithMockUser(username = "hale0087", roles = {"ADMIN"})
    public void register() throws Exception {

        Mockito.when(userService.getInfo()).thenReturn(userResponseSuccess);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/info")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(3)
    @WithMockUser(username = "hale0087", roles = {"ADMIN"})
    public void checkExistingUsername() throws Exception {
        Mockito.when(userService.checkExistingUsername(ArgumentMatchers.anyString())).thenReturn("Ok");

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/check-username?username=87")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(4)
    @WithMockUser(username = "hale0087", roles = {"ADMIN"})
    public void checkExistingUsernameFail() throws Exception {
        Mockito.when(userService.checkExistingUsername(ArgumentMatchers.anyString())).thenThrow(new ValidationException("Username is exist!"));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/check-username?username=hale0087")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }



}