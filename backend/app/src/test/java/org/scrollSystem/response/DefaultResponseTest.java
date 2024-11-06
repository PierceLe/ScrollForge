package org.scrollSystem.response;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultResponseTest {

    @Test
    public void testSuccessWithMessageAndData() {
        String message = "Operation successful";
        String data = "Sample Data";


        ResponseEntity<DefaultResponse<String>> responseEntity = DefaultResponse.success(message, data);

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

        ResponseEntity<DefaultResponse<Object>> responseEntity = DefaultResponse.error(errorMessage);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertFalse(responseEntity.getBody().getSuccess());
        assertEquals(errorMessage, responseEntity.getBody().getMessage());
        assertNull(responseEntity.getBody().getData());
    }

    @Test
    public void testErrorAccessDenyWithMessage() {
        String accessDenyMessage = "Access denied";

        ResponseEntity<DefaultResponse<Object>> responseEntity = DefaultResponse.errorAccessDeny(accessDenyMessage);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertFalse(responseEntity.getBody().getSuccess());
        assertEquals(accessDenyMessage, responseEntity.getBody().getMessage());
        assertNull(responseEntity.getBody().getData());
    }
}
