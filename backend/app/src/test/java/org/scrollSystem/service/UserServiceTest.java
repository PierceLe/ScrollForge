package org.scrollSystem.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class UserServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private String jwtToken;

    @MockBean
    private User mockUser;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @MockBean
    private  S3Service s3Service;

    @BeforeEach
    void initdata() throws Exception {
        mockUser = User.builder()
                .id(1)
                .username("hale0087")
                .email("hale0087@uni.sydney.edu.au")
                .firstName("Pierce")
                .lastName("Le")
                .role("ROLE_ADMIN")
                .phone("+61449510532")
                .password("54b908a8be404cb42e3f05400128704d0c7d760b42fb8a2a5049e359eb9b0050")
                .salt("vYDTe/LHlrhN2jOp7BypCg==")
                .avatarUrl(null)
                .build();

        userRepository.save(mockUser);

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
    @WithMockUser(username = "hale0087", roles = {"ADMIN"})
    public void testGetUsersSuccess() throws Exception {
        // Perform the GET request and verify the response
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].firstName").value("Pierce"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].lastName").value("Le"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].email").value("hale0087@uni.sydney.edu.au"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].username").value("hale0087"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].role").value("ROLE_ADMIN"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].phone").value("+61449510532"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].avatarUrl").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].firstName").value("hello"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].lastName").value("hi"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].email").value("c@gmail.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].username").value("t3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].role").value("ROLE_USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].phone").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].avatarUrl").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].id").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].firstName").value("hello"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].lastName").value("hi"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].email").value("s@gmail.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].username").value("11111111"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].role").value("ROLE_USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].phone").value("123456789"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].avatarUrl").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[3].id").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[3].firstName").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[3].lastName").value("Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[3].email").value("john.doe@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[3].username").value("john1234"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[3].role").value("ROLE_USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[3].phone").value("+1234567890"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[3].avatarUrl").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[4].id").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[4].firstName").value("Nhat Minh"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[4].lastName").value("Vuong"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[4].email").value("nvuo0694@uni.sydney.edu.au"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[4].username").value("nvuo0694"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[4].role").value("ROLE_USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[4].phone").value("123456789"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[4].avatarUrl").doesNotExist());
    }

    @Test
    @Order(2)
    @DirtiesContext
    @WithMockUser(username = "hale0087", roles = {"ADMIN"})
    public void deleteSuccessfully() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/users/nvuo0694")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("User with username nvuo0694 deleted successfully"));
    }

    @Test
    @Order(3)
    @WithMockUser(username = "hale0087", roles = {"ADMIN"})
    public void deleteFail() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/users/nvuo69a4fwrf")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User with username nvuo69a4fwrf not found"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").doesNotExist());
    }

    @Test
    @Order(4)
    @DirtiesContext
    @WithMockUser(username = "hale0087", roles = {"ADMIN"})
    public void checkExistingUsername() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/check-username?username=87")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("Ok"));
    }

    @Test
    @Order(5)
    @DirtiesContext
    @WithMockUser(username = "hale0087", roles = {"ADMIN"})
    public void checkExistingUsernameFail() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/users/check-username?username=hale0087")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Username is exist!"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").doesNotExist());
    }

//    @Test
//    @org.junit.jupiter.api.Order(4)
//    @WithMockUser(username = "hale0087", roles = {"ADMIN"})
//    public void getInfo() throws Exception {
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .get("/api/v1/users/info")
//                        .header("Authorization", "Bearer " + jwtToken)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }



}
