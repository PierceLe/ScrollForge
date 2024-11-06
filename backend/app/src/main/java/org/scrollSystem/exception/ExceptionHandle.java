package org.scrollSystem.exception;

import org.scrollSystem.response.DefaultResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionHandle {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<DefaultResponse<Object>> exception(Exception exception) {
        if (exception instanceof ClientAbortException) {
            return null;
        }
        log.error("exception: ", exception);
        return DefaultResponse.error("Having errors, please try again in another time");
    }
}
