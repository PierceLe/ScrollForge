package org.scrollSystem.request;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotNull(message = "First name can not be empty")
    @NotBlank(message = "First name can not be empty")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "First name must contain only letters")
    private String firstName;

    @NotNull(message = "Last name can not be empty")
    @NotBlank(message = "Last name can not be empty")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "Last name must contain only letters")
    private String lastName;

    @Email(message = "Email is not in the correct format")
    @NotNull(message = "Email can not be empty")
    @NotBlank(message = "Email can not be empty")
    private String email;

    @NotNull(message = "Password can not be empty")
    @NotBlank(message = "Password can not be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Size(max = 16, message = "Password must be no longer than 16 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_\\s\\t]).{8,16}$",
            message = "Password must contain at least one digit, one uppercase and lowercase letter, one special character, and may include spaces or tabs")
    private String password;

    @NotNull(message = "Your customisable ID key must contain exactly 8 characters")
    @NotBlank(message = "Your customisable ID key must contain exactly 8 characters")
    @Size(max = 8, message = "Your customisable ID key must contain exactly 8 characters")
    @Size(min = 8, message = "Your customisable ID key must contain exactly 8 characters")
    @Pattern(regexp = "^[a-z0-9]+", message = "Your new customisable ID key must only contain digits and letters.")
    private String username;

    @NotNull(message = "Phone number can not be empty")
    @NotBlank(message = "Phone number can not be empty")
    @Pattern(regexp = "^[+]?[0-9]{10,13}$", message = "Invalid phone number")
    private String phone;
    private String salt;

}
