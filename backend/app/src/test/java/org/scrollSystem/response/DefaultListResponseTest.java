package org.scrollSystem.response;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultListResponseTest {

    @Test
    public void testSuccessWithData() {
        List<String> data = Arrays.asList("Item1", "Item2", "Item3");

        ResponseEntity<DefaultListResponse<String>> responseEntity = DefaultListResponse.success(data);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().getSuccess());
        assertEquals(data, responseEntity.getBody().getData());
        assertNull(responseEntity.getBody().getMessage());  // No message in this variant
    }

    @Test
    public void testSuccessWithMessageAndData() {
        String message = "Operation successful";
        List<String> data = Arrays.asList("Item1", "Item2", "Item3");

        ResponseEntity<DefaultListResponse<String>> responseEntity = DefaultListResponse.success(message, data);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody().getSuccess());
        assertEquals(message, responseEntity.getBody().getMessage());
        assertEquals(data, responseEntity.getBody().getData());
    }

    @Test
    public void testErrorWithMessage() {
        String errorMessage = "An error occurred";

        ResponseEntity<DefaultListResponse<Object>> responseEntity = DefaultListResponse.error(errorMessage);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertFalse(responseEntity.getBody().getSuccess());
        assertEquals(errorMessage, responseEntity.getBody().getMessage());
        assertNull(responseEntity.getBody().getData());  // No data in error response
    }
}
