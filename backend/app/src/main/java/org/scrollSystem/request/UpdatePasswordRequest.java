package org.scrollSystem.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String oldPassword;
    @NotNull(message = "Password can not be empty")
    @NotBlank(message = "Password can not be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Size(max = 16, message = "Password must be no longer than 16 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_\\s\\t]).{8,16}$",
            message = "Password must contain at least one digit, one uppercase and lowercase letter, one special character, and may include spaces or tabs")
    private String newPassword;
}
