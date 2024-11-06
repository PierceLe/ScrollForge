package org.scrollSystem.request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?!\\s*$).*$", message = "First name must contain only letters")
    String firstName;

    @Pattern(regexp = "^(?=.*[a-zA-Z])(?!\\s*$).*$", message = "Last name must contain only letters")
    String lastName;

    @Email(message = "Invalid email format")
    String email;

    @Size(max = 8, message = "Your customisable ID key must contain exactly 8 characters")
    @Size(min = 8, message = "Your customisable ID key must contain exactly 8 characters")
    @Pattern(regexp = "^[a-z0-9]+", message = "Your new customisable ID key must only contain digits and letters.")
    String username;


    @Pattern(regexp = "^[+]?[0-9]{10,13}$", message = "Invalid phone number")
    String phoneNumber;



}
