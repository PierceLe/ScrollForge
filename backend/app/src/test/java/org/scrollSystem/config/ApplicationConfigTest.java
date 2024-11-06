package org.scrollSystem.config;

import org.junit.jupiter.api.Test;
import org.scrollSystem.App;
import org.scrollSystem.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
public class ApplicationConfigTest
{

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApplicationConfig applicationConfig;
    @Autowired
    private App app;

    @MockBean
    private S3Service s3Service;

    @Test
    public void testEncode()
    {
        String rawPassword = "12345";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
    }

    @Test
    public void testEncodeCorrect()
    {
        String rawPassword = "12345";
        String salt = "@";
        String encodedPassword = passwordEncoder.encode(rawPassword + salt);
        assertEquals(encodedPassword, "ca20cffd89c01dd095d145f54aa6a2bdb4aead6eaefc1f32d573568659ae8278");
    }

    @Test
    public void testSaltGenerate()
    {
        byte[] salt = applicationConfig.getNextSalt();
        assertNotNull(salt);
    }

    @Test
    public void testMatches()
    {
        passwordEncoder.matches("a", "b");
    }

    @Test
    public void testException()
    {
        assertThrows(RuntimeException.class, () -> {
            passwordEncoder.encode(null);
        });
    }

}