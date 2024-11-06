package org.scrollSystem.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRequest {
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "first name must contain only alphabetic characters")
    private String firstName;

    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "last name must contain only alphabetic characters")
    private String lastName;

    @Email(message = "email is not in correct form")
    private String email;

    @Size(max=8, message = "user name must be no longer than 8 character")
    private String username;
    private String phone;

    @Size(min = 8, message = "password must be at least 8 characters long")
    @Size(max = 16, message = "password must be no longer than 16 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_\\s\\t]).{8,16}$",
        message = "password must contain at least one digit, one uppercase and lowercase letter, one special character, and may include spaces or tabs")
    private String password;
}