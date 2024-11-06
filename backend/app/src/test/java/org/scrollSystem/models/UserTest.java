package org.scrollSystem.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.scrollSystem.service.S3Service;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class UserTest {

    private User user;

    @Mock
    private UserDetails mockUserDetails;

    @MockBean
    private S3Service s3Service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize User with basic information
        user = User.builder()
                .id(1)
                .firstName("John")
                .lastName("Doe")
                .username("john.doe")
                .email("john.doe@example.com")
                .role("ROLE_USER")
                .password("password")
                .build();
    }

    @Test
    public void testIsAccountNonExpired() {
        when(mockUserDetails.isAccountNonExpired()).thenReturn(true);
        assertTrue(user.isAccountNonExpired(), "Account should be non-expired");
    }

    @Test
    public void testIsAccountNonLocked() {
        when(mockUserDetails.isAccountNonLocked()).thenReturn(true);
        assertTrue(user.isAccountNonLocked(), "Account should be non-locked");
    }

    @Test
    public void testIsCredentialsNonExpired() {
        when(mockUserDetails.isCredentialsNonExpired()).thenReturn(true);
        assertTrue(user.isCredentialsNonExpired(), "Credentials should be non-expired");
    }

    @Test
    public void testIsEnabled() {
        when(mockUserDetails.isEnabled()).thenReturn(true);
        assertTrue(user.isEnabled(), "Account should be enabled");
    }

    @Test
    public void testGetUploadNumberWhenNull() {
        assertEquals(0, user.getUploadNumber(), "Upload number should be 0 when it is null.");
    }

    @Test
    public void testGetUploadNumberWhenNotNull() {
        user.setUploadNumber(5);
        assertEquals(5, user.getUploadNumber(), "Upload number should be 5.");
    }

    @Test
    public void testGetDownloadNumberWhenNull() {
        assertEquals(0, user.getDownloadNumber(), "Download number should be 0 when it is null.");
    }

    @Test
    public void testGetDownloadNumberWhenNotNull() {
        user.setDownloadNumber(10);
        assertEquals(10, user.getDownloadNumber(), "Download number should be 10.");
    }
}


