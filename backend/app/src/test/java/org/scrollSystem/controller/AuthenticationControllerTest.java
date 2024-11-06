package org.scrollSystem.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.scrollSystem.exception.ValidationException;
import org.scrollSystem.models.User;
import org.scrollSystem.repository.UserRepository;
import org.scrollSystem.request.AuthenticationRequest;
import org.scrollSystem.request.RegisterRequest;
import org.scrollSystem.response.AuthenticationResponse;
import org.scrollSystem.response.DefaultResponse;
import org.scrollSystem.response.UserResponse;
import org.scrollSystem.service.S3Service;
import org.scrollSystem.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAuthenticationService userAuthenticationService;

    @MockBean
    private S3Service s3Service;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private RegisterRequest registerRequest;
    private AuthenticationResponse authenticationResponse;

    private RegisterRequest registerRequestSuccess;
    private RegisterRequest registerRequestFailPass;

    private RegisterRequest registerRequestFailEmail;

    private UserResponse userResponseRegisterSuccess;

    private RegisterRequest registerRequestFailUsername;

    private DefaultResponse defaultResponseError;
    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void initData()
    {
        registerRequestSuccess = RegisterRequest.builder()
                .firstName("Nhat Minh")
                .lastName("Vuong")
                .email("nvuo0694@uni.sydney.edu.au")
                .username("nvuo0694")
                .password("LongBien1@")
                .phone("0413688993")
                .build();

        userRepository.save(User.builder()
                .id(1)
                .username("hale0087")
                .email("hale0087@uni.sydney.edu.au")
                .firstName("Pierce")
                .lastName("Le")
                .role("ROLE_ADMIN")
                .phone("+61449510532")
                .build());

        userRepository.save(User.builder()
                .id(2)
                .username("qngu0806")
                .email("qngu0806@uni.sydney.edu.au")
                .firstName("Quang Huy")
                .lastName("Nguyen")
                .role("ROLE_USER")
                .phone("+61411591768")
                .build());

        registerRequestFailPass = RegisterRequest.builder()
                .firstName("Nhat Minh")
                .lastName("Vuong")
                .email("nvuo0694@uni.sydney.edu.au")
                .password("1")
                .username("nvuo0694")
                .phone("0413688993")
                .build();

        registerRequestFailEmail = RegisterRequest.builder()
                .firstName("Nhat Minha")
                .lastName("Vuonga")
                .email("qngu0806@uni.sydney.edu.au")
                .password("LongBien1@")
                .username("nvua0694")
                .phone("0413688994")
                .build();

        registerRequestFailUsername = RegisterRequest.builder()
                .firstName("Nhat Minha")
                .lastName("Vuonga")
                .email("qngg0806@uni.sydney.edu.au")
                .password("LongBien1@")
                .username("qngu0806")
                .phone("0413688995")
                .build();

        userResponseRegisterSuccess = UserResponse.builder()
                .id(3)
                .firstName("Nhat Minh")
                .lastName("Vuong")
                .email("nvuo0694@uni.sydney.edu.au")
                .username("nvuo0694")
                .role("ROLE_USER")
                .phone("0413688993")
                .build();

        defaultResponseError = DefaultResponse.builder()
                        .success(false)
                        .message("Password must be at least 8 characters long")
                        .build();



        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    @Order(1)
    void testRegisterSuccess() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(registerRequestSuccess);

        Mockito.when(userAuthenticationService.register(ArgumentMatchers.any())).thenReturn(userResponseRegisterSuccess);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(2)
    void testRegisterFailPass() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(registerRequestFailPass);

        Mockito.when(userAuthenticationService.register(ArgumentMatchers.any()))
                .thenThrow(new RuntimeException("Password must be at least 8 characters long"));


        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Order(3)
    void testRegisterFailEmail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(registerRequestFailEmail);

        Mockito.when(userAuthenticationService.register(ArgumentMatchers.any()))
                .thenThrow(new ValidationException("Email have been existed"));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Order(4)
    void testRegisterFailUsername() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(registerRequestFailUsername);

        Mockito.when(userAuthenticationService.register(ArgumentMatchers.any()))
                .thenThrow(new ValidationException("Username have been existed"));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

}


