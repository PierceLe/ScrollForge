package org.scrollSystem.exception;

import org.apache.catalina.connector.ClientAbortException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.scrollSystem.response.DefaultResponse;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

public class ExceptionHandleTest {

    @Mock
    private Logger mockLogger;

    @InjectMocks
    private ExceptionHandle exceptionHandle;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testClientAbortException() {
        Exception clientAbortException = Mockito.mock(ClientAbortException.class);
        ResponseEntity<DefaultResponse<Object>> response = exceptionHandle.exception(clientAbortException);
        assertNull(response, "Response should be null for ClientAbortException");
    }


}
