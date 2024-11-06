package org.scrollSystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.scrollSystem.utility.EnvLoader;
import org.springframework.boot.SpringApplication;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

public class AppTest {

    private App app;

    @BeforeEach
    public void setUp() {
        app = new App();
    }

    @Test
    public void testGetGreeting() {
        assertEquals("Hello World", app.getGreeting(), "getGreeting() should return 'Hello World'");
    }

    @Test
    public void testMainMethodEnvLoader() {
        Map<String, String> mockEnv = new HashMap<>();
        mockEnv.put("AWS_ACCESS_KEY", "mock_access_key");
        mockEnv.put("AWS_SECRET_KEY", "mock_secret_key");
        try (MockedStatic<EnvLoader> mockedEnvLoader = Mockito.mockStatic(EnvLoader.class);
             MockedStatic<SpringApplication> mockedSpringApplication = mockStatic(SpringApplication.class)) {

            mockedEnvLoader.when(() -> EnvLoader.loadEnv(".env")).thenReturn(mockEnv);

            mockedSpringApplication.when(() -> SpringApplication.run(App.class, new String[]{}))
                    .thenReturn(null);

            App.main(new String[]{});

            assertEquals("mock_access_key", System.getProperty("AWS_ACCESS_KEY"));
            assertEquals("mock_secret_key", System.getProperty("AWS_SECRET_KEY"));
        }
    }
}
