package org.scrollSystem.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.scrollSystem.config.ApplicationConfig;
import org.scrollSystem.config.security.JwtService;
import org.scrollSystem.exception.ValidationException;
import org.scrollSystem.models.User;
import org.scrollSystem.repository.UserRepository;
import org.scrollSystem.request.AuthenticationRequest;
import org.scrollSystem.request.RegisterRequest;
import org.scrollSystem.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class UserAuthenticationServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ApplicationConfig applicationConfig;

    @MockBean
    private S3Service s3Service;

    @MockBean
    private User user;


    private AuthenticationRequest authenticationRequest;

    private RegisterRequest registerRequestSuccess;

    private RegisterRequest registerRequestFailPass;
    private RegisterRequest registerRequestFailEmail;
    private RegisterRequest registerRequestFailUsername;

    @BeforeEach
    void initdata() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        userRepository.save(User.builder()
                .id(1)
                .username("hale0087")
                .email("hale0087@uni.sydney.edu.au")
                .firstName("Pierce")
                .lastName("Le")
                .role("ROLE_ADMIN")
                .phone("+61449510532")
                .password("f707fdda7c874ff49ebfb2c88a2860c5ff4ce3d94a21efb76566ad0f92c9ad57") //123456
                .salt("a")
                .avatarUrl(null)
                .build());

        userRepository.save(User.builder()
                .id(2)
                .username("qngu0806")
                .email("qngu0806@uni.sydney.edu.au")
                .firstName("Quang Huy")
                .lastName("Nguyen")
                .role("ROLE_USER")
                .phone("+61411591768")
                .password("f707fdda7c874ff49ebfb2c88a2860c5ff4ce3d94a21efb76566ad0f92c9ad57") //123456
                .salt("a")
                .avatarUrl(null)
                .build());

    }

    @Test
    @Order(1)
    void testRegisterSuccess() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        registerRequestSuccess = RegisterRequest.builder()
                .firstName("Nhat Minh")
                .lastName("Vuong")
                .email("nvuo0694@uni.sydney.edu.au")
                .username("nvuo0694")
                .password("LongBien1@")
                .phone("0413688993")
                .build();

        String content = objectMapper.writeValueAsString(registerRequestSuccess);

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

        registerRequestFailPass = RegisterRequest.builder()
                .firstName("Nhat Minh")
                .lastName("Vuong")
                .email("nvuo0694@uni.sydney.edu.au")
                .password("1")
                .username("nvuo0694")
                .phone("0413688993")
                .build();

        String content = objectMapper.writeValueAsString(registerRequestFailPass);

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

        registerRequestFailEmail = RegisterRequest.builder()
                .firstName("Nhat Minha")
                .lastName("Vuonga")
                .email("qngu0806@uni.sydney.edu.au")
                .password("LongBien1@")
                .username("nvua0694")
                .phone("0413688994")
                .build();

        String content = objectMapper.writeValueAsString(registerRequestFailEmail);

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

        registerRequestFailUsername = RegisterRequest.builder()
                .firstName("Nhat Minha")
                .lastName("Vuonga")
                .email("qngg0806@uni.sydney.edu.au")
                .password("LongBien1@")
                .username("qngu0806")
                .phone("0413688995")
                .build();

        String content = objectMapper.writeValueAsString(registerRequestFailUsername);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void testLoginSucess() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        authenticationRequest = AuthenticationRequest.builder()
                .username("hale0087")
                .password("123456")
                .build();
        String content = objectMapper.writeValueAsString(authenticationRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testLoginFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        authenticationRequest = AuthenticationRequest.builder()
                .username("hale0087")
                .password("12345")
                .build();
        String content = objectMapper.writeValueAsString(authenticationRequest);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }


}
